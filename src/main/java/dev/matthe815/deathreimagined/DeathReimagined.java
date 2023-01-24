package dev.matthe815.deathreimagined;

import dev.matthe815.deathreimagined.api.PlayerData;
import dev.matthe815.deathreimagined.gui.DyingUI;
import dev.matthe815.deathreimagined.items.ItemSyringe;
import dev.matthe815.deathreimagined.networking.PlayerDyingStatusPacket;
import dev.matthe815.deathreimagined.networking.PlayerHelpRespawnPacket;
import dev.matthe815.deathreimagined.networking.PlayerRespawnPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DeathReimagined.MODID)
public class DeathReimagined {
    public static final String MODID = "deathreimagined";

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean isDying = false;
    public static int dyingTick = 0;

    public static final Item SYRINGE = new ItemSyringe();

    public static final SimpleChannel network = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, "deathreimagined"))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    public DeathReimagined() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        setupNetworking();
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new DyingUI());
        network.registerMessage(0, PlayerDyingStatusPacket.class, PlayerDyingStatusPacket::encode, PlayerDyingStatusPacket::decode, PlayerDyingStatusPacket.Handler::handle);
    }

    private void setupNetworking() {
        int index = 1;
        network.registerMessage(index++, PlayerRespawnPacket.class, PlayerRespawnPacket::encode, PlayerRespawnPacket::decode, PlayerRespawnPacket.Handler::handle);
        network.registerMessage(index++, PlayerHelpRespawnPacket.class, PlayerHelpRespawnPacket::encode, PlayerHelpRespawnPacket::decode, PlayerHelpRespawnPacket.Handler::handle);
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerData.AddData((ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerData.RemoveData(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.world.isRemote) {
            if (isDying && dyingTick > 0) dyingTick--; // Simulate the server's environment on the client.
            return;
        }

        PlayerData data = PlayerData.GetData(event.player);
        data.OnTick();
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.EntityInteract event)
    {
        // Only if you right click another player
        if (!(event.getEntity() instanceof PlayerEntity)) return;

        DeathReimagined.network.sendToServer(
                new PlayerHelpRespawnPacket(((PlayerEntity)event.getEntity()).getName().getString()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntity().world.isRemote) return; // This should only run on servers
        if (!(event.getEntity() instanceof PlayerEntity)) return; // Only react to player deaths

        PlayerEntity player = (PlayerEntity)event.getEntity();

        // Perform normal death behaviour if respawning.
        if (PlayerData.GetData(player).IsRespawning()) {
            PlayerData.GetData(player).SetRespawning(false);
            return;
        }

        event.setCanceled(true); // Stop the actual death
        player.setHealth(0.5f); // It won't stop if this isn't set.

        if (PlayerData.GetData(player).IsDying()) return; // We don't want to reset the number while dying.
        PlayerData.GetData(player).OnDeath();
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemRegister(final RegistryEvent.Register<Item> itemRegistryEvent) {
            itemRegistryEvent.getRegistry().register(SYRINGE);
        }
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}

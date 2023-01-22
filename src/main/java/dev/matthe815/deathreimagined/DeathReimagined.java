package dev.matthe815.deathreimagined;

import dev.matthe815.deathreimagined.api.PlayerData;
import dev.matthe815.deathreimagined.gui.DyingUI;
import dev.matthe815.deathreimagined.networking.PlayerDyingStatusPacket;
import dev.matthe815.deathreimagined.networking.PlayerRespawnPacket;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DeathReimagined.MODID)
public class DeathReimagined {
    public static final String MODID = "deathreimagined";

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean isDying = false;
    public static int dyingTick = 0;

    public static final SimpleChannel network = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, "deathreimagined"))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    public DeathReimagined() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DyingUI());
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        setupNetworking();
    }

    private void setupNetworking() {
        int index = 0;
        network.registerMessage(index++, PlayerDyingStatusPacket.class, PlayerDyingStatusPacket::encode, PlayerDyingStatusPacket::decode, PlayerDyingStatusPacket.Handler::handle);
        network.registerMessage(index++, PlayerRespawnPacket.class, PlayerRespawnPacket::encode, PlayerRespawnPacket::decode, PlayerRespawnPacket.Handler::handle);
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
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntity().world.isRemote) return; // This should only run on servers
        if (!(event.getEntity() instanceof PlayerEntity)) return; // Only react to player deaths

        PlayerEntity player = (PlayerEntity)event.getEntity();
        PlayerData.GetData(player).OnDeath();

        event.setCanceled(true); // Stop the actual death
        player.setHealth(0.5f); // It won't stop if this isn't set.

        System.out.println("Player is dying");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}

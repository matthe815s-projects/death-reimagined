package dev.matthe815.deathreimagined;

import dev.matthe815.deathreimagined.api.LocalPlayerData;
import dev.matthe815.deathreimagined.api.PlayerData;
import dev.matthe815.deathreimagined.events.Events;
import dev.matthe815.deathreimagined.gui.DyingUI;
import dev.matthe815.deathreimagined.items.ItemSyringe;
import dev.matthe815.deathreimagined.networking.NetworkManager;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DeathReimagined.MODID)
public class DeathReimagined {
    public static final String MODID = "deathreimagined";

    private static final Logger LOGGER = LogManager.getLogger();

    public static LocalPlayerData LOCAL_DATA;

    public static final Item SYRINGE = new ItemSyringe();

    /**
     * Handles network related events for the mod.
     */
    public static final NetworkManager NETWORK = new NetworkManager();

    public DeathReimagined() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Events());
    }

    private void setup(final FMLCommonSetupEvent event) {
        setupNetworking();
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        DeathReimagined.LOCAL_DATA = new LocalPlayerData();
        MinecraftForge.EVENT_BUS.register(new DyingUI());   // Register the UI for rendering
        NetworkManager.RegisterClient();
    }

    private void setupNetworking() {
        NetworkManager.RegisterCommon();
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

package dev.matthe815.deathreimagined.networking;

import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.networking.packets.PlayerDyingStatusPacket;
import dev.matthe815.deathreimagined.networking.packets.PlayerHelpRespawnPacket;
import dev.matthe815.deathreimagined.networking.packets.PlayerRespawnPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkManager {
    public static final String NETWORK_PROTOCOL = "1";

    private static int REGISTER_INDEX = 1;

    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(DeathReimagined.MODID, "deathreimagined"))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> NetworkManager.NETWORK_PROTOCOL)
            .simpleChannel();

    public static void RegisterClient ()
    {
        HANDLER.registerMessage(0, PlayerDyingStatusPacket.class, PlayerDyingStatusPacket::encode, PlayerDyingStatusPacket::decode, PlayerDyingStatusPacket.Handler::handle);
    }

    public static void RegisterCommon ()
    {
        HANDLER.registerMessage(REGISTER_INDEX++, PlayerRespawnPacket.class, PlayerRespawnPacket::encode, PlayerRespawnPacket::decode, PlayerRespawnPacket.Handler::handle);
        HANDLER.registerMessage(REGISTER_INDEX++, PlayerHelpRespawnPacket.class, PlayerHelpRespawnPacket::encode, PlayerHelpRespawnPacket::decode, PlayerHelpRespawnPacket.Handler::handle);
    }
}

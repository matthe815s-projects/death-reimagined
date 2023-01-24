package dev.matthe815.deathreimagined.networking;

import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.networking.packets.PlayerDyingStatusPacket;
import dev.matthe815.deathreimagined.networking.packets.PlayerHelpRespawnPacket;
import dev.matthe815.deathreimagined.networking.packets.PlayerRespawnPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.lwjgl.system.windows.MSG;

public class NetworkManager {
    public static final String NETWORK_PROTOCOL = "1";

    private static int REGISTER_INDEX = 0;

    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(DeathReimagined.MODID, "deathreimagined"))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> NetworkManager.NETWORK_PROTOCOL)
            .simpleChannel();

    public static void RegisterClient ()
    {
        HANDLER.registerMessage(REGISTER_INDEX, PlayerDyingStatusPacket.class, PlayerDyingStatusPacket::encode, PlayerDyingStatusPacket::decode, PlayerDyingStatusPacket.Handler::handle);
    }

    public static void RegisterCommon ()
    {
        HANDLER.registerMessage(REGISTER_INDEX++, PlayerRespawnPacket.class, PlayerRespawnPacket::encode, PlayerRespawnPacket::decode, PlayerRespawnPacket.Handler::handle);
        HANDLER.registerMessage(REGISTER_INDEX++, PlayerHelpRespawnPacket.class, PlayerHelpRespawnPacket::encode, PlayerHelpRespawnPacket::decode, PlayerHelpRespawnPacket.Handler::handle);
    }

    /**
     * Send a message from the client directly to the server.
     * This can only be performed on the client.
     * @param packet
     */
    @OnlyIn(Dist.CLIENT)
    public static void SendToServer (MSG packet)
    {
        HANDLER.sendToServer(packet);
    }

    /**
     * Sends a message from the server to a client.
     * This can only be performed on the server.
     * @param packet
     * @param player
     */
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void SendToClient (MSG packet, ServerPlayerEntity player)
    {
        HANDLER.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_SERVER);
    }

    /**
     * Send a message from the server to all clients connected.
     * This can only be performed on the server.
     * @param packet
     */
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void Broadcast (MSG packet)
    {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            SendToClient(packet, serverPlayerEntity);
        });
    }
}

package dev.matthe815.deathreimagined.networking;

import dev.matthe815.deathreimagined.api.PlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerHelpRespawnPacket {
    public String target;

    public PlayerHelpRespawnPacket(String target)
    {
        this.target = target;
    }

    public static PlayerHelpRespawnPacket decode(PacketBuffer buf)
    {
        return new PlayerHelpRespawnPacket(buf.readString());
    }

    public static void encode(PlayerHelpRespawnPacket msg, PacketBuffer buf)
    {
        buf.writeString(msg.target);
    }

    public static class Handler
    {
        public static void handle(final PlayerHelpRespawnPacket pkt, Supplier<NetworkEvent.Context> ctx)
        {
            if (!PlayerData.GetData( pkt.target ).IsDying()) return;

            PlayerData.GetData( pkt.target ).OnRespawn(false);
            ctx.get().setPacketHandled(true);
        }
    }
}

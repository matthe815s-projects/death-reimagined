package dev.matthe815.deathreimagined.networking;

import dev.matthe815.deathreimagined.api.PlayerData;
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
        System.out.println(msg.target);
        buf.writeString(msg.target);
    }

    public static class Handler
    {
        public static void handle(final PlayerHelpRespawnPacket pkt, Supplier<NetworkEvent.Context> ctx)
        {
            System.out.println("Respawn attempt");
            System.out.println(pkt.target);
            if (!PlayerData.GetData( pkt.target ).IsDying()) return;
            System.out.println("Respawning");

            PlayerData.GetData( pkt.target ).OnRespawn(false);
            ctx.get().setPacketHandled(true);
        }
    }
}

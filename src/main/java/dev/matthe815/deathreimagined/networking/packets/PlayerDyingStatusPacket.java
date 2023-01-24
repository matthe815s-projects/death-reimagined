package dev.matthe815.deathreimagined.networking.packets;

import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.gui.DyingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerDyingStatusPacket {
    public boolean isDying;
    public int dyingTick;

    public PlayerDyingStatusPacket(boolean isDying, int dyingTick)
    {
        this.isDying = isDying;
        this.dyingTick = dyingTick;
    }

    public static PlayerDyingStatusPacket decode(PacketBuffer buf)
    {
        return new PlayerDyingStatusPacket(buf.readBoolean(), buf.readInt());
    }

    public static void encode(PlayerDyingStatusPacket msg, PacketBuffer buf)
    {
        buf.writeBoolean(msg.isDying);
        buf.writeInt(msg.dyingTick);
    }

    public static class Handler
    {
        public static void handle(final PlayerDyingStatusPacket pkt, Supplier<NetworkEvent.Context> ctx)
        {
            DeathReimagined.LOCAL_DATA.OnStatus(pkt.isDying, pkt.dyingTick);

            // Set or reset the effects of dying when you start dying
            if (pkt.isDying) DeathReimagined.LOCAL_DATA.OnDeath();
            else DeathReimagined.LOCAL_DATA.OnRespawn();

            ctx.get().setPacketHandled(true);
        }
    }
}

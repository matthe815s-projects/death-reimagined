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
            DeathReimagined.isDying = pkt.isDying;
            DeathReimagined.dyingTick = pkt.dyingTick;

            // Set the effects of dying when you start dying
            if (pkt.isDying) {
                Minecraft.getInstance().displayGuiScreen(new DyingScreen());
                Minecraft.getInstance().player.startSleeping(Minecraft.getInstance().player.getPosition());
            } else { // Reset the effects
                Minecraft.getInstance().currentScreen.closeScreen();
                Minecraft.getInstance().player.stopSleepInBed(true, true);
            }

            ctx.get().setPacketHandled(true);
        }
    }
}

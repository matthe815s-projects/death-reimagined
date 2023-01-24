package dev.matthe815.deathreimagined.networking.packets;

import dev.matthe815.deathreimagined.DeathReimagined;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerSetupPacket {

    public ServerSetupPacket()
    {
    }

    public static ServerSetupPacket decode(PacketBuffer buf)
    {
        return new ServerSetupPacket();
    }

    public static void encode(ServerSetupPacket msg, PacketBuffer buf)
    {
    }

    public static class Handler
    {
        public static void handle(final ServerSetupPacket pkt, Supplier<NetworkEvent.Context> ctx)
        {

        }
    }
}

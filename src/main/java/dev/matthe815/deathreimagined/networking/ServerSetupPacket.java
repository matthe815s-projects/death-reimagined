package dev.matthe815.deathreimagined.networking;

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
            ServerPlayerEntity player = ctx.get().getSender();
            player.world.setTileEntity(player.getPosition(), new ChestTileEntity());

            DeathReimagined.network.sendTo(new PlayerDyingStatusPacket(false, 0), ctx.get().getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);

            BlockPos spawnLocation;

            if (player.getBedPosition().isPresent()) spawnLocation = player.getBedPosition().get();
            else spawnLocation = player.world.getServer().func_241755_D_().getSpawnPoint();

            player.setPositionAndUpdate(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
            ctx.get().setPacketHandled(true);
        }
    }
}

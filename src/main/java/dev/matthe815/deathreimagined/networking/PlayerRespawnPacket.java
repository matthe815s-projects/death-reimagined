package dev.matthe815.deathreimagined.networking;

import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.api.PlayerData;
import dev.matthe815.deathreimagined.gui.DyingScreen;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerRespawnPacket {

    public PlayerRespawnPacket()
    {
    }

    public static PlayerRespawnPacket decode(PacketBuffer buf)
    {
        return new PlayerRespawnPacket();
    }

    public static void encode(PlayerRespawnPacket msg, PacketBuffer buf)
    {
    }

    public static class Handler
    {
        public static void handle(final PlayerRespawnPacket pkt, Supplier<NetworkEvent.Context> ctx)
        {
            ServerPlayerEntity player = ctx.get().getSender();
            PlayerData.GetData(player).OnRespawn();
            ctx.get().setPacketHandled(true);
        }
    }
}

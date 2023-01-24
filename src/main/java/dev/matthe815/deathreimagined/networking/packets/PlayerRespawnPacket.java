package dev.matthe815.deathreimagined.networking.packets;

import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.api.PlayerData;
import dev.matthe815.deathreimagined.enums.EnumRespawnType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerRespawnPacket {
    public EnumRespawnType respawnType = EnumRespawnType.RESPAWN;

    public PlayerRespawnPacket() {}

    public PlayerRespawnPacket(EnumRespawnType type) {
        respawnType = type;
    }

    public static PlayerRespawnPacket decode(PacketBuffer buf)
    {
        return new PlayerRespawnPacket(buf.readEnumValue(EnumRespawnType.class));
    }

    public static void encode(PlayerRespawnPacket msg, PacketBuffer buf)
    {
        buf.writeEnumValue(msg.respawnType);
    }

    public static class Handler
    {
        public static void handle(final PlayerRespawnPacket pkt, Supplier<NetworkEvent.Context> ctx)
        {
            ServerPlayerEntity player = ctx.get().getSender();

            switch (pkt.respawnType) {
                case RESPAWN: // If you press the respawn button
                    PlayerData.GetData(player).SetTicks(255);
                    break;
                case SELF_REVIVE: // If you use a self-revive item
                    if (!player.inventory.hasItemStack(new ItemStack(DeathReimagined.SYRINGE))) return;

                    ItemStack syringes = player.inventory.getStackInSlot(player.inventory.getSlotFor(new ItemStack(DeathReimagined.SYRINGE)));

                    // If you only have one, remove the stack, else deincrement the count.
                    if (syringes.getCount() == 1) player.inventory.removeStackFromSlot(player.inventory.getSlotFor(syringes));
                    else syringes.setCount(syringes.getCount()-1);

                    PlayerData.GetData(player).OnRespawn(false);
                    break;
            }
            ctx.get().setPacketHandled(true);
        }
    }
}

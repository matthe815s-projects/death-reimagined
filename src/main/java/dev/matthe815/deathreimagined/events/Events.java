package dev.matthe815.deathreimagined.events;

import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.api.PlayerData;
import dev.matthe815.deathreimagined.networking.NetworkManager;
import dev.matthe815.deathreimagined.networking.packets.PlayerHelpRespawnPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Events {
    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerData.AddData((ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerData.RemoveData(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // The client should simulate what's happening on the server
        // This helps save bandwidth and is really all that is necessary.
        if (event.player.world.isRemote) {
            if (DeathReimagined.LOCAL_DATA.IsDying() && DeathReimagined.LOCAL_DATA.GetTicks() > 0)
                DeathReimagined.LOCAL_DATA.SetTicks(DeathReimagined.LOCAL_DATA.GetTicks()-1); // Simulate the server's environment on the client.
            return;
        }

        PlayerData.GetData(event.player).OnTick();
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.EntityInteract event)
    {
        // Only if you right click another player
        if (!(event.getEntity() instanceof PlayerEntity)) return;

        NetworkManager.SendToServer(
                new PlayerHelpRespawnPacket((event.getEntity()).getName().getString()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntity().world.isRemote) return; // This should only run on servers
        if (!(event.getEntity() instanceof PlayerEntity)) return; // Only react to player deaths

        PlayerEntity player = (PlayerEntity)event.getEntity();

        // Perform normal death behaviour if respawning.
        if (PlayerData.GetData(player).IsRespawning()) {
            PlayerData.GetData(player).SetRespawning(false);
            return;
        }

        event.setCanceled(true); // Stop the actual death
        player.setHealth(0.5f); // It won't stop if this isn't set.

        if (PlayerData.GetData(player).IsDying()) return; // We don't want to reset the number while dying.
        PlayerData.GetData(player).OnDeath();
    }
}

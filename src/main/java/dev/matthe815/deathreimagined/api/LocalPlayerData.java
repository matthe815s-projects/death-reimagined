package dev.matthe815.deathreimagined.api;

import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.networking.packets.PlayerDyingStatusPacket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkDirection;

public class LocalPlayerData {
    /**
     * The number of ticks before respawning
     */
    int deathTimer = 0;

    /**
     * Get if the player is currently dying.
     * @return
     */
    public boolean IsDying () {
        return deathTimer > 0;
    }

    public int GetTicks () {
        return deathTimer;
    }

    /**
     * Sets the client-side death ticks.
     * This is still processed server side, so modification will not
     * result in an early respawn.
     * @param ticks
     * @return
     */
    public void SetTicks (int ticks) {
        deathTimer = ticks;
    }

    public void OnRespawn () {
        deathTimer = 0;
    }

    public void OnDeath (int ticks) {
        deathTimer = ticks;
    }
}

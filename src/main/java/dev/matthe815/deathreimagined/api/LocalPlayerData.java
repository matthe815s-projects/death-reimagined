package dev.matthe815.deathreimagined.api;

import dev.matthe815.deathreimagined.gui.DyingScreen;
import net.minecraft.client.Minecraft;

public class LocalPlayerData {
    /**
     * The number of ticks before respawning
     */
    int dyingTick = 0;
    boolean isDying = false;

    /**
     * Get if the player is currently dying.
     * @return
     */
    public boolean IsDying () {
        return dyingTick > 0;
    }

    public int GetTicks () {
        return dyingTick;
    }

    /**
     * Sets the client-side death ticks.
     * This is still processed server side, so modification will not
     * result in an early respawn.
     * @param ticks
     * @return
     */
    public void SetTicks (int ticks) {
        dyingTick = ticks;
    }

    public void OnRespawn () {
        Minecraft.getInstance().currentScreen.closeScreen();
        Minecraft.getInstance().player.stopSleepInBed(true, true);
    }

    public void OnStatus(boolean isDying, int dyingTick) {
        this.isDying = isDying;
        this.dyingTick = dyingTick;
    }

    public void OnDeath() {
        Minecraft.getInstance().displayGuiScreen(new DyingScreen());
        Minecraft.getInstance().player.startSleeping(Minecraft.getInstance().player.getPosition());
    }
}

package dev.matthe815.deathreimagined.api;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    public static final Map<String, PlayerData> playerDatas = new HashMap<>();

    PlayerEntity player;

    /**
     * Keep track of the number of deaths before respawning, it can be used for time degradation.
     */
    int deathCount = 0;

    /**
     * The number of ticks before respawning
     */
    int deathTimer = 0;

    public static PlayerData GetData(PlayerEntity player) {
        return playerDatas.get(player.getName().getString());
    }

    public static void AddData(PlayerEntity player) {
        playerDatas.put(player.getName().getString(), new PlayerData());
    }

    public static void RemoveData(PlayerEntity player) {
        playerDatas.remove(player.getName().getString());
    }

    public void OnDeath () {
        deathCount++;
        deathTimer = 270;
    }

    public void OnTick () {
        // Not dead if there's no timer for it
        if (deathTimer <= 0) return;

        deathTimer -= 1;

        if (deathTimer == 0)  {
            player.respawnPlayer();
            deathCount = 0;
        }
    }
}

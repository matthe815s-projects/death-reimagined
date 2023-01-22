package dev.matthe815.deathreimagined.api;

import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.networking.PlayerDyingStatusPacket;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    public static final Map<String, PlayerData> playerDatas = new HashMap<>();

    ServerPlayerEntity player;

    public PlayerData(ServerPlayerEntity player)
    {
        this.player = player;
    }

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

    public static void AddData(ServerPlayerEntity player) {
        playerDatas.put(player.getName().getString(), new PlayerData(player));
    }

    public static void RemoveData(PlayerEntity player) {
        playerDatas.remove(player.getName().getString());
    }

    public static PlayerData GetData(String target) {
        return playerDatas.get(target);
    }

    /**
     * Set the dying ticks to a specified amount
     * @param ticks
     */
    public void SetTicks (int ticks) {
        this.deathTimer = ticks;
    }

    /**
     * Get if the player is currently dying.
     * @return
     */
    public boolean IsDying () {
        return deathTimer > 0;
    }

    public void OnRespawn (boolean atSpawn) {
        deathCount = 0;
        deathTimer = 0;

        DeathReimagined.network.sendTo(new PlayerDyingStatusPacket(false, 0), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);

        if (atSpawn) {
            BlockPos spawnLocation;

            if (player.getBedPosition().isPresent()) spawnLocation = player.getBedPosition().get();
            else spawnLocation = player.world.getServer().func_241755_D_().getSpawnPoint();

            player.setPositionAndUpdate(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ());
        }

        // Set your health and food to half of the max value
        player.setHealth(player.getMaxHealth() / 2);
        player.getFoodStats().setFoodLevel(10);
    }

    public void OnDeath () {
        deathCount++;
        deathTimer =
                player.getServer().getPlayerList().getPlayers().size() > 1 ? (2400 / deathCount) : 255;

        DeathReimagined.network.sendTo(
                new PlayerDyingStatusPacket(true, deathTimer), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void OnTick () {
        // Not dead if there's no timer for it
        if (deathTimer <= 0) return;

        deathTimer -= 1;

        if (deathTimer == 0)  {
            deathCount = 0;
            OnRespawn(true);
        }
    }
}

package dev.matthe815.deathreimagined.api;

import dev.matthe815.deathreimagined.DeathReimagined;
import dev.matthe815.deathreimagined.networking.NetworkManager;
import dev.matthe815.deathreimagined.networking.packets.PlayerDyingStatusPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    public static final Map<String, PlayerData> playerDatas = new HashMap<>();

    ServerPlayerEntity player;

    boolean respawning = false;

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

        NetworkManager.SendToClient(new PlayerDyingStatusPacket(false, 0), player);

        // Perform the normal death effects if there's no special revive conditions.
        if (atSpawn) {
            respawning = true;
            player.setHealth(0);
        } else {
            player.setHealth(player.getMaxHealth() / 2);
            player.getFoodStats().setFoodLevel(10);
        }
    }

    public void OnDeath () {
        deathCount++;

        // Whether a long delay is applied to the death respawn, if you have a syringe or if another player is online.
        boolean hasLongDelay = player.getServer().getPlayerList().getPlayers().size() > 1
                || player.inventory.hasItemStack(new ItemStack(DeathReimagined.SYRINGE));

        deathTimer = hasLongDelay ? (2400 / deathCount) : 255;

        NetworkManager.HANDLER.sendTo(new PlayerDyingStatusPacket(true, deathTimer), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        //NetworkManager.SendToClient(new PlayerDyingStatusPacket(true, deathTimer), player);
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

    public boolean IsRespawning() {
        return respawning;
    }

    public void SetRespawning(boolean respawning) {
        this.respawning = respawning;
    }
}

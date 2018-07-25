package me.awesomefishh.skywars.player;

import me.awesomefishh.skywars.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerManager {

    private UUID uuid;
    private int coinsEarned;
    private int killsEarned;
    private boolean isAlive;
    private boolean hasDied;
    private int spawn;
    private double coinsMultiplier;

    public PlayerManager(UUID uuid, int coinsEarned, int killsEarned, boolean isAlive, boolean hasDied, int spawn) {
        this.uuid = uuid;
        this.coinsEarned = coinsEarned;
        this.killsEarned = killsEarned;
        this.isAlive = isAlive;
        this.hasDied = hasDied;
        this.spawn = spawn;
        this.setCoinsMultiplier();
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getCoinsEarned() {
        return coinsEarned;
    }

    public void setCoinsEarned(int coinsEarned) {
        this.coinsEarned = coinsEarned;
    }

    public int getKillsEarned() {
        return killsEarned;
    }

    public void setKillsEarned(int killsEarned) {
        this.killsEarned = killsEarned;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    public boolean hasDied() {
        return hasDied;
    }

    public void setHasDied(boolean hasDied) {
        this.hasDied = hasDied;
    }

    public int getSpawn() {
        return spawn;
    }

    public void setSpawn(int spawn) {
        this.spawn = spawn;
    }

    public double getCoinsMultiplier() {
        return coinsMultiplier;
    }

    public void setCoinsMultiplier() {
        Player player = Bukkit.getPlayer(uuid);

        int counter = 0;
        for (String multiplier : Main.getInstance().getConfig().getConfigurationSection("coin-multipliers").getKeys(false)) {
            String permission = Main.getInstance().getConfig().getString("coin-multipliers." + multiplier);
            if (player.hasPermission(permission)) {
                this.coinsMultiplier = Double.parseDouble(multiplier.replaceAll(",", "."));
                counter++;
            }
        }

        if (counter == 0) {
            this.coinsMultiplier = 1;
        }

    }
}

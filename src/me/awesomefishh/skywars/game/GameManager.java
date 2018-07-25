package me.awesomefishh.skywars.game;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {

    private Main plugin;

    private GameState gameState = GameState.LOBBY_WAITING;
    private List<String> validArenas = new ArrayList<>();
    private String chosenArena;

    public GameManager() {
        this.plugin = Main.getInstance();
    }

    public void checkValidArenas() {

        List<String> validArenas = new ArrayList<>();
        Configuration arenaConfig = plugin.getConfigManager().getArenaConfig();

        if (arenaConfig.getConfigurationSection("arenas") == null) {
            plugin.getLogger().info("There are no valid arenas, starting in maintenance mode!");
            setGameState(GameState.MAINTENANCE);
            return;
        }

        for (String name : arenaConfig.getConfigurationSection("arenas").getKeys(false)) {

            boolean succesfull = true;
            String message = "Some settings were not setup correctly for arena " + name + ": ";

            if (arenaConfig.get("arenas." + name + ".minplayers") == null) {
                succesfull = false;
                message = message + "minplayers ";
            }

            if (arenaConfig.get("arenas." + name + ".maxplayers") == null) {
                succesfull = false;
                message = message + "maxplayers ";
            }

            if (Bukkit.getWorld(arenaConfig.getString("arenas." + name + ".selection.world")) == null ||
                    arenaConfig.get("arenas." + name + ".selection.minX") == null ||
                    arenaConfig.get("arenas." + name + ".selection.minY") == null ||
                    arenaConfig.get("arenas." + name + ".selection.minZ") == null ||
                    arenaConfig.get("arenas." + name + ".selection.maxX") == null ||
                    arenaConfig.get("arenas." + name + ".selection.maxY") == null ||
                    arenaConfig.get("arenas." + name + ".selection.maxZ") == null) {
                succesfull = false;
                message = message + "selection ";
            }

            if (arenaConfig.getConfigurationSection("arenas." + name + ".spawns") != null) {
                List<String> validSpawns = new ArrayList<>();
                for (String spawnId : arenaConfig.getConfigurationSection("arenas." + name + ".spawns").getKeys(false)) {
                    if (arenaConfig.getString("arenas." + name + ".spawns." + spawnId + ".world") == null
                            || Bukkit.getWorld(arenaConfig.getString("arenas." + name + ".spawns." + spawnId + ".world")) == null
                            || arenaConfig.get("arenas." + name + ".spawns." + spawnId + ".X") == null
                            || arenaConfig.get("arenas." + name + ".spawns." + spawnId + ".Y") == null
                            || arenaConfig.get("arenas." + name + ".spawns." + spawnId + ".Z") == null) {
                        succesfull = false;
                        message = message + "spawn[" + spawnId + "] ";
                    } else {
                        validSpawns.add(spawnId);
                    }
                }

                if (validSpawns.size() < arenaConfig.getInt("arenas." + name + ".maxplayers")) {
                    succesfull = false;
                    message = message + "not_enough_spawns ";
                }

            } else {
                succesfull = false;
                message = message + "spawn[all] ";
            }

            if (succesfull) {
                validArenas.add(name);
            } else {
                plugin.getLogger().warning(message);
            }
        }

        if (validArenas.size() == 0) {
            plugin.getLogger().info("There are no valid arenas, starting in maintenance mode!");
            setGameState(GameState.MAINTENANCE);
        } else {
            this.validArenas = validArenas;
        }

    }

    public void chooseRandomArena() {

        if (validArenas.size() > 1) {
            Random random = new Random();
            int chosenArenaIndex = random.nextInt(validArenas.size() - 1);
            this.chosenArena = validArenas.get(chosenArenaIndex);
            plugin.getLogger().info("Arena " + this.chosenArena + " was chosen!");
        } else if (validArenas.size() == 1) {
            this.chosenArena = validArenas.get(0);
            plugin.getLogger().info("Arena " + this.chosenArena + " was chosen!");
        }

    }

    public void dropItems(Player player) {
        PlayerInventory inventory = player.getInventory();

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null) {
                Location location = player.getLocation();
                location.getWorld().dropItemNaturally(location, itemStack);
            }
        }

        for (ItemStack itemStack : inventory.getArmorContents()) {
            if (itemStack != null) {
                Location location = player.getLocation();
                location.getWorld().dropItemNaturally(location, itemStack);
            }
        }

    }

    public List<Player> getPlayersAlive() {
        List<Player> playersAlive = new ArrayList<>();
        for (PlayerManager playerManager : plugin.getPlayerManager().values()) {
            if (playerManager.isAlive()) {
                playersAlive.add(Bukkit.getPlayer(playerManager.getUuid()));
            }
        }
        return playersAlive;
    }

    public List<Player> getPlayersSpectating() {
        List<Player> playersSpectating = new ArrayList<>();
        for (PlayerManager playerManager : plugin.getPlayerManager().values()) {
            if (!playerManager.isAlive()) {
                playersSpectating.add(Bukkit.getPlayer(playerManager.getUuid()));
            }
        }
        return playersSpectating;
    }

    public List<Player> getPlayersDied() {
        List<Player> playersDied = new ArrayList<>();
        for (PlayerManager playerManager : plugin.getPlayerManager().values()) {
            if (playerManager.hasDied()) {
                playersDied.add(Bukkit.getPlayer(playerManager.getUuid()));
            }
        }
        return playersDied;
    }


    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public List<String> getValidArenas() {
        return validArenas;
    }

    public String getChosenArena() {
        return chosenArena;
    }
}

package me.awesomefishh.skywars.schedulers;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import me.awesomefishh.skywars.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class LobbyCountdown {

    private Main plugin;

    private int taskId;

    public LobbyCountdown() {
        this.plugin = Main.getInstance();
    }

    public void initiate() {

        plugin.getGameManager().setGameState(GameState.LOBBY_COUNTDOWN);

        new BukkitRunnable() {

            int seconds = plugin.getConfig().getInt("lobby-countdown");

            @Override
            public void run() {
                taskId = this.getTaskId();
                if (seconds > 0) {
                    switch (seconds) {
                        case 120:
                        case 60:
                        case 30:
                        case 15:
                        case 10:
                        case 5:
                        case 4:
                        case 3:
                        case 2:
                        case 1:
                            Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.GRAY + "You will be teleported to your islands in " + ChatColor.RED + seconds + ChatColor.GRAY + " seconds!");
                            break;
                        default:
                            break;
                    }
                    seconds--;
                } else {
                    Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.GRAY + "Teleporting to islands!");

                    Configuration arenaConfig = plugin.getConfigManager().getArenaConfig();
                    GameManager gameManager = plugin.getGameManager();

                    int c = 1;
                    for (Player player : gameManager.getPlayersAlive()) {
                        plugin.getPlayerManager().get(player.getUniqueId()).setSpawn(c);
                        Location islandSpawn = new Location(Bukkit.getWorld(arenaConfig.getString("arenas." + gameManager.getChosenArena() + ".spawns." + c + ".world")),
                                arenaConfig.getDouble("arenas." + gameManager.getChosenArena() + ".spawns." + c + ".X"),
                                arenaConfig.getDouble("arenas." + gameManager.getChosenArena() + ".spawns." + c + ".Y"),
                                arenaConfig.getDouble("arenas." + gameManager.getChosenArena() + ".spawns." + c + ".Z"));
                        player.teleport(islandSpawn);
                        player.getInventory().clear();
                        c++;
                    }

                    for (Player player : plugin.getGameManager().getPlayersSpectating()) {
                        player.teleport(plugin.getGameManager().getPlayersAlive().get(0));
                        player.getInventory().clear();
                    }
                    gameManager.setGameState(GameState.INGAME);
                    this.cancel();
                }

            }

        }.runTaskTimer(plugin, 0L, 20L);

    }

    public void cancel() {
        plugin.getServer().getScheduler().cancelTask(taskId);
        plugin.getGameManager().setGameState(GameState.LOBBY_WAITING);
        Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.GRAY + "There were not enough players left and the countdown has been cancelled!");
    }
}

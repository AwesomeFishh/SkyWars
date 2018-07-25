package me.awesomefishh.skywars.listeners;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.database.DatabaseManager;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import me.awesomefishh.skywars.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class QuitListener implements Listener {

    private Main plugin;
    private DatabaseManager databaseManager;
    private GameManager gameManager;

    public QuitListener() {
        this.plugin = Main.getInstance();
        this.databaseManager = plugin.getDatabaseManager();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (gameManager.getGameState().equals(GameState.MAINTENANCE)) {
            event.setQuitMessage(plugin.getPrefix() + ChatColor.RED + player.getName() + ChatColor.GRAY + " left! " + ChatColor.DARK_RED + "[MAINTENANCE MODE]");
            return;
        }

        Configuration arenaConfig = plugin.getConfigManager().getArenaConfig();

        if (gameManager.getGameState().equals(GameState.LOBBY_WAITING) || gameManager.getGameState().equals(GameState.LOBBY_COUNTDOWN)) {

            plugin.getPlayerManager().remove(uuid);
            event.setQuitMessage(plugin.getPrefix() + ChatColor.RED + player.getName() + ChatColor.GRAY + " has left the game! [" + ChatColor.RED + gameManager.getPlayersAlive().size() + "/"
                    + arenaConfig.getInt("arenas." + gameManager.getChosenArena() + ".maxplayers") + ChatColor.GRAY + "]");

            //Check if we need to cancel the countdown
            if (gameManager.getPlayersAlive().size() < arenaConfig.getInt("arenas." + gameManager.getChosenArena() + ".minplayers") && gameManager.getPlayersAlive().size() + 1 == arenaConfig.getInt("arenas." + gameManager.getChosenArena() + ".minplayers")) {
                plugin.getLobbyCountdown().cancel();
            }

            return;
        }

        if (gameManager.getGameState().equals(GameState.INGAME)) {

            PlayerManager playerManager = plugin.getPlayerManager().get(uuid);
            if (playerManager.isAlive()) {

                player.setHealth(20);
                player.setFoodLevel(20);
                player.setGameMode(GameMode.SPECTATOR);

                playerManager.setAlive(false);
                playerManager.setHasDied(true);

                event.setQuitMessage(plugin.getPrefix() + ChatColor.RED + player.getName() + ChatColor.GRAY + " has left the game!");
                Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.RED + player.getName() + ChatColor.GRAY + " died!");
                Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.GOLD + gameManager.getPlayersAlive().size() + ChatColor.GRAY + " players remaining.");

            } else {
                event.setQuitMessage(plugin.getPrefix() + ChatColor.GRAY + player.getName() + " has left the game and is no longer spectating!");
                return;
            }

            //Check if we have a winner
            if (gameManager.getPlayersAlive().size() == 1) {

                gameManager.setGameState(GameState.FINISHED);

                Player lastPlayer = gameManager.getPlayersAlive().get(0);
                UUID luuid = lastPlayer.getUniqueId();
                PlayerManager lastManager = plugin.getPlayerManager().get(luuid);

                double playerCoinMultiplier = lastManager.getCoinsMultiplier();
                int coinsPerWin = plugin.getConfig().getInt("coins-per-win");
                int coinsEarnedForWin = (int) (playerCoinMultiplier * coinsPerWin);
                lastManager.setCoinsEarned(lastManager.getCoinsEarned() + coinsEarnedForWin);

                Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.RED + lastPlayer.getName() + ChatColor.GRAY + " has won the game with " + ChatColor.RED + lastManager.getKillsEarned() + ChatColor.GRAY + "!");
                lastPlayer.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "+" + coinsEarnedForWin + " coins");

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try {
                            PreparedStatement addLastPlayerWin = databaseManager.getConnection().prepareStatement("UPDATE " + databaseManager.getPlayertable() + " SET WINS=WINS+1 WHERE UUID='" + luuid.toString() + "'");
                            addLastPlayerWin.executeUpdate();

                            for (PlayerManager playerManager : plugin.getPlayerManager().values()) {

                                int coinsEarned = playerManager.getCoinsEarned();
                                int killsEarned = playerManager.getKillsEarned();

                                PreparedStatement updateStats = databaseManager.getConnection().prepareStatement("UPDATE " + databaseManager.getPlayertable()
                                        + " SET COINS=COINS+" + coinsEarned + ", KILLS=KILLS+" + killsEarned + ", GAMESPLAYED=GAMESPLAYED+1 WHERE UUID='" + playerManager.getUuid().toString() + "'");
                                updateStats.executeUpdate();

                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        Bukkit.shutdown();

                    }
                }.runTaskLater(plugin, 10L * 20L);
            }

        }

    }

}

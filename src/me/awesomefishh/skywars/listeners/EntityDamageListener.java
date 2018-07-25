package me.awesomefishh.skywars.listeners;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.database.DatabaseManager;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import me.awesomefishh.skywars.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class EntityDamageListener implements Listener {

    private Main plugin;
    private DatabaseManager databaseManager;
    private GameManager gameManager;

    public EntityDamageListener() {
        this.plugin = Main.getInstance();
        this.databaseManager = plugin.getDatabaseManager();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onDeath(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (gameManager.getGameState().equals(GameState.LOBBY_WAITING) || gameManager.getGameState().equals(GameState.LOBBY_COUNTDOWN) || gameManager.getGameState().equals(GameState.FINISHED)) {
            event.setCancelled(true);
            return;
        }

        if (gameManager.getGameState().equals(GameState.MAINTENANCE)) {
            return;
        }

        //If the cause is another Entity
        if (event instanceof EntityDamageByEntityEvent) {

            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event;

            if (!(entityEvent.getDamager() instanceof Player)) {
                return;
            }

            Player damager = (Player) entityEvent.getDamager();
            UUID duuid = damager.getUniqueId();

            if (player.getHealth() - entityEvent.getDamage() > 1) {
                return;
            }

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SPECTATOR);

            PlayerManager playerManager = plugin.getPlayerManager().get(uuid);
            PlayerManager damagerManager = plugin.getPlayerManager().get(duuid);

            playerManager.setAlive(false);
            playerManager.setHasDied(true);

            double playerCoinMultiplier = damagerManager.getCoinsMultiplier();
            int coinsPerKill = plugin.getConfig().getInt("coins-per-kill");
            int coinsEarnedForKill = (int) (coinsPerKill * playerCoinMultiplier);
            damagerManager.setCoinsEarned(damagerManager.getCoinsEarned() + coinsEarnedForKill); //Add coins to killers PlayerManager
            damagerManager.setKillsEarned(damagerManager.getKillsEarned() + 1); //Add kils

            Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.RED + player.getName() + ChatColor.GRAY + " was killed by " + ChatColor.RED + damager.getName() + "!"); //BC death message
            Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.GOLD + gameManager.getPlayersAlive().size() + ChatColor.GRAY + " players remaining."); //BC players remaining
            damager.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "+" + coinsEarnedForKill + " coins"); //Send message +X coins
            player.sendMessage(plugin.getPrefix() + ChatColor.GRAY + "Your stats: " + ChatColor.GOLD + playerManager.getCoinsEarned() + " coins, " + ChatColor.RED + playerManager.getKillsEarned()
                    + " kills, " + ChatColor.GREEN + "finished: " + gameManager.getPlayersAlive().size() + 1 + "."); //Send message with stats
            gameManager.dropItems(player);

        }

        //Else it's a cause like FallDamage
        else {

            if (player.getHealth() - event.getDamage() > 1) {
                return;
            }

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SPECTATOR);

            PlayerManager playerManager = plugin.getPlayerManager().get(uuid);
            playerManager.setAlive(false);
            playerManager.setHasDied(true);

            Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.RED + player.getName() + ChatColor.GRAY + " died!");
            Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.GOLD + gameManager.getPlayersAlive().size() + ChatColor.GRAY + " players remaining.");
            player.sendMessage(plugin.getPrefix() + ChatColor.GRAY + "Your stats: " + ChatColor.GOLD + playerManager.getCoinsEarned() + " coins, " + ChatColor.RED + playerManager.getKillsEarned()
                    + " kills, " + ChatColor.GREEN + "finished: " + gameManager.getPlayersAlive().size() + 1 + ".");
            gameManager.dropItems(player);

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

            Bukkit.broadcastMessage(plugin.getPrefix() + ChatColor.RED + lastPlayer.getName() + ChatColor.GRAY + " has won the game with " + ChatColor.RED + lastManager.getKillsEarned() + ChatColor.GRAY + "kills!");
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

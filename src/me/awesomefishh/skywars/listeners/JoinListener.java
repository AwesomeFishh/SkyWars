package me.awesomefishh.skywars.listeners;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import me.awesomefishh.skywars.player.PlayerManager;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class JoinListener implements Listener {

    private Main plugin;
    private GameManager gameManager;

    public JoinListener() {
        this.plugin = Main.getInstance();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        plugin.getDatabaseManager().checkPlayer(player);


        if (gameManager.getGameState().equals(GameState.MAINTENANCE)) {
            if (player.hasPermission("skywars.maintenance.join")) {
                event.setJoinMessage(plugin.getPrefix() + ChatColor.RED + player.getName() + ChatColor.GRAY + " joined! " + ChatColor.DARK_RED + "[MAINTENANCE MODE]");
            } else {
                event.setJoinMessage(plugin.getPrefix() + ChatColor.RED + player.getName() + ChatColor.GRAY + " joined without permission! " + ChatColor.DARK_RED + "[MAINTENANCE MODE]");
                player.kickPlayer(ChatColor.RED + "This server is in maintenance mode!");
            }
            return;
        }

        Location spawnLoc = new Location(Bukkit.getWorld(plugin.getConfig().getString("spawn.world")), plugin.getConfig().getDouble("spawn.X"), plugin.getConfig().getDouble("spawn.Y"), plugin.getConfig().getDouble("spawn.Z"));

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();

        Configuration arenaConfig = plugin.getConfigManager().getArenaConfig();

        if (gameManager.getGameState().equals(GameState.LOBBY_WAITING) || gameManager.getGameState().equals(GameState.LOBBY_COUNTDOWN)) {

            //If the game is not full
            if (gameManager.getPlayersAlive().size() < arenaConfig.getInt("arenas." + gameManager.getChosenArena() + ".maxplayers")) {

                plugin.getPlayerManager().put(uuid, new PlayerManager(uuid, 0, 0, true, false, -1));
                event.setJoinMessage(plugin.getPrefix() + ChatColor.RED + player.getName() + ChatColor.GRAY + " has joined the game! [" + ChatColor.RED + gameManager.getPlayersAlive().size() + "/" +
                        arenaConfig.getInt("arenas." + gameManager.getChosenArena() + ".maxplayers") + ChatColor.GRAY + "]");
                player.teleport(spawnLoc);

            }

            //If the game is full, player is spectating.
            else {

                plugin.getPlayerManager().put(uuid, new PlayerManager(uuid, 0, 0, false, false, -1));
                event.setJoinMessage(plugin.getPrefix() + ChatColor.GRAY + player.getName() + " has joined and is now spectating!");
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(spawnLoc);

            }

            //Check if we have to start the countdown
            if (gameManager.getPlayersAlive().size() >= arenaConfig.getInt("arenas." + gameManager.getChosenArena() + ".minplayers")) {
                plugin.getLobbyCountdown().initiate();
            }

            ItemStack kitItem = new ItemStack(Material.PAPER);
            kitItem.getItemMeta().setDisplayName(ChatColor.GOLD + "Choose a kit!");
            player.getInventory().setItem(4, kitItem);

            return;
        }

        if (gameManager.getGameState().equals(GameState.INGAME) || gameManager.getGameState().equals(GameState.FINISHED)) {
            if (!plugin.getPlayerManager().containsKey(uuid))
                plugin.getPlayerManager().put(uuid, new PlayerManager(uuid, 0, 0, false, false, -1));
            event.setJoinMessage(plugin.getPrefix() + ChatColor.GRAY + player.getName() + " has joined and is now spectating!");
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(gameManager.getPlayersAlive().get(0));
        }

    }

}

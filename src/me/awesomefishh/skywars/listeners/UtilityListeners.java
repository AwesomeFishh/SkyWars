package me.awesomefishh.skywars.listeners;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class UtilityListeners implements Listener {

    private Main plugin;
    private GameManager gameManager;

    public UtilityListeners() {
        plugin = Main.getInstance();
        gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void disableDeathMessage(PlayerDeathEvent event) {
        if (!gameManager.getGameState().equals(GameState.MAINTENANCE)) {
            event.setDeathMessage("");
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (gameManager.getGameState().equals(GameState.LOBBY_WAITING) || gameManager.getGameState().equals(GameState.LOBBY_COUNTDOWN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (gameManager.getGameState().equals(GameState.LOBBY_WAITING) || gameManager.getGameState().equals(GameState.LOBBY_COUNTDOWN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (gameManager.getGameState().equals(GameState.LOBBY_WAITING) || gameManager.getGameState().equals(GameState.LOBBY_COUNTDOWN)) {
            event.setCancelled(true);
        }
    }
}

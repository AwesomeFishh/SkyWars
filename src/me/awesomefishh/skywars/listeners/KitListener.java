package me.awesomefishh.skywars.listeners;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitListener implements Listener {

    private Main plugin;
    private GameManager gameManager;

    public KitListener() {
        this.plugin = Main.getInstance();
        this.gameManager = plugin.getGameManager();
    }

    //Right click the paper
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        GameState gameState = gameManager.getGameState();
        if (!gameState.equals(GameState.LOBBY_COUNTDOWN) && !gameState.equals(GameState.LOBBY_WAITING)) {
            return;
        }

        Action eventAction = event.getAction();
        if (!eventAction.equals(Action.RIGHT_CLICK_AIR) && !eventAction.equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().getType().equals(Material.PAPER)) {
            player.sendMessage(plugin.getPrefix() + ChatColor.GRAY + "Opening kit menu...");
            plugin.getKitMenu().setupInventory(player);
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        GameState gameState = gameManager.getGameState();
        if (gameState.equals(GameState.LOBBY_WAITING) || gameState.equals(GameState.LOBBY_COUNTDOWN)) {

            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem.getItemMeta() == null)
                return;
            ItemMeta clickedItemMeta = clickedItem.getItemMeta();

            if (clickedItemMeta.getDisplayName().contains(ChatColor.GRAY + "Kit")) {
                String kitName = clickedItemMeta.getDisplayName().substring(6);
                player.sendMessage(plugin.getPrefix() + ChatColor.GRAY + "You clicked kit " + kitName);

            }

        }

    }

}

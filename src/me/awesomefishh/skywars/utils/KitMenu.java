package me.awesomefishh.skywars.utils;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.kits.KitMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitMenu {

    private Main plugin;

    public KitMenu() {
        this.plugin = Main.getInstance();
    }

    public void setupInventory(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Choose your kit!");

        for (KitMain kit : plugin.getKitManager().values()) {
            ItemStack kitIcon = new ItemStack(kit.getKitIcon());

            ItemMeta kitIconMeta = kitIcon.getItemMeta();
            kitIconMeta.setDisplayName(ChatColor.GRAY + "Kit " + kit.getName());
            kitIcon.setItemMeta(kitIconMeta);

            int kitSlot = kit.getKitSlot();

            inventory.setItem(kitSlot, kitIcon);
        }

        player.openInventory(inventory);

    }

}

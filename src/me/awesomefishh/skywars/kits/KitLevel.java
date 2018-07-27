package me.awesomefishh.skywars.kits;

import me.awesomefishh.skywars.Main;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitLevel {

    private Main plugin;

    private String name;
    private int level;
    private List<ItemStack> items;

    KitLevel(String name, int level, List<ItemStack> items) {
        this.plugin = Main.getInstance();
        this.name = name;
        this.level = level;
        this.items = items;
    }


    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}

package me.awesomefishh.skywars.kits;


import me.awesomefishh.skywars.Main;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitMain {

    private Main plugin;

    private String name;
    private Map<Integer, KitLevel> kitLevels = new HashMap<>();
    private int levels = 0;

    private Material kitIcon;
    private int kitSlot;

    public KitMain(String name) {
        this.name = name;

        plugin = Main.getInstance();
    }

    public void setupKit() {

        Configuration kitConfig = plugin.getConfigManager().getKitConfig();

        kitIcon = Material.getMaterial(kitConfig.getString("kits." + name + ".icon"));
        kitSlot = kitConfig.getInt("kits." + name + ".slot");

        for (String level : kitConfig.getConfigurationSection("kits." + name + ".levels").getKeys(false)) {
            List<ItemStack> items = new ArrayList<>();
            boolean isValid = true;

            plugin.getLogger().info(name + ", " + level);
            for (String item : kitConfig.getStringList("kits." + name + ".levels." + level)) {
                if (getItem(item) != null || getAmount(item) != -1) {
                    levels++;
                    items.add(new ItemStack(getItem(item), getAmount(item)));
                } else {
                    isValid = false;
                }
            }

            if (isValid) {
                kitLevels.put(Integer.parseInt(level), new KitLevel(name, Integer.parseInt(level), items));
            } else {
                plugin.getLogger().info("Kit " + name + ", level " + level + " is not valid, ignoring.");
            }

        }

    }

    private Material getItem(String kitString) {
        String itemName = kitString.substring(0, kitString.indexOf(" "));
        if (Material.getMaterial(itemName) != null) {
            return Material.getMaterial(itemName);
        } else {
            plugin.getLogger().warning(name + ": " + itemName + " is not a valid item!");
            return null;
        }
    }

    private int getAmount(String kitString) {
        try {
            int amount = Integer.parseInt(kitString.substring(kitString.indexOf(" ") + 1));
            if (amount < 1) {
                plugin.getLogger().warning(kitString + ": amount cannot be less than 1!");
                return -1;
            }
            return amount;

        } catch (NumberFormatException e) {
            e.printStackTrace();
            plugin.getLogger().warning(kitString + ": does not have a valid amount!");
            return -1;
        }
    }


    public String getName() {
        return name;
    }

    public int getAmountOfLevels() {
        return levels;
    }

    public Map<Integer, KitLevel> getKitLevels() {
        return kitLevels;
    }

    public KitLevel getKitLevel(int level) {
        if (getKitLevels().containsKey(level)) {
            return getKitLevels().get(level);
        }
        return null;
    }

    public Material getKitIcon() {
        return kitIcon;
    }

    public int getKitSlot() {
        return kitSlot;
    }
}

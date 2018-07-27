package me.awesomefishh.skywars.configurations;

import me.awesomefishh.skywars.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class KitConfig {

    private Main plugin;

    KitConfig() {
        this.plugin = Main.getInstance();
    }

    private FileConfiguration cfg;
    private File file;

    void createConfig() {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        file = new File(plugin.getDataFolder(), "kits.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.getLogger().info("Kits.yml successfully created!");
            } catch (IOException e) {
                plugin.getLogger().severe("Kits.yml could not be created! (IOException)");
                e.printStackTrace();
            }
        }

        cfg = YamlConfiguration.loadConfiguration(file);
    }

    FileConfiguration getConfig() {
        return cfg;
    }

    void saveDefaultConfig() {
        plugin.saveResource("kits.yml", false);
        file = new File(plugin.getDataFolder(), "kits.yml");
        cfg = YamlConfiguration.loadConfiguration(file);
    }

}

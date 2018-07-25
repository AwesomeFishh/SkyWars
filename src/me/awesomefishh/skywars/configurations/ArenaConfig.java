package me.awesomefishh.skywars.configurations;

import me.awesomefishh.skywars.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ArenaConfig {

    private Main plugin;

    ArenaConfig() {
        this.plugin = Main.getInstance();
    }

    private FileConfiguration cfg;
    private File file;

    void createConfig() {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        file = new File(plugin.getDataFolder(), "arenas.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.getLogger().info("Arenas.yml successfully created!");
            } catch (IOException e) {
                plugin.getLogger().severe("Arenas.yml could not be created! (IOException)");
                e.printStackTrace();
            }
        }

        cfg = YamlConfiguration.loadConfiguration(file);
    }

    FileConfiguration getConfig() {
        return cfg;
    }

    void saveConfig() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Arenas.yml could not be saved!");
            e.printStackTrace();
        }
    }

}

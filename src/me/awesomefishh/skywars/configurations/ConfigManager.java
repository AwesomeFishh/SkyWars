package me.awesomefishh.skywars.configurations;

import me.awesomefishh.skywars.Main;
import org.bukkit.configuration.Configuration;

public class ConfigManager {

    private Main plugin;
    private ArenaConfig arenaConfig;

    public ConfigManager() {
        this.plugin = Main.getInstance();
        this.arenaConfig = new ArenaConfig();
    }

    public void setupConfigs() {
        arenaConfig.createConfig();
        arenaConfig.saveConfig();
    }


    public Configuration getArenaConfig() {
        return arenaConfig.getConfig();
    }

    public void saveArenaConfig() {
        arenaConfig.saveConfig();
    }

}

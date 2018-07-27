package me.awesomefishh.skywars.configurations;

import org.bukkit.configuration.Configuration;

public class ConfigManager {

    private ArenaConfig arenaConfig;
    private KitConfig kitConfig;

    public ConfigManager() {
        this.arenaConfig = new ArenaConfig();
        this.kitConfig = new KitConfig();
    }

    public void setupConfigs() {
        arenaConfig.createConfig();
        arenaConfig.saveConfig();

        kitConfig.createConfig();
        kitConfig.saveDefaultConfig();
    }


    public Configuration getArenaConfig() {
        return arenaConfig.getConfig();
    }

    public void saveArenaConfig() {
        arenaConfig.saveConfig();
    }

    public Configuration getKitConfig() {
        return kitConfig.getConfig();
    }

    public void saveKitConfig() {
        kitConfig.saveDefaultConfig();
    }

}

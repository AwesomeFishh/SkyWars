package me.awesomefishh.skywars;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.awesomefishh.skywars.commands.CommandManager;
import me.awesomefishh.skywars.configurations.ConfigManager;
import me.awesomefishh.skywars.database.DatabaseManager;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import me.awesomefishh.skywars.kits.KitMain;
import me.awesomefishh.skywars.listeners.*;
import me.awesomefishh.skywars.player.PlayerManager;
import me.awesomefishh.skywars.pluginmessage.PlMessageListener;
import me.awesomefishh.skywars.pluginmessage.PlMessageMethods;
import me.awesomefishh.skywars.schedulers.LobbyCountdown;
import me.awesomefishh.skywars.utils.KitMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private DatabaseManager databaseManager;
    private LobbyCountdown lobbyCountdown;
    private ConfigManager configManager;
    private GameManager gameManager;
    private CommandManager commandManager;
    private PlMessageMethods plMessageMethods;
    private KitMenu kitMenu;

    private WorldEditPlugin worldEditPlugin;
    private String prefix;
    private Map<UUID, PlayerManager> playerManager = new HashMap<>();
    private Map<String, KitMain> kitManager = new HashMap<>();

    public void onEnable() {
        saveDefaultConfig();

        registerClasses();
        registerListeners();
        setupKits();

        gameManager.checkValidArenas();
        gameManager.chooseRandomArena();

        setupWorld();

    }

    public void onDisable() {

        //TODO feature: send players to lobby
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (gameManager.getGameState().equals(GameState.FINISHED)) {
                player.kickPlayer(getPrefix() + ChatColor.GOLD + "The game has ended!");
            } else {
                player.kickPlayer(getPrefix() + ChatColor.RED + "The server has shutdown for some reason. If you believe this to be an error, please contact an adminstrator!");
            }
        }

    }

    private void registerClasses() {

        worldEditPlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("chat-prefix"));

        databaseManager = new DatabaseManager();
        databaseManager.establishConnection();
        lobbyCountdown = new LobbyCountdown();
        configManager = new ConfigManager();
        configManager.setupConfigs();
        gameManager = new GameManager();
        commandManager = new CommandManager();
        commandManager.setup();
        plMessageMethods = new PlMessageMethods();
        kitMenu = new KitMenu();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PlMessageListener());

    }

    private void registerListeners() {

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new UtilityListeners(), this);
        getServer().getPluginManager().registerEvents(new KitListener(), this);

    }

    private void setupKits() {
        for (String kitName : configManager.getKitConfig().getConfigurationSection("kits").getKeys(false)) {
            kitManager.put(kitName, new KitMain(kitName));
            kitManager.get(kitName).setupKit();
        }
    }

    private void setupWorld() {
        Bukkit.getWorld(getConfig().getString("world")).setAutoSave(false);
        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("announceAdvancements", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doWeatherCycle", "false");
            for (Entity entity : world.getEntities()) {
                entity.remove();
            }
        }
    }


    public Main() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LobbyCountdown getLobbyCountdown() {
        return lobbyCountdown;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public PlMessageMethods getMessageMethods() {
        return plMessageMethods;
    }

    public KitMenu getKitMenu() {
        return kitMenu;
    }

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<UUID, PlayerManager> getPlayerManager() {
        return playerManager;
    }

    public Map<String, KitMain> getKitManager() {
        return kitManager;
    }
}

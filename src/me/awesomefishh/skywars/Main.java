package me.awesomefishh.skywars;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.awesomefishh.skywars.commands.CommandManager;
import me.awesomefishh.skywars.configurations.ConfigManager;
import me.awesomefishh.skywars.database.DatabaseManager;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import me.awesomefishh.skywars.listeners.EntityDamageListener;
import me.awesomefishh.skywars.listeners.JoinListener;
import me.awesomefishh.skywars.listeners.QuitListener;
import me.awesomefishh.skywars.listeners.UtilityListeners;
import me.awesomefishh.skywars.player.PlayerManager;
import me.awesomefishh.skywars.pluginmessage.MessageListener;
import me.awesomefishh.skywars.pluginmessage.MessageMethods;
import me.awesomefishh.skywars.schedulers.LobbyCountdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private DatabaseManager databaseManager;
    private LobbyCountdown lobbyCountdown;
    private ConfigManager configManager;
    private GameManager gameManager;
    private CommandManager commandManager;
    private MessageMethods messageMethods;

    private WorldEditPlugin worldEditPlugin;
    private String prefix;
    private HashMap<UUID, PlayerManager> playerManager = new HashMap<>();

    public void onEnable() {
        saveDefaultConfig();

        registerClasses();
        registerListeners();

        gameManager.checkValidArenas();
        gameManager.chooseRandomArena();

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

    public void onDisable() {

        //TODO future: send players to lobby
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
        messageMethods = new MessageMethods();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());

    }

    private void registerListeners() {

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new UtilityListeners(), this);

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

    public MessageMethods getMessageMethods() {
        return messageMethods;
    }

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    public String getPrefix() {
        return prefix;
    }

    public HashMap<UUID, PlayerManager> getPlayerManager() {
        return playerManager;
    }
}

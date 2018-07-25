package me.awesomefishh.skywars.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommArena extends SubCommand {

    private Main plugin;
    private GameManager gameManager;

    CommArena() {
        this.plugin = Main.getInstance();
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Only players can use this command!");
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (args.length == 0) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid args! /sw help");
            return;
        }

        //sw arena create(0) <name>(1) <maxplayers>(2) <minplayers>(3)
        if (args[0].equalsIgnoreCase("create")) {
            onCreate(player, args);
        }
        //sw arena setspawn(0) <name>(1) <spawn>(2)
        else if (args[0].equalsIgnoreCase("setspawn")) {
            onSetSpawn(player, args);
        }

    }

    private void onCreate(Player player, String[] args) {

        if (!gameManager.getGameState().equals(GameState.MAINTENANCE)) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You can only use this command in maintenance mode!");
            return;
        }

        if (args.length != 4) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid args! /sw help");
            return;
        }

        if (!player.hasPermission("skywars.arena.create")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You don't have permission to do that!");
            return;
        }

        String name = args[1];
        int maxPlayers, minPlayers;

        try {

            maxPlayers = Integer.parseInt(args[2]);
            minPlayers = Integer.parseInt(args[3]);

        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid number(s)!");
            return;
        }

        if (minPlayers > maxPlayers) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Min. players can't be bigger than max. players!");
            return;
        }

        if (minPlayers < 1) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Min. players can't 0 or less!");
            return;
        }

        Configuration arenaConfig = plugin.getConfigManager().getArenaConfig();
        if (arenaConfig.getConfigurationSection("arenas." + name) != null) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "An arena with that name already exists!");
            return;
        }

        if (plugin.getWorldEditPlugin().getSelection(player) == null) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Please make a WorldEdit selection of your arena first!");
            return;
        }

        Selection selection = plugin.getWorldEditPlugin().getSelection(player);
        Location minLoc = selection.getMinimumPoint();
        Location maxLoc = selection.getMaximumPoint();

        arenaConfig.set("arenas." + name + ".minplayers", minPlayers);
        arenaConfig.set("arenas." + name + ".maxplayers", maxPlayers);
        arenaConfig.set("arenas." + name + ".selection.world", minLoc.getWorld().getName());
        arenaConfig.set("arenas." + name + ".selection.minX", minLoc.getX());
        arenaConfig.set("arenas." + name + ".selection.minY", minLoc.getY());
        arenaConfig.set("arenas." + name + ".selection.minZ", minLoc.getZ());
        arenaConfig.set("arenas." + name + ".selection.maxX", maxLoc.getX());
        arenaConfig.set("arenas." + name + ".selection.maxY", maxLoc.getY());
        arenaConfig.set("arenas." + name + ".selection.maxZ", maxLoc.getZ());
        plugin.getConfigManager().saveArenaConfig();

        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Successfully created arena " + name + ".");
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Max. players: " + ChatColor.YELLOW + maxPlayers);
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Min. players: " + ChatColor.YELLOW + minPlayers);

    }

    private void onSetSpawn(Player player, String[] args) {

        if (!gameManager.getGameState().equals(GameState.MAINTENANCE)) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You can only use this command in maintenance mode!");
            return;
        }

        if (args.length != 3) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid args! /sw help");
            return;
        }

        if (!player.hasPermission("skywars.arena.setspawn")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You don't have permission to do that!");
            return;
        }

        String name = args[1];
        int spawn;

        try {
            spawn = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid number(s)!");
            return;
        }

        if (spawn < 1) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Spawn ID can't be 0 or less!");
            return;
        }

        Configuration arenaConfig = plugin.getConfigManager().getArenaConfig();
        if (spawn > arenaConfig.getInt("arenas." + name + ".maxplayers")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Spawn ID can't be higher than the max. players!");
            return;
        }

        Location location = player.getLocation();
        arenaConfig.set("arenas." + name + ".spawns." + spawn + ".world", location.getWorld().getName());
        arenaConfig.set("arenas." + name + ".spawns." + spawn + ".X", location.getX());
        arenaConfig.set("arenas." + name + ".spawns." + spawn + ".Y", location.getY());
        arenaConfig.set("arenas." + name + ".spawns." + spawn + ".Z", location.getZ());
        plugin.getConfigManager().saveArenaConfig();

        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Spawn " + spawn + " set successfully!");
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + location.getWorld().getName() + ChatColor.YELLOW + ", " + ChatColor.GREEN + location.getX() + ChatColor.YELLOW + ", " + ChatColor.GREEN + location.getY() + ChatColor.YELLOW + ", " + ChatColor.GREEN + location.getZ());

    }

    @Override
    public String name() {
        return plugin.getCommandManager().arena;
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}

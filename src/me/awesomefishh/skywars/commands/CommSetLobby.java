package me.awesomefishh.skywars.commands;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommSetLobby extends SubCommand {

    private Main plugin;
    private GameManager gameManager;

    CommSetLobby() {
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

        if (!gameManager.getGameState().equals(GameState.MAINTENANCE)) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You can only use this command in maintenance mode!");
            return;
        }

        if (args.length != 0) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid args! /sw help");
            return;
        }

        if (!player.hasPermission("skywars.setlobby")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You don't have permission to do that!");
            return;
        }

        Location location = player.getLocation();
        plugin.getConfig().set("spawn.world", location.getWorld().getName());
        plugin.getConfig().set("spawn.X", location.getX());
        plugin.getConfig().set("spawn.Y", location.getY());
        plugin.getConfig().set("spawn.Z", location.getZ());
        plugin.saveConfig();

        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Spawn set successfully!");
        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + location.getWorld().getName() + ChatColor.GREEN + ", " + ChatColor.YELLOW + location.getX() + ChatColor.GREEN + ", " + ChatColor.YELLOW + location.getY() + ChatColor.GREEN + ", " + ChatColor.YELLOW + location.getZ());

    }

    @Override
    public String name() {
        return plugin.getCommandManager().setlobby;
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}

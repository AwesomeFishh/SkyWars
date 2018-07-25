package me.awesomefishh.skywars.commands;

import me.awesomefishh.skywars.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommHelp extends SubCommand {

    private Main plugin;

    public CommHelp() {
        this.plugin = Main.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid args! /sw help");
            return;
        }

        sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "/sw help - " + ChatColor.YELLOW + "Displays this message!");
        if (sender.hasPermission("skywars.arena.create"))
            sender.sendMessage(plugin.getPrefix()+ChatColor.GOLD + "/sw arena create <arenaName> <maxPlayers> <minPlayers> - " + ChatColor.YELLOW + "Create an arena with your WorldEdit selection!");
        if(sender.hasPermission("skywars.arena.setspawn"))
            sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "/sw arena setspawn <arenaName> <spawnId>  - " + ChatColor.YELLOW + "Set a spawn to your current location!");
        if(sender.hasPermission("skywars.maintenance"))
            sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "/sw maintenance - " + ChatColor.YELLOW + "Enable or disable maintenance mode!");
        if(sender.hasPermission("skywars.setlobby"))
            sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "/sw setlobby - " + ChatColor.YELLOW + "Set the spawn to your current position!");

    }

    @Override
    public String name() {
        return plugin.getCommandManager().help;
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}

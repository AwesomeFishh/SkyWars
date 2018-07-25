package me.awesomefishh.skywars.commands;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.game.GameManager;
import me.awesomefishh.skywars.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommMaintenance extends SubCommand {

    private Main plugin;
    private GameManager gameManager;

    CommMaintenance() {
        this.plugin = Main.getInstance();
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (args.length != 0) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid args! /sw help");
            return;
        }

        if (!sender.hasPermission("skywars.maintenance")) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You don't have permission to do that!");
            return;
        }

        if (gameManager.getGameState().equals(GameState.INGAME) || gameManager.getGameState().equals(GameState.FINISHED)) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "The game has already started!");
            return;
        }

        //Disable maintenance mode and kick all players
        if (gameManager.getGameState().equals(GameState.MAINTENANCE)) {
            gameManager.setGameState(GameState.LOBBY_WAITING);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(ChatColor.RED + "Maintenance mode has been disabled, please join again!");
            }
        }
        //Else enable maintenance mode and kick all players
        else {
            gameManager.setGameState(GameState.MAINTENANCE);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(ChatColor.RED + "This server is now in maintenance mode!");
            }
        }

    }

    @Override
    public String name() {
        return plugin.getCommandManager().maintenance;
    }

    @Override
    public String[] aliases() {
        return new String[]{"main", "maint", "mt"};
    }
}

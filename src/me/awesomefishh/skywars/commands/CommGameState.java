package me.awesomefishh.skywars.commands;

import me.awesomefishh.skywars.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommGameState extends SubCommand {

    private Main plugin;

    public CommGameState() {
        this.plugin = Main.getInstance();
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Current GameState is: " + ChatColor.YELLOW + plugin.getGameManager().getGameState().toString());
    }

    @Override
    public String name() {
        return plugin.getCommandManager().gamestate;
    }

    @Override
    public String[] aliases() {
        return new String[]{"gs", "games", "gstate"};
    }
}

package me.awesomefishh.skywars.commands;

import me.awesomefishh.skywars.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class CommandManager implements CommandExecutor {

    private Main plugin;
    private ArrayList<SubCommand> commands = new ArrayList<SubCommand>();

    public CommandManager() {
        this.plugin = Main.getInstance();
    }

    //Subcommands && main command
    public String main = "skywars";
    public String help = "help";
    public String arena = "arena";
    public String maintenance = "maintenance";
    public String setlobby = "setlobby";
    public String gamestate = "gamestate";

    //Setup
    public void setup() {

        plugin.getCommand(main).setExecutor(this);

        this.commands.add(new CommHelp());
        this.commands.add(new CommArena());
        this.commands.add(new CommMaintenance());
        this.commands.add(new CommSetLobby());
        this.commands.add(new CommGameState());

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        //If command == /guild || /g
        if (command.getName().equalsIgnoreCase(main)) {

            //If args.length == 0
            if (args.length == 0) {
                SubCommand t = this.get("help");
                assert t != null;
                t.onCommand(sender, args);
                return true;
            }

            //Call the get(name) function, which returns the subcommand (Class Subcommand)
            SubCommand target = this.get(args[0]);

            //If the subcommand does not exist
            if (target == null) {
                //Send message: invalid subcommand && return
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid subcommand! /sw help");
                return true;
            }

            ArrayList<String> argslist = new ArrayList<>(Arrays.asList(args));
            argslist.remove(0);
            String[] argsarray = argslist.toArray(new String[argslist.size()]);

            try {
                //Call onCommand function in the SubCommand we got
                target.onCommand(sender, argsarray);
            } catch (Exception e) {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "An error has occurred! If this issue persists, please contact a staff member.");
                e.printStackTrace();
            }
        }

        return true;
    }

    private SubCommand get(String name) {
        Iterator<SubCommand> subcommands = this.commands.iterator();

        //While there's another subcommand
        while (subcommands.hasNext()) {
            SubCommand sc = subcommands.next();

            //If the currently iterated subcommand == passed name, return SubCommand sc
            if (sc.name().equalsIgnoreCase(name)) {
                return sc;
            }

            String[] aliases;
            //Get the amount of aliases of currently iterated subcommand
            int length = (aliases = sc.aliases()).length;

            //Loop through aliases array
            for (int i = 0; i < length; i++) {
                String alias = aliases[i];
                //If the passed name (which is args[0], which is the subcommand) is the same as one of the currently iterated subcommand's aliases, return that SubCommand sc
                if (name.equalsIgnoreCase(alias)) {
                    return sc;
                }
            }
        }
        return null;
    }

}

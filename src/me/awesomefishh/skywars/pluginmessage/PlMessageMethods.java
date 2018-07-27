package me.awesomefishh.skywars.pluginmessage;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlMessageMethods {

    private Main plugin;
    private GameManager gameManager;

    public PlMessageMethods() {
        plugin = Main.getInstance();
        gameManager = plugin.getGameManager();
    }

    void sendGameState(String serverName) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward");
        out.writeUTF(serverName);
        out.writeUTF("GameState");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);

        try {
            msgout.writeUTF(gameManager.getGameState().name());
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

    }

    void sendPlayerData(String serverName) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward");
        out.writeUTF(serverName);
        out.writeUTF("PlayerData");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);

        try {
            msgout.writeUTF(gameManager.getPlayersAlive().size() + "");
            msgout.writeUTF(plugin.getConfigManager().getArenaConfig().getInt("arenas." + gameManager.getChosenArena() + ".maxplayers") + "");
            msgout.writeUTF(gameManager.getPlayersSpectating().size() + "");
            msgout.writeUTF(gameManager.getPlayersDied().size() + "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

    }

    void sendArenaData(String serverName) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward");
        out.writeUTF(serverName);
        out.writeUTF("MapData");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);

        try {
            msgout.writeUTF(gameManager.getChosenArena());
            msgout.writeUTF(plugin.getConfigManager().getArenaConfig().getInt("arenas." + gameManager.getChosenArena() + ".minplayers") + "");
            msgout.writeUTF(plugin.getConfigManager().getArenaConfig().getInt("arenas." + gameManager.getChosenArena() + ".maxplayers") + "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

    }

}

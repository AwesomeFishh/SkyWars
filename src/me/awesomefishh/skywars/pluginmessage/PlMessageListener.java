package me.awesomefishh.skywars.pluginmessage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.awesomefishh.skywars.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PlMessageListener implements PluginMessageListener {

    private Main plugin;

    public PlMessageListener() {
        plugin = Main.getInstance();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

        if (!channel.equals("BungeeCord")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        //
        if (subchannel.equals("GetGameState")) {

            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

            try {
                String serverName = msgin.readUTF();
                plugin.getMessageMethods().sendGameState(serverName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //
        else if (subchannel.equals("GetPlayerData")) {

            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

            try {
                String serverName = msgin.readUTF();
                plugin.getMessageMethods().sendPlayerData(serverName);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //
        else if (subchannel.equals("GetArenaData")) {

            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

            try {
                String serverName = msgin.readUTF();
                plugin.getMessageMethods().sendArenaData(serverName);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}

package me.awesomefishh.skywars.database;

import me.awesomefishh.skywars.Main;
import me.awesomefishh.skywars.kits.KitMain;
import org.bukkit.entity.Player;

import java.sql.*;

public class DatabaseManager {

    private Main plugin;

    private Connection connection;
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    public DatabaseManager() {
        this.plugin = Main.getInstance();
    }

    //Try to establish connection with database
    public void establishConnection() {

        //Variables
        host = plugin.getConfig().getString("host");
        port = plugin.getConfig().getInt("port");
        database = plugin.getConfig().getString("database");
        username = plugin.getConfig().getString("username");
        password = plugin.getConfig().getString("password");

        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }

                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?user=" + username + "&password=" + password + "&autoReconnect=true");

                plugin.getLogger().info("Database connection established!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().info("////////////////////////////////////////////////////////////");
            plugin.getLogger().info("//// An error occurred while setting up mysql database! ////");
            plugin.getLogger().info("//// Make sure the database is online and               ////");
            plugin.getLogger().info("//// your credentials are configured correctly!         ////");
            plugin.getLogger().info("////////////////////////////////////////////////////////////");
        }
    }

    //Return connection
    public Connection getConnection() {
        return connection;
    }

    public String getPlayertable() {
        return "player_data";
    }

    public String getKitTable() {
        return "player_kits";
    }

    public void checkPlayer(Player player) {
        try {

            PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM " + getPlayertable() + " WHERE UUID='" + player.getUniqueId() + "'");
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                PreparedStatement insertPlayer = getConnection().prepareStatement("INSERT INTO " + getPlayertable() + " (UUID,COINS,KILLS,WINS,GAMESPLAYED) VALUES ('" + player.getUniqueId() + "',0,0,0,0)");
                insertPlayer.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            for (KitMain kit : plugin.getKitManager().values()) {
                PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM " + getKitTable() + " WHERE UUID='" + player.getUniqueId() + "' AND KIT='" + kit.getName() + "'");
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    PreparedStatement insertPlayer = getConnection().prepareStatement("INSERT INTO " + getKitTable() + " (UUID,KIT,LEVEL) VALUES ('" + player.getUniqueId() + "','" + kit.getName() + "',1)");
                    insertPlayer.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}

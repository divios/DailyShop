package io.github.divios.dailyrandomshop.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class sqlite {

    private static boolean first = false;
    public static Connection con;
    private static String connectionString;
    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();


    public static void getInstance() {
        if (!first) init();
        connect();
    }

    public static void init() {
        first = true;
        connectionString = "jdbc:sqlite:" + main.getDataFolder() + File.separator +
                main.getDescription().getName().toLowerCase() + ".db";
        try {
            Class.forName("org.sqlite.JDBC"); // This is required to put here for Spigot 1.10 and below to force class load
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            main.getLogger().severe("An error occurred closing the SQLite database connection: " + ex.getMessage());
        }
    }

    public static void connect() {
        if (con == null) {
            try {
                con = DriverManager.getConnection(connectionString);
            } catch (SQLException ex) {
                main.getLogger().severe("An error occurred retrieving the SQLite database connection: " + ex.getMessage());
                main.getServer().getPluginManager().disablePlugin(main);
            }
        }
    }

    public static Connection getConnection() {
        connect();
        return con;
    }
}

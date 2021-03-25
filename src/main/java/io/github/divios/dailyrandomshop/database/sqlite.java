package io.github.divios.dailyrandomshop.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;

class sqlite {

    private static boolean first = false;
    public static Connection con;
    private static String connectionString;
    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();


    public static void getInstance() {
        if (!first) init();
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

    private static void connect() {
        try {
            if (con != null) {
                con.close();
            }
            con = DriverManager.getConnection(connectionString);
        } catch (SQLException ex) {
            main.getLogger().severe("An error occurred retrieving the SQLite database connection: " + ex.getMessage());
            main.getServer().getPluginManager().disablePlugin(main);
        }
    }

    public static void connect(Callback c) {
        try {
            connect();
            c.accept(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public static Connection getConnection() {
        return con;
    }

    @FunctionalInterface
    public interface Callback {
        void accept(Connection connection) throws SQLException;
    }

}

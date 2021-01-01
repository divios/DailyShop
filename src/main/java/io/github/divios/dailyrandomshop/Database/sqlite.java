package io.github.divios.dailyrandomshop.Database;

import io.github.divios.dailyrandomshop.DailyRandomShop;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class sqlite {

    public Connection con;
    private final String connectionString;
    private final DailyRandomShop main;

    public sqlite(DailyRandomShop main) {
        this.main = main;
        connectionString = "jdbc:sqlite:" + main.getDataFolder() + File.separator +
                main.getDescription().getName().toLowerCase() + ".db";

        try {
            Class.forName("org.sqlite.JDBC"); // This is required to put here for Spigot 1.10 and below to force class load
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            main.getLogger().severe("An error occurred closing the SQLite database connection: " + ex.getMessage());
        }
    }

    public void connect()  {
        if (con == null) {
            try {
                con = DriverManager.getConnection(connectionString);
            } catch (SQLException ex) {
                main.getLogger().severe("An error occurred retrieving the SQLite database connection: " + ex.getMessage());


            }
        }
    }


}

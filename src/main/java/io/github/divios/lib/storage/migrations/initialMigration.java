package io.github.divios.lib.storage.migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class initialMigration {

    public static void migrate(Connection connection, String tablePrefix) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "active_shops (" +
                    "name varchar [255] PRIMARY KEY, " +
                    "type varchar [255], " +
                    "timestamp varchar [255], " +
                    "timer INTEGER, " +
                    "gui varchar [255]" +
                    ")");
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "log (" +
                    "player varchar [255], " +        // PlayerName
                    "shopID varchar [255], " +        // shopID
                    "itemUUID varchar [255], " +        // Item-UUID
                    "rawItem varchar [255], " +        // ItemName
                    "type varchar [255], " +              // Item Type
                    "price DOUBLE, " +                    // Item Price
                    "quantity INTEGER, " +                 // item quantity
                    "timestamp data" +
                    ")");
        }

    }

}

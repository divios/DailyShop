package io.github.divios.lib.storage.migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class initialMigration {

    public static void migrate(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {

            statement.addBatch("CREATE TABLE IF NOT EXISTS Shops(" +
                    "shop_id VARCHAR PRIMARY KEY," +
                    "shop_timer INT NOT NULL," +
                    "shop_timestamp TIMESTAMP NOT NULL" +
                    ");"
            );

            statement.addBatch("CREATE TABLE IF NOT EXISTS Guis(" +
                    "gui_serial VARCHAR NOT NULL," +
                    "shop_id INT UNSIGNED UNIQUE," +
                    "FOREIGN KEY(shop_id) REFERENCES shops(shop_id) " +
                    "ON DELETE CASCADE" +
                    ");"
            );

            statement.addBatch("CREATE TABLE IF NOT EXISTS Items(" +
                    "item_uuid VARCHAR NOT NULL," +
                    "item_serial VARCHAR NOT NULL," +
                    "shop_id INT UNSIGNED NOT NULL," +
                    "UNIQUE(item_uuid, shop_id), " +
                    "FOREIGN KEY(shop_id) REFERENCES shops(shop_id) " +
                    "ON DELETE CASCADE" +
                    ");"
            );

            statement.addBatch("CREATE TABLE IF NOT EXISTS Accounts(" +
                    "account_serial VARCHAR NOT NULL," +
                    "shop_id INT UNSIGNED UNIQUE," +
                    "FOREIGN KEY(shop_id) REFERENCES shops(shop_id) " +
                    "ON DELETE CASCADE" +
                    ");"
            );

            statement.executeBatch();
        }

    }

}

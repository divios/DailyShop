package io.github.divios.lib.storage.migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class initialMigration {

    public static void migrate(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {

            statement.addBatch("CREATE TABLE IF NOT EXISTS Shops(" +
                    "shop_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(30) NOT NULL," +
                    "timer INT NOT NULL," +
                    "timestamp TIMESTAMP NOT NULL" +
                    ");"
            );

            statement.addBatch("CREATE TABLE IF NOT EXISTS Guis(" +
                    "gui_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                    "content VARCHAR NOT NULL," +
                    "shop_id INT UNSIGNED," +
                    "FOREIGN KEY(shop_id) REFERENCES shops(shop_id)" +
                    ");"
            );

            statement.addBatch("CREATE TABLE IF NOT EXISTS Items(" +
                    "item_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                    "itemSerial VARCHAR NOT NULL," +
                    "uuid VARCHAR NOT NULL," +
                    "shop_id INT UNSIGNED," +
                    "FOREIGN KEY(shop_id) REFERENCES shops(shop_id)" +
                    ");"
            );

            statement.addBatch("CREATE TABLE IF NOT EXISTS Accounts(" +
                    "account_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                    "accountSerial VARCHAR NOT NULL," +
                    "shop_id INT UNSIGNED," +
                    "FOREIGN KEY(shop_id) REFERENCES shops(shop_id)" +
                    ");"
            );

            statement.executeBatch();
        }

    }

}

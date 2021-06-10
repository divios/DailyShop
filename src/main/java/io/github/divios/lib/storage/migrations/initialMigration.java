package io.github.divios.lib.storage.migrations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class initialMigration {

    public static void migrate(Connection connection, String tablePrefix) throws SQLException {

        // Create farms table.
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tablePrefix + "active_shops (" +
                    "name varchar [255], " +
                    "type varchar [255], " +
                    "timestamp varchar [255], " +
                    "timer INTEGER, " +
                    "gui varchar [255]" +
                    ")");
        }


    }

}

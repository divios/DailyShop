package io.github.divios.lib.storage;

import io.github.divios.core_lib.database.DataManagerAbstract;
import io.github.divios.core_lib.database.DatabaseConnector;
import io.github.divios.core_lib.database.SQLiteConnector;
import io.github.divios.core_lib.misc.timeStampUtils;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.lib.dLib.dGui;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.storage.migrations.initialMigration;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class dataManager extends DataManagerAbstract {

    private static final DRShop plugin = DRShop.getInstance();

    private static dataManager instance = null;

    private dataManager(DatabaseConnector connection, Plugin plugin) {
        super(connection);
    }

    public static dataManager getInstance() {
        if (instance == null) {
            instance = new dataManager(new SQLiteConnector(plugin), plugin);
            instance.databaseConnector.connect(connection ->
                    initialMigration.migrate(connection, instance.getTablePrefix()));
        }
        return instance;
    }


    public CompletableFuture<HashSet<dShop>> getShops() {

        return CompletableFuture.supplyAsync(() -> {

            HashSet<dShop> shops = new LinkedHashSet<>();

            this.databaseConnector.connect(connection -> {
                try (Statement statement = connection.createStatement()) {
                    String selectFarms = "SELECT * FROM " + this.getTablePrefix() + "active_shops";
                    ResultSet result = statement.executeQuery(selectFarms);

                    while (result.next()) {
                        String name = result.getString("name");
                        dShop shop = new dShop(name,
                                dShop.dShopT.valueOf(result.getString("type")),
                                result.getString("gui"),
                                timeStampUtils.deserialize(result.getString("timestamp")),
                                result.getInt("timer"));

                        getShop(name).thenAccept(shop::setItems);
                        shops.add(shop);
                    }
                }
            });
            return shops;
        });
    }


    public CompletableFuture<HashSet<dItem>> getShop(String name) {

        return CompletableFuture.supplyAsync(() -> {

            HashSet<dItem> items = new LinkedHashSet<>();

            this.databaseConnector.connect(connection -> {
                try (Statement statement = connection.createStatement()) {
                    String selectFarms = "SELECT * FROM " + this.getTablePrefix() + "shop_" + name;
                    ResultSet result = statement.executeQuery(selectFarms);

                    while (result.next()) {
                        dItem newItem = dItem.deserialize(result.getString("itemSerial"));
                        items.add(newItem);
                    }
                }
            });
            return items;
        });

    }

    public void createShop(dShop shop) {
        this.async(() -> this.databaseConnector.connect(connection -> {

            String createShop = "INSERT INTO " + this.getTablePrefix() +
                    "active_shops (name, type, gui, timestamp, timer) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {
                statement.setString(1, shop.getName());
                statement.setString(2, shop.getType().name());
                statement.setString(3, shop.getGui().serialize());
                statement.setString(4, timeStampUtils.serialize(shop.getTimestamp()));
                statement.setInt(5, shop.getTimer());
                statement.executeUpdate();
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "shop_"
                        + shop.getName() + "(" +
                        "itemSerial varchar [255], " +
                        "uuid varchar [255] " +
                        ")");
            }

        }));
    }

    public void renameShop(String oldName, String newName) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String renameShop = "UPDATE " + this.getTablePrefix() + "active_shops" +
                    " SET name = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(renameShop)) {
                statement.setString(1, newName);
                statement.setString(2, oldName);
                statement.executeUpdate();
            }

            String renameTable = "ALTER TABLE " + this.getTablePrefix() + "shop_" + oldName +
                    " RENAME TO " + this.getTablePrefix() + "shop_" + newName;
            try (Statement statement = connection.createStatement()) {
                statement.execute(renameTable);
            }

        }));
    }

    public void deleteShop(String name) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteShop = "DELETE FROM " + this.getTablePrefix() + "active_shops WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteShop)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE " + this.getTablePrefix() + "shop_" + name);
            }

        }));
    }

    public void addItem(String name, dItem item) {
        this.async(() -> this.databaseConnector.connect(connection -> {

            String createShop = "INSERT INTO " + this.getTablePrefix() +
                    "shop_" + name + " (itemSerial, uuid) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {


                statement.setString(1, item.serialize());
                statement.setString(2, item.getUid().toString());
                statement.executeUpdate();
            }

        }));
    }

    public void deleteItem(String name, UUID uid) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deeleteItem = "DELETE FROM " + this.getTablePrefix() + "shop_" + name + " WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(deeleteItem)) {
                statement.setString(1, uid.toString());
                statement.executeUpdate();
            }
        }));
    }

    public void updateItem(String name, dItem item) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String updateItem = "UPDATE " + this.getTablePrefix() + "shop_" + name +
                    " SET itemSerial = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateItem)) {
                statement.setString(1, item.serialize());
                statement.setString(2, item.getUid().toString());
                statement.executeUpdate();
            }
        }));
    }

    public void syncUpdateGui(String name, dGui gui) {
        this.databaseConnector.connect(connection -> {
            String updateGui = "UPDATE " + this.getTablePrefix() + "active_shops " +
                    "SET gui = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateGui)) {
                statement.setString(1, gui.serialize());
                statement.setString(2, name);
                statement.executeUpdate();
            }
        });
    }

    public void asyncUpdateGui(String name, dGui gui) {
        this.async(() -> syncUpdateGui(name, gui));
    }

    public void updateTimeStamp(String name, Timestamp timestamp) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String updateTimeStamp = "UPDATE " + this.getTablePrefix() + "active_shops " +
                    "SET timestamp = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateTimeStamp)) {
                statement.setString(1, timeStampUtils.serialize(timestamp));
                statement.setString(2, name);
                statement.executeUpdate();
            }
        }));
    }

    public void updateTimer(String name, int timer) {  //TODO: implementar cambiarlo en shopsGui 
        this.async(() -> this.databaseConnector.connect(connection -> {
            String updateTimeStamp = "UPDATE " + this.getTablePrefix() + "active_shops " +
                    "SET timer = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateTimeStamp)) {
                statement.setInt(1, timer);
                statement.setString(2, name);
                statement.executeUpdate();
            }
        }));
    }


}

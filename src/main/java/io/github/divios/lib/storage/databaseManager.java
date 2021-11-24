package io.github.divios.lib.storage;

import io.github.divios.core_lib.database.DataManagerAbstract;
import io.github.divios.core_lib.database.DatabaseConnector;
import io.github.divios.core_lib.database.SQLiteConnector;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.timeStampUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FutureUtils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.log.options.dLogEntry;
import io.github.divios.lib.dLib.synchronizedGui.syncMenu;
import io.github.divios.lib.storage.migrations.initialMigration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class databaseManager extends DataManagerAbstract {

    private static final DailyShop plugin = DailyShop.getInstance();

    private static databaseManager instance = null;

    private databaseManager(DatabaseConnector connection) {
        super(connection);
    }

    public static databaseManager getInstance() {
        if (instance == null) {
            instance = new databaseManager(new SQLiteConnector(plugin));
            instance.databaseConnector.connect(connection -> initialMigration.migrate(connection, instance.getTablePrefix()));
        }
        return instance;
    }

    public CompletableFuture<Set<dShop>> getShops() {
        return CompletableFuture.supplyAsync(() -> {

            Set<dShop> shops = new LinkedHashSet<>();
            Log.info("mmm1");
            this.databaseConnector.connect(connection -> {
                try (Statement statement = connection.createStatement()) {
                    String selectFarms = "SELECT * FROM " + this.getTablePrefix() + "active_shops";
                    ResultSet result = statement.executeQuery(selectFarms);

                    Log.info("mmm2");
                    while (result.next()) {
                        String name = result.getString("name");
                        dShop shop = new dShop(name,
                                dShop.dShopT.valueOf(result.getString("type")),
                                result.getString("gui"),
                                timeStampUtils.deserialize(result.getString("timestamp")),
                                result.getInt("timer"),
                                FutureUtils.waitFor(getShop(name)));

                        shops.add(shop);
                    }
                }
            });
            Log.info("mmm3");
            return shops;
        });
    }


    public CompletableFuture<Set<dItem>> getShop(String name) {
        return CompletableFuture.supplyAsync(() -> {

            Set<dItem> items = new LinkedHashSet<>();

            this.databaseConnector.connect(connection -> {
                try (Statement statement = connection.createStatement()) {
                    String selectFarms = "SELECT * FROM " + this.getTablePrefix() + "shop_" + name;
                    ResultSet result = statement.executeQuery(selectFarms);

                    while (result.next()) {
                        dItem newItem = dItem.fromBase64(result.getString("itemSerial"));
                        items.add(newItem);
                    }
                }
            });
            return items;
        });

    }

    public CompletableFuture<Void> createShop(dShop shop) {
        Log.severe("oke");
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {

            String createShop = "INSERT OR REPLACE INTO " + this.getTablePrefix() +
                    "active_shops (name, type, gui, timestamp, timer) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {
                statement.setString(1, shop.getName());
                statement.setString(2, shop.getType().name());
                statement.setString(3, shop.getGuis().toJson());
                statement.setString(4, timeStampUtils.serialize(shop.getTimestamp()));
                statement.setInt(5, shop.getTimer());
                statement.executeUpdate();
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "shop_"
                        + shop.getName() + "(" +
                        "itemSerial varchar [255], " +
                        "uuid varchar [255] PRIMARY KEY" +
                        ")");
            }

        }));
    }

    public CompletableFuture<Void> renameShop(String oldName, String newName) {
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {
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

    public CompletableFuture<Void> deleteShop(String name) {
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {
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

    public CompletableFuture<Void> addItem(String name, dItem item) {
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {

            String createShop = "INSERT OR REPLACE INTO " + this.getTablePrefix() +
                    "shop_" + name + " (itemSerial, uuid) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {


                statement.setString(1, item.toBase64());
                statement.setString(2, item.getUid().toString());
                statement.executeUpdate();
            }

        }));
    }

    public CompletableFuture<Void> deleteItem(String shopName, UUID uid) {
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {
            String deeleteItem = "DELETE FROM " + this.getTablePrefix() + "shop_" + shopName + " WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(deeleteItem)) {
                statement.setString(1, uid.toString());
                statement.executeUpdate();
            }
        }));
    }

    public CompletableFuture<Void> deleteAllItems(String shopName) {
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {

            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE " + this.getTablePrefix() + "shop_" + shopName);
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS " + this.getTablePrefix() + "shop_"
                        + shopName + "(" +
                        "itemSerial varchar [255] , " +
                        "uuid varchar [255] PRIMARY KEY" +
                        ")");
            }

        }));
    }

    public CompletableFuture<Void> updateItem(String name, dItem item) {
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {
            String updateItem = "UPDATE " + this.getTablePrefix() + "shop_" + name +
                    " SET itemSerial = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateItem)) {
                statement.setString(1, item.toBase64());
                statement.setString(2, item.getUid().toString());
                statement.executeUpdate();
            }
        }));
    }

    public void syncUpdateGui(String name, syncMenu gui) {
        this.databaseConnector.connect(connection -> {
            String updateGui = "UPDATE " + this.getTablePrefix() + "active_shops " +
                    "SET gui = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateGui)) {
                statement.setString(1, gui.toJson());
                statement.setString(2, name);
                statement.executeUpdate();
            }
        });
    }

    public CompletableFuture<Void> asyncUpdateGui(String name, syncMenu gui) {
        return CompletableFuture.runAsync(() -> syncUpdateGui(name, gui));
    }

    public CompletableFuture<Void> updateTimeStamp(String name, Timestamp timestamp) {
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {
            String updateTimeStamp = "UPDATE " + this.getTablePrefix() + "active_shops " +
                    "SET timestamp = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateTimeStamp)) {
                statement.setString(1, timeStampUtils.serialize(timestamp));
                statement.setString(2, name);
                statement.executeUpdate();
            }
        }));
    }

    public CompletableFuture<Void> updateTimer(String name, int timer) {
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {
            String updateTimeStamp = "UPDATE " + this.getTablePrefix() + "active_shops " +
                    "SET timer = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateTimeStamp)) {
                statement.setInt(1, timer);
                statement.setString(2, name);
                statement.executeUpdate();
            }
        }));
    }

    public CompletableFuture<Void> addLogEntry(dLogEntry entry) {
        return CompletableFuture.runAsync(() -> this.databaseConnector.connect(connection -> {

            String createShop = "INSERT INTO " + this.getTablePrefix() +
                    "log" + " (player, shopID, itemUUID, rawItem, type, price, quantity, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {

                statement.setString(1, entry.getPlayer());
                statement.setString(2, entry.getShopID());
                statement.setString(3, entry.getItemUUID().toString());
                statement.setString(4, ItemUtils.serialize(entry.getRawItem()));
                statement.setString(5, entry.getType().name());
                statement.setDouble(6, entry.getPrice());
                statement.setInt(7, entry.getQuantity());
                statement.setString(8, new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(entry.getTimestamp()));
                statement.executeUpdate();
            }

        }));
    }

    public CompletableFuture<Collection<dLogEntry>> getEntries() {

        Deque<dLogEntry> entries = new ArrayDeque<>();
        return CompletableFuture.supplyAsync(() -> {
            this.databaseConnector.connect(connection -> {

                try (Statement statement = connection.createStatement()) {
                    String getLogs = "SELECT * FROM " + this.getTablePrefix() + "log";
                    ResultSet result = statement.executeQuery(getLogs);

                    while (result.next()) {

                        Date timestamp = null;

                        try {
                            timestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(result.getString("timestamp"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        dLogEntry entry = dLogEntry.builder()
                                .withPlayer(result.getString("player"))
                                .withShopID(result.getString("shopID"))
                                .withItemUUID(result.getString("itemUUID"))
                                .withRawItem(ItemUtils.deserialize(result.getString("rawItem")))
                                .withType(dShop.dShopT.valueOf(result.getString("type")))
                                .withPrice(result.getDouble("price"))
                                .withQuantity(result.getInt("quantity"))
                                .withTimestamp(timestamp)
                                .build();

                        entries.push(entry);
                    }
                }
            });

            return entries;
        });

    }


}

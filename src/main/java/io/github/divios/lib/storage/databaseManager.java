package io.github.divios.lib.storage;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.github.divios.core_lib.database.DataManagerAbstract;
import io.github.divios.core_lib.database.SQLiteConnector;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.timeStampUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.ShopGui;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.registry.RecordBookEntry;
import io.github.divios.lib.managers.WrappedShop;
import io.github.divios.lib.storage.migrations.initialMigration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class databaseManager extends DataManagerAbstract {

    private static final DailyShop plugin = DailyShop.get();

    protected final ExecutorService asyncPool = Executors.newSingleThreadExecutor();

    public databaseManager() {
        super(new SQLiteConnector(plugin));
        super.databaseConnector.connect(connection -> initialMigration.migrate(connection, super.getTablePrefix()));
    }

    public Set<dShop> getShops() {

        Set<dShop> shops = new LinkedHashSet<>();

        this.databaseConnector.connect(connection -> {
            try (Statement statement = connection.createStatement()) {

                String selectFarms = "SELECT * FROM " + this.getTablePrefix() + "active_shops";
                ResultSet result = statement.executeQuery(selectFarms);

                JsonParser parser = new JsonParser();
                while (result.next()) {
                    String name = result.getString("name");
                    dShop shop;

                    try {
                        shop = new WrappedShop(name,
                                parser.parse(result.getString("gui")),
                                timeStampUtils.deserialize(result.getString("timestamp")),
                                result.getInt("timer"),
                                getShopItems(name));
                    } catch (Exception | Error e) {
                        //e.printStackTrace();
                        DebugLog.info(e.getMessage());
                        shop = new WrappedShop(name,
                                result.getInt("timer"),
                                timeStampUtils.deserialize(result.getString("timestamp")));
                    }
                    //shop.destroy();
                    shops.add(shop);
                }
            }
        });
        return shops;
    }

    public CompletableFuture<Set<dShop>> getShopsAsync() {
        return CompletableFuture.supplyAsync(this::getShops);
    }


    public Set<dItem> getShopItems(String name) {
        Set<dItem> items = new LinkedHashSet<>();

        this.databaseConnector.connect(connection -> {
            try (Statement statement = connection.createStatement()) {

                String selectFarms = "SELECT * FROM " + this.getTablePrefix() + "shop_" + name;
                ResultSet result = statement.executeQuery(selectFarms);

                JsonParser parser = new JsonParser();
                while (result.next()) {
                    try {
                        JsonElement json = parser.parse(result.getString("itemSerial"));
                        items.add(dItem.fromJson(json));
                    } catch (IllegalStateException | JsonSyntaxException ignored) {
                    }
                }
            }
        });
        return items;
    }

    public CompletableFuture<Set<dItem>> getShopItemsAsync(String name) {
        return CompletableFuture.supplyAsync(() -> getShopItems(name));

    }

    public void createShop(dShop shop) {
        this.databaseConnector.connect(connection -> {

            String createShop = "INSERT OR REPLACE INTO " + this.getTablePrefix() +
                    "active_shops (name, type, gui, timestamp, timer) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {
                statement.setString(1, shop.getName());
                statement.setString(3, shop.getGui().toJson().toString());
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
            shop.getItems().forEach(dItem -> addItem(shop.getName(), dItem));
        });
    }

    public Future<?> createShopAsync(dShop shop) {
        return asyncPool.submit(() -> createShop(shop));
    }

    public void renameShop(String oldName, String newName) {
        this.databaseConnector.connect(connection -> {
            String renameShop = "UPDATE " + this.getTablePrefix() + "active_shops" +
                    " SET name = ? WHERE name = ? collate nocase";
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
        });
    }

    public Future<?> renameShopAsync(String oldName, String newName) {
        return asyncPool.submit(() -> renameShop(oldName, newName));
    }

    public void deleteShop(String name) {
        this.databaseConnector.connect(connection -> {
            String deleteShop = "DELETE FROM " + this.getTablePrefix() + "active_shops WHERE name = ? collate nocase";
            try (PreparedStatement statement = connection.prepareStatement(deleteShop)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE " + this.getTablePrefix() + "shop_" + name);
            }
        });
    }

    public Future<?> deleteShopAsync(String name) {
        return asyncPool.submit(() -> deleteShop(name));
    }

    public void addItem(String name, dItem item) {
        this.databaseConnector.connect(connection -> {

            String createShop = "INSERT OR REPLACE INTO " + this.getTablePrefix() +
                    "shop_" + name + " (itemSerial, uuid) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {

                statement.setString(1, item.toJson().toString());
                statement.setString(2, item.getUUID().toString());
                statement.executeUpdate();
            }
        });
    }

    public Future<?> addItemAsync(String name, dItem item) {
        return asyncPool.submit(() -> addItem(name, item));
    }

    public void deleteItem(String shopName, UUID uid) {
        this.databaseConnector.connect(connection -> {
            String deleteItem = "DELETE FROM " + this.getTablePrefix() + "shop_" + shopName + " WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteItem)) {
                statement.setString(1, uid.toString());
                statement.executeUpdate();
            }
        });
    }

    public Future<?> deleteItemAsync(String shopName, UUID uid) {
        return asyncPool.submit(() -> deleteItem(shopName, uid));
    }

    public void deleteAllItems(String shopName) {
        this.databaseConnector.connect(connection -> {

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
        });
    }

    public Future<?> deleteAllItemsAsync(String shopName) {
        return asyncPool.submit(() -> deleteAllItems(shopName));
    }

    public void updateItem(String name, dItem item) {
        this.databaseConnector.connect(connection -> {
            String updateItem = "UPDATE " + this.getTablePrefix() + "shop_" + name +
                    " SET itemSerial = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateItem)) {
                statement.setString(1, item.toJson().toString());
                statement.setString(2, item.getUUID().toString());
                statement.executeUpdate();
            }
        });
    }

    public Future<?> updateItemAsync(String name, dItem item) {
        return asyncPool.submit(() -> updateItem(name, item));
    }

    public void updateGui(String name, ShopGui gui) {
        this.databaseConnector.connect(connection -> {
            String updateGui = "UPDATE " + this.getTablePrefix() + "active_shops " +
                    "SET gui = ? WHERE name = ? collate nocase";
            try (PreparedStatement statement = connection.prepareStatement(updateGui)) {
                statement.setString(1, gui.toJson().toString());
                statement.setString(2, name);
                statement.executeUpdate();
            }
        });
    }

    public Future<?> updateGuiAsync(String name, ShopGui gui) {
        return asyncPool.submit(() -> updateGui(name, gui));
    }

    public void updateTimeStamp(String name, Timestamp timestamp) {
        this.databaseConnector.connect(connection -> {
            String updateTimeStamp = "UPDATE " + this.getTablePrefix() + "active_shops " +
                    "SET timestamp = ? WHERE name = ? collate nocase";
            try (PreparedStatement statement = connection.prepareStatement(updateTimeStamp)) {
                statement.setString(1, timeStampUtils.serialize(timestamp));
                statement.setString(2, name);
                statement.executeUpdate();
            }
        });
    }

    public Future<?> updateTimeStampAsync(String name, Timestamp timestamp) {
        return asyncPool.submit(() -> updateTimeStamp(name, timestamp));
    }

    public void updateTimer(String name, int timer) {
        this.databaseConnector.connect(connection -> {
            String updateTimeStamp = "UPDATE " + this.getTablePrefix() + "active_shops " +
                    "SET timer = ? WHERE name = ? collate nocase";
            try (PreparedStatement statement = connection.prepareStatement(updateTimeStamp)) {
                statement.setInt(1, timer);
                statement.setString(2, name);
                statement.executeUpdate();
            }
        });
    }

    public Future<?> updateTimerAsync(String name, int timer) {
        return asyncPool.submit(() -> updateTimer(name, timer));
    }

    public void addLogEntry(RecordBookEntry entry) {
        this.databaseConnector.connect(connection -> {

            String createShop = "INSERT INTO " + this.getTablePrefix() +
                    "log" + " (player, shopID, itemUUID, rawItem, type, price, quantity, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {

                statement.setString(1, entry.getPlayer());
                statement.setString(2, entry.getShopID());
                statement.setString(3, entry.getItemID());
                statement.setString(4, ItemUtils.serialize(entry.getRawItem()));
                statement.setString(5, entry.getType().name());
                statement.setDouble(6, entry.getPrice());
                statement.setInt(7, entry.getQuantity());
                statement.setString(8, new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(entry.getTimestamp()));
                statement.executeUpdate();
            }
        });
    }

    public Future<?> addLogEntryAsync(RecordBookEntry entry) {
        return asyncPool.submit(() -> addLogEntry(entry));
    }

    public Collection<RecordBookEntry> getLogEntries() {

        Deque<RecordBookEntry> entries = new ArrayDeque<>();
        this.databaseConnector.connect(connection -> {

            try (Statement statement = connection.createStatement()) {
                String getLogs = "SELECT * FROM " + this.getTablePrefix() + "log";
                ResultSet result = statement.executeQuery(getLogs);

                while (result.next()) {

                    Date timestamp = null;

                    try {
                        timestamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                                .parse(result.getString("timestamp"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    RecordBookEntry entry = RecordBookEntry.createEntry()
                            .withPlayer(result.getString("player"))
                            .withShopID(result.getString("shopID"))
                            .withItemID(result.getString("itemUUID"))
                            .withRawItem(ItemUtils.deserialize(result.getString("rawItem")))
                            .withType(Transactions.Type.valueOf(result.getString("type").toUpperCase()))
                            .withPrice(result.getDouble("price"))
                            .withQuantity(result.getInt("quantity"))
                            .withTimestamp(timestamp == null ? new Timestamp(System.currentTimeMillis()) : timestamp)
                            .create();

                    entries.push(entry);
                }
            }
        });
        return entries;
    }

    public CompletableFuture<Collection<RecordBookEntry>> getLogEntriesAsync() {
        return CompletableFuture.supplyAsync(this::getLogEntries);
    }

    public void finishAsyncQueries() {
        asyncPool.shutdown();
        try {
            asyncPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

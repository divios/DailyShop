package io.github.divios.lib.storage;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.storage.migrations.initialMigration;

import javax.security.auth.callback.Callback;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class dataManager {

    private static dataManager instance = null;
    private static final DataManagerAbstract con = DataManagerAbstract.getInstance();

    public static dataManager getInstance() {
        if (instance == null) {
            instance = new dataManager();
            con.databaseConnector.connect(connection ->
                    initialMigration.migrate(connection, con.getTablePrefix()));
        }
        return instance;
    }


    public synchronized void getShops(Consumer<HashSet<dShop>> callback) {

            HashSet<dShop> shops = new LinkedHashSet<>();

            con.async(() -> con.databaseConnector.connect(connection -> {
                try (Statement statement = connection.createStatement()) {
                    String selectFarms = "SELECT * FROM " + con.getTablePrefix() + "active_shops";
                    ResultSet result = statement.executeQuery(selectFarms);

                    while (result.next()) {
                        String name = result.getString("name");
                        dShop shop = new dShop(name,
                                dShop.dShopT.valueOf(result.getString("type")));

                       getShop(name, shop::setItems);
                       shops.add(shop);
                    }
                }
                callback.accept(shops);
            }));
    }


    public synchronized void getShop(String name, Consumer<HashSet<dItem>> callback) {

            HashSet<dItem> items = new LinkedHashSet<>();

            con.async(() -> con.databaseConnector.connect(connection -> {
                try (Statement statement = connection.createStatement()) {
                    String selectFarms = "SELECT * FROM " + con.getTablePrefix() + "shop_" + name;
                    ResultSet result = statement.executeQuery(selectFarms);

                    while (result.next()) {
                        dItem newItem = dItem.constructFromBase64(result.getString("itemSerial"));
                       items.add(newItem);
                    }
                }
                callback.accept(items);
            }));
        }

    public synchronized void createShop(String name, dShop.dShopT type) {
        con.async(() -> con.databaseConnector.connect(connection -> {

            String createShop = "INSERT INTO " + con.getTablePrefix() +
                    "active_shops (name, type) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {
                statement.setString(1, name);
                statement.setString(2, type.name());
                statement.executeUpdate();
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS " + con.getTablePrefix() + "shop_"
                        + name + "(" +
                        "itemSerial varchar [255], " +
                        "uuid varchar [255] " +
                        ")");
            }

        }));
    }

    public synchronized void deleteShop(String name) {
        con.async(() -> con.databaseConnector.connect(connection -> {
            String deleteShop = "DELETE FROM " + con.getTablePrefix() + "active_shops WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteShop)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP TABLE " + con.getTablePrefix() + "shop_" + name);
            }

        }));
    }

    public synchronized void addItem(String name, dItem item) {
        con.queueAsync(() -> con.databaseConnector.connect(connection -> {

            String createShop = "INSERT INTO " + con.getTablePrefix() +
                    "shop_" + name + " (itemSerial, uuid) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createShop)) {


                statement.setString(1, item.getItemSerial());
                statement.setString(2, item.getUid().toString());
                statement.executeUpdate();
            }

        }), "add");
    }

    public synchronized void deleteItem(String name, UUID uid) {
        con.queueAsync(() -> con.databaseConnector.connect(connection -> {
            String deeleteItem = "DELETE FROM " + con.getTablePrefix() + "shop_" + name + " WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(deeleteItem)) {
                statement.setString(1, uid.toString());
                statement.executeUpdate();
            }
        }), "delete");
    }

    public synchronized void updateItem(String name, dItem item) {
        con.queueAsync(() -> con.databaseConnector.connect(connection -> {
            String updateItem = "UPDATE " + con.getTablePrefix() + "shop_" + name +
                    " SET itemSerial = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateItem)) {
                statement.setString(1, item.getItemSerial());
                statement.setString(2, item.getUid().toString());
                statement.executeUpdate();
            }
        }), "update");
    }


}

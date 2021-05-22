package io.github.divios.lib.storage;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import io.github.divios.lib.storage.migrations.initialMigration;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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


    public CompletableFuture<HashSet<dShop>> getShops() {
        HashSet<dShop> shops = new LinkedHashSet<>();

        try {
            con.async(() -> con.databaseConnector.connect(connection -> {
                try (Statement statement = connection.createStatement()) {
                    String selectFarms = "SELECT * FROM " + con.getTablePrefix() + "active_shops";
                    ResultSet result = statement.executeQuery(selectFarms);

                    String name = result.getString("name");

                    while(result.next()) {
                        dShop shop = new dShop(name,
                                dShop.dShopT.valueOf(result.getString("type")));

                        try {
                            shop.setItems(getShop(name).get());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        shops.add(shop);
                    }
                }
            })).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(shops);

    }


    public CompletableFuture<HashSet<dItem>> getShop(String name) {
        HashSet<dItem> items = new LinkedHashSet<>();
        try {
            con.async(() -> con.databaseConnector.connect(connection -> {
                try (Statement statement = connection.createStatement()) {
                    String selectFarms = "SELECT * FROM " + con.getTablePrefix() + "shop_" + name;
                    ResultSet result = statement.executeQuery(selectFarms);

                    while(result.next()) {
                        try {
                            byte[] itemSerial = Base64.getDecoder().decode(result.getString("itemSerial"));
                            ByteArrayInputStream bs = new ByteArrayInputStream(itemSerial);
                            ObjectInputStream is = new ObjectInputStream(bs);

                            dItem item = (dItem) is.readObject();
                            items.add(item);

                        } catch (Exception e) {
                            DRShop.getInstance().getLogger().warning("A previous sell item registered " +
                                    "on the db is now unsupported, skipping...");
                        }
                    }
                }
            })).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(items);
    }


    public void createShop(String name, dShop.dShopT type) {
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

    public void deleteShop(String name) {
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

    public void addItem(String name, dItem item) {
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

    public void deleteItem(String name, dItem item) {
        con.queueAsync(() -> con.databaseConnector.connect(connection -> {
            String deeleteItem = "DELETE FROM " + con.getTablePrefix() + "shop_" + name + " WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(deeleteItem)) {
                statement.setString(1, item.getUid().toString());
                statement.executeUpdate();
            }
        }), "delete");
    }

    public void updateItem(String name, dItem item) {
        con.queueAsync(() -> con.databaseConnector.connect(connection -> {
            String updateItem = "UPDATE FROM " + con.getTablePrefix() + "shop_" + name +
                    "SET itemSerial = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateItem)) {
                statement.setString(1, item.getItemSerial());
                statement.setString(2, item.getUid().toString());
            }
        }), "update");
    }





}

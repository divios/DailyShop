package io.github.divios.dailyrandomshop.database;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.utils.conf_updater;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class dataManager {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static dataManager instance = null;
    public Map<ItemStack, Double> listDailyItems, listSellItems;
    public Map<String, Integer>  currentItems;
    public int listDailyItemsHash, listSellItemsHash, currentItemsHash;

    private dataManager() {
    }

    public static dataManager getInstance() {
        if (instance == null) init(new BukkitRunnable() {
            @Override
            public void run() {
                buyGui.getInstance();
            }
        });
        return instance;
    }

    private static void init(BukkitRunnable c) {
        instance = new dataManager();
        sqlite.getInstance();
        try {
            files.createdb();
        } catch (IOException e) {
            main.getLogger().severe("Couldn't load databases");
            e.printStackTrace();
            main.getServer().getPluginManager().disablePlugin(main);
        }

        utils.sync(() -> {
            instance.createTables();
            instance.getBuyItems();
            instance.getSellItems();
            instance.getSyncCurrentItems();

            instance.listDailyItemsHash = instance.listDailyItems.hashCode();
            instance.listSellItemsHash = instance.listSellItems.hashCode();

            c.run();
        });
    }

    public void createTables() {
        sqlite.connect(connection -> {
            try (Statement statement = connection.createStatement()){

                statement.execute("CREATE TABLE IF NOT EXISTS timer"
                        + "(time int);");

                statement.execute("CREATE TABLE IF NOT EXISTS sell_items"
                        + "(serial varchar [255], price int);");

                statement.execute("CREATE TABLE IF NOT EXISTS daily_items"
                        + "(serial varchar [255], price int);");

                statement.execute("CREATE TABLE IF NOT EXISTS current_items"
                        + "(uuid varchar [255], amount int);");
            }
        });
    }

    public int getTimer() {
        AtomicInteger time = new AtomicInteger();
        String selectTimer = "SELECT * FROM timer";
        sqlite.connect(connection -> {
            try {
                PreparedStatement statement =
                        connection.prepareStatement(selectTimer);

                ResultSet result = statement.executeQuery();

                result.next();
                time.set(result.getInt("time"));
            } catch (SQLException ignored) {}
        });
        return time.get();
    }

    private void updateAbstractTimer(int time) {
        String updateTimer = "INSERT INTO timer (time) VALUES (?)";
        deleteElements("timer");
        sqlite.connect(connection -> {
            try (PreparedStatement statement =
                         connection.prepareStatement(updateTimer)) {

                statement.setInt(1, time);
                statement.executeUpdate();

            }
        });
    }

    public void updateTimer(int time) {
        updateAbstractTimer(time);
    }

    public void updateSellItems() {
        AbstractUpdateList(listSellItems, "sell_items");
        listSellItemsHash = listSellItems.hashCode();
    }

    public void getSellItems() {
        listSellItems = AbstractGetList("sell_items", false);
    }

    public void updateBuyItems() {
        AbstractUpdateList(listDailyItems, "daily_items");
        listDailyItemsHash = listDailyItems.hashCode();
    }

    public void getBuyItems() {
        listDailyItems = AbstractGetList("daily_items", true);
    }

    public void getSyncCurrentItems() {
        currentItems = getCurrentItems();
    }

    public void updateCurrentItems() {
        abstractUpdateCurrentItems();
        currentItemsHash = buyGui.getInstance().getCurrentItemsHash();
    }


    private Map<ItemStack, Double> AbstractGetList(String table, boolean isDailyItems) {
        Map<ItemStack, Double> items = Collections.synchronizedMap(new LinkedHashMap<>());
        String SQL_Create = "SELECT * FROM " + table;

        sqlite.connect(connection -> {
            try (PreparedStatement statement =
                         connection.prepareStatement(SQL_Create)) {

                ResultSet result = statement.executeQuery();
                String string;
                NBTCompound itemData;
                ItemStack item;
                byte[] itemserial;

                while (result.next()) {
                    itemserial = Base64.getDecoder().decode(result.getString("serial"));
                    try {
                        string = new String(itemserial);
                        itemData = new NBTContainer(string);
                        item = NBTItem.convertNBTtoItem(itemData);
                        if (utils.isEmpty(item)) continue;
                        if (isDailyItems) {
                            if (utils.isEmpty(dailyItem.getUuid(item))) continue;
                        }

                        try {
                            Material.valueOf(item.getType().toString());
                        } catch (Exception e) {
                            continue;
                        }

                    } catch (Exception e) {
                        main.getLogger().warning("A previous sell item registered " +
                                "on the db is now unsupported, skipping...");
                        continue;
                    }

                    if (utils.isEmpty(dailyItem.getUuid(item))) {
                        new dailyItem(item).craft();
                    }

                    if (table.equals("daily_items") && conf_updater.priceFormat)
                        new dailyItem(item).addNbt(dailyItem.dailyMetadataType.rds_price,
                                new dailyItem.dailyItemPrice(result.getDouble(2)))
                                .getItem();

                    items.put(item, result.getDouble(2));
                }

            }
        });

        return items;
    }

    private void AbstractUpdateList(Map<ItemStack, Double> list, String table) {
        deleteElements(table);
        String updateItem = "INSERT INTO " + table + " (serial, price) VALUES (?, ?)";

        sqlite.connect(connection -> {
            try (PreparedStatement statement =
                         connection.prepareStatement(updateItem)) {

                for (Map.Entry<ItemStack, Double> entry : list.entrySet()) {

                    NBTCompound itemData = NBTItem.convertItemtoNBT(entry.getKey());
                    String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                    statement.setString(1, base64);
                    statement.setDouble(2, entry.getValue());

                    statement.executeUpdate();
                }

            }
        });
    }

    public Map<String, Integer> getCurrentItems() {
        Map<String, Integer> items = new LinkedHashMap<>();
        String SQL_Create = "SELECT * FROM current_items";

        sqlite.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(SQL_Create);) {
                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    items.put(result.getString(1),
                            result.getInt(2));
                }

            }
        });
        return items;
    }

    public void abstractUpdateCurrentItems() {
        currentItems = buyGui.getInstance().getCurrentItems();
        String insertItem = "INSERT INTO " + "current_items (uuid, amount) VALUES (?, ?)";
        deleteElements("current_items");

        sqlite.connect(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(insertItem)) {

                for (Map.Entry<String, Integer> s : currentItems.entrySet()) {

                    statement.setString(1, s.getKey());
                    statement.setInt(2, s.getValue());
                    statement.executeUpdate();
                }

            }
        });
    }

    public void deleteElements(String table) {
        String deleteTable = "DELETE FROM " + table + ";";
        sqlite.connect(connection -> {
            try (PreparedStatement statement =
                         connection.prepareStatement(deleteTable)){

                statement.executeUpdate();
            }
        });
    }


}
package io.github.divios.dailyrandomshop.Database;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DataManager {

    private final sqlite db;
    private final DailyRandomShop main;

    public DataManager(sqlite db, DailyRandomShop main) {
        this.db = db;
        this.main = main;
    }

    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(main, runnable);
    }

    /**
     * Queue a task to be run synchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(main, runnable);
    }


    public void createTables() throws SQLException {
        db.connect();

        //Create timer
        try (Statement statement = db.con.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS timer"
                    + "(time int, id int);");

        }

        // Create sell_items
        try (Statement statement = db.con.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS sell_items"
                    + "(material varchar [255], price int);");
        }

        // Create buy_items
        try (Statement statement = db.con.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS daily_items"
                    + "(material varchar [255], price int);");
        }

        //create current_items
        try (Statement statement = db.con.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS current_items"
                    + "(material varchar [255]);");
        }

    }

    public int getTimer() {

        int time = -1;
        db.connect();
        try {
            String selectTimer = "SELECT time FROM " + "timer" + " WHERE id = 1";
            PreparedStatement statement = db.con.prepareStatement(selectTimer);
            ResultSet result = statement.executeQuery();

            result.next();
            time = result.getInt("time");
        } catch (SQLException e) {
            main.getLogger().warning("Couldn't read timer value from database, setting it to value on config");
        }
        return time;
    }


    public boolean isPoblated() {

        final boolean[] res = {false};

        try {
            db.connect();
            String selectTimer = "SELECT * FROM timer";
            PreparedStatement statement = db.con.prepareStatement(selectTimer);
            ResultSet result = statement.executeQuery();

            boolean resulta = result.next();

            res[0] = resulta;
        } catch (SQLException e) {
            main.getLogger().warning("Somethings went wrong while checking if timer table is poblated");
        }

        return res[0];
    }

    public void updateTimer(int time) {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();
                PreparedStatement statement;
                boolean result = isPoblated();
                if (!result) {
                    String SQL_Create = "INSERT INTO " + "timer (time, id) VALUES (?, ?)";
                    statement = db.con.prepareStatement(SQL_Create);
                    statement.setInt(1, time);
                    statement.setInt(2, 1);
                    statement.executeUpdate();
                    return;
                }

                String updateTimer = "UPDATE " + "timer SET time = ? WHERE id = ?";
                statement = db.con.prepareStatement(updateTimer);
                statement.setInt(1, time);
                statement.setInt(2, 1);
                statement.executeUpdate();

            } catch (SQLException e) {
                main.getLogger().warning("Couldn't update timer on database");
            }
        });
    }

    public void updateCurrentItems() {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();
                PreparedStatement statement;

                //Quitamos los elementos previos
                String deleteTable = "DELETE FROM " + "current_items;";
                statement = db.con.prepareStatement(deleteTable);
                statement.executeUpdate();
                ByteArrayOutputStream str = new ByteArrayOutputStream();
                ObjectOutputStream data = new ObjectOutputStream(str);

                for (ItemStack item : main.listDailyItems.keySet()) {

                    String insertItem = "INSERT INTO " + "current_items (material) VALUES (?)";
                    statement = db.con.prepareStatement(insertItem);
                    NBTCompound itemData = NBTItem.convertItemtoNBT(item);

                    String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                    statement.setString(1, base64);
                    statement.executeUpdate();
                }

            } catch (SQLException | IOException e) {
                main.getLogger().warning("Couldn't update current items on database");
            }
        });

    }

    public ArrayList<ItemStack> getCurrentItems() {
        ArrayList<ItemStack> items = new ArrayList<>();

        try {
            db.connect();
            String SQL_Create = "SELECT * FROM current_items";
            PreparedStatement statement = db.con.prepareStatement(SQL_Create);
            ResultSet result = statement.executeQuery();

            String string;
            NBTCompound itemData;
            ItemStack item;
            byte[] itemserial;
            while (result.next()) {
                itemserial = Base64.getDecoder().decode(result.getString("material"));
                try {
                    string = new String(itemserial);
                    itemData = new NBTContainer(string);
                    item = NBTItem.convertNBTtoItem(itemData);
                    if (item == null || !main.listDailyItems.containsKey(item)) continue;

                } catch (Exception e) {
                    main.getLogger().warning("A previous current item registered on the db is now unsupported, skipping...");
                    continue;
                }
                items.add(item);
            }

        } catch (SQLException e) {
            main.getLogger().warning("Couldn't update current items on database");
        }

        return items;
    }

    public void updateSellItems() {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();

                //Quitamos los elementos previos
                Statement statementDel = db.con.createStatement();
                statementDel.execute("DELETE FROM " + "sell_items;");

                ByteArrayOutputStream str = new ByteArrayOutputStream();
                ObjectOutputStream data = new ObjectOutputStream(str);
                PreparedStatement statement;

                for (Map.Entry<ItemStack, Double> item : main.listSellItems.entrySet()) {

                    String insertItem = "INSERT INTO " + "sell_items (material, price) VALUES (?, ?)";
                    statement = db.con.prepareStatement(insertItem);
                    NBTCompound itemData = NBTItem.convertItemtoNBT(item.getKey());

                    String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                    statement.setString(1, base64);
                    statement.setDouble(2, item.getValue());
                    statement.executeUpdate();
                }

            } catch (SQLException | IOException e) {
                main.getLogger().warning("Couldn't update sell items on database");
            }
        });

    }

    public LinkedHashMap<ItemStack, Double> getSellItems() {
        LinkedHashMap<ItemStack, Double> items = new LinkedHashMap<>();

        try {
            String SQL_Create = "SELECT * FROM sell_items";
            PreparedStatement statement = db.con.prepareStatement(SQL_Create);
            ResultSet result = statement.executeQuery();

            String string;
            NBTCompound itemData;
            ItemStack item;
            byte[] itemserial;
            while (result.next()) {
                itemserial = Base64.getDecoder().decode(result.getString("material"));
                try {
                    string = new String(itemserial);
                    itemData = new NBTContainer(string);
                    item = NBTItem.convertNBTtoItem(itemData);
                    if (item == null) continue;
                    try {
                        Material.valueOf(item.getType().toString());
                    } catch (Exception e) {
                        continue;
                    }

                } catch (Exception e) {
                    main.getLogger().warning("A previous sell item registered on the db is now unsupported, skipping...");
                    continue;
                }
                items.put(item, result.getDouble(2));
            }

        } catch (SQLException e) {
            main.getLogger().warning("Couldn't get sell items on database");
        }


        return items;
    }

    public void updateDailyItems() {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();

                //Quitamos los elementos previos
                Statement statementDel = db.con.createStatement();
                statementDel.execute("DELETE FROM " + "daily_items;");

                ByteArrayOutputStream str = new ByteArrayOutputStream();
                ObjectOutputStream data = new ObjectOutputStream(str);
                PreparedStatement statement;

                for (Map.Entry<ItemStack, Double> item : main.listSellItems.entrySet()) {

                    String insertItem = "INSERT INTO " + "sell_items (material, price) VALUES (?, ?)";
                    statement = db.con.prepareStatement(insertItem);
                    NBTCompound itemData = NBTItem.convertItemtoNBT(item.getKey());

                    String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                    statement.setString(1, base64);
                    statement.setDouble(2, item.getValue());
                    statement.executeUpdate();
                }

            } catch (SQLException | IOException e) {
                main.getLogger().warning("Couldn't update daily items on database");
            }
        });

    }

    public HashMap<ItemStack, Double> getDailyItems() {
        HashMap<ItemStack, Double> items = new HashMap<>();


        try {
            String SQL_Create = "SELECT * FROM daily_items";
            PreparedStatement statement = db.con.prepareStatement(SQL_Create);
            ResultSet result = statement.executeQuery();

            String string;
            NBTCompound itemData;
            ItemStack item;
            byte[] itemserial;
            while (result.next()) {
                itemserial = Base64.getDecoder().decode(result.getString("material"));
                try {
                    string = new String(itemserial);
                    itemData = new NBTContainer(string);
                    item = NBTItem.convertNBTtoItem(itemData);
                    if (item == null) continue;
                    try {
                        Material.valueOf(item.getType().toString());
                    } catch (Exception e) {
                        continue;
                    }

                } catch (Exception e) {
                    main.getLogger().warning("A previous sell item registered on the db is now unsupported, skipping...");
                    continue;
                }
                items.put(item, result.getDouble(2));
            }

        } catch (SQLException e) {
            main.getLogger().warning("Couldn't get daily items on database");
        }


        return items;
    }

}

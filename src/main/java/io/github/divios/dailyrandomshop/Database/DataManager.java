package io.github.divios.dailyrandomshop.Database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public void updateCurrentItems(ArrayList<ItemStack> currentItems) {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();
                PreparedStatement statement;

                //Quitamos los elementos previos
                String deleteTable = "DELETE FROM " + "current_items;";
                statement = db.con.prepareStatement(deleteTable);
                statement.executeUpdate();

                for (ItemStack item : currentItems) {

                    String insertItem = "INSERT INTO " + "current_items (material) VALUES (?)";
                    statement = db.con.prepareStatement(insertItem);
                    NBTCompound itemData = NBTItem.convertItemtoNBT(main.utils.removeItemAsDaily(item));

                    String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                    statement.setString(1, base64);
                    statement.executeUpdate();
                }

            } catch (SQLException e) {
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
                    if (item == null || !main.utils.listContaisItem(main.listDailyItems, item) ||
                            item.getType() == Material.AIR) continue;

                } catch (Exception e) {
                    main.getLogger().warning("A previous current item registered on the db is now unsupported, skipping...");
                    continue;
                }
                items.add(item);
            }

        } catch (SQLException e) {
            main.getLogger().warning("Couldn't get current items from database");
        }

        return items;
    }


    public void updateSellItemPrice(ItemStack item, Double price) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();
                String updateItem = "UPDATE sell_items SET price = ? WHERE material = ?";
                PreparedStatement statement = db.con.prepareStatement(updateItem);

                NBTCompound itemData = NBTItem.convertItemtoNBT(item);
                String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                statement.setDouble(1, price);
                statement.setString(2, base64);

                statement.executeUpdate();

            } catch (SQLException err) {
                main.getLogger().warning("There was an error trying to save the item");
            }
        });
    }

    public void deleteSellItem(ItemStack item) {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();
                String updateItem = "DELETE FROM sell_items WHERE material = ?";
                PreparedStatement statement = db.con.prepareStatement(updateItem);

                NBTCompound itemData = NBTItem.convertItemtoNBT(item);
                String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                statement.setString(1, base64);
                statement.executeUpdate();

            } catch (SQLException err) {
                main.getLogger().warning("There was an error trying to delete the item");
            }
        });
    }

    public void addSellItem(ItemStack item, Double price) {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();
                String updateItem = "INSERT INTO sell_items (material, price) VALUES (?, ?)";
                PreparedStatement statement = db.con.prepareStatement(updateItem);

                NBTCompound itemData = NBTItem.convertItemtoNBT(item);
                String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                statement.setString(1, base64);
                statement.setDouble(2, price);

                statement.executeUpdate();

            } catch (SQLException err) {
                main.getLogger().warning("There was an error trying to delete the item");
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
                    if (item == null || item.getType() == Material.AIR) continue;
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

    public void updateAllSellItems() {
        db.connect();
        PreparedStatement statement;
        try {
            //Quitamos los elementos previos
            String deleteTable = "DELETE FROM " + "sell_items;";
            statement = db.con.prepareStatement(deleteTable);
            statement.executeUpdate();

            for (Map.Entry<ItemStack, Double> entry: main.listSellItems.entrySet()) {

                String updateItem = "INSERT INTO sell_items (material, price) VALUES (?, ?)";
                statement = db.con.prepareStatement(updateItem);

                NBTCompound itemData = NBTItem.convertItemtoNBT(entry.getKey());
                String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                statement.setString(1, base64);
                statement.setDouble(2, entry.getValue());

                statement.executeUpdate();
            }

        } catch (SQLException Ignored) {}
    }

    public void updateAllSellItemsJson(String json) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            db.connect();
            PreparedStatement statement;
            try {
                //Quitamos los elementos previos
                String deleteTable = "DELETE FROM " + "sell_items;";
                statement = db.con.prepareStatement(deleteTable);
                statement.executeUpdate();
                LinkedHashMap<ItemStack, Double> list = new Gson().fromJson(
                        json, new TypeToken<LinkedHashMap<ItemStack, Double>>() {}.getType());

                for (Map.Entry<ItemStack, Double> entry : list.entrySet()) {

                    String updateItem = "INSERT INTO sell_items (material, price) VALUES (?, ?)";
                    statement = db.con.prepareStatement(updateItem);

                    NBTCompound itemData = NBTItem.convertItemtoNBT(entry.getKey());
                    String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                    statement.setString(1, base64);
                    statement.setDouble(2, entry.getValue());

                    statement.executeUpdate();
                }

            } catch (SQLException Ignored) {
            }
        });
    }


    public void updateDailyItemPrice(ItemStack item, Double price) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();
                String updateItem = "UPDATE daily_items SET price = ? WHERE material = ?";
                PreparedStatement statement = db.con.prepareStatement(updateItem);

                NBTCompound itemData = NBTItem.convertItemtoNBT(item);
                String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                statement.setDouble(1, price);
                statement.setString(2, base64);

                statement.executeUpdate();

            } catch (SQLException err) {
                main.getLogger().warning("There was an error trying to save the item");
            }
        });
    }

    public void deleteDailyItem(ItemStack item) {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();
                String updateItem = "DELETE FROM daily_items WHERE material = ?";
                PreparedStatement statement = db.con.prepareStatement(updateItem);

                NBTCompound itemData = NBTItem.convertItemtoNBT(item);
                String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                statement.setString(1, base64);
                statement.executeUpdate();

            } catch (SQLException err) {
                main.getLogger().warning("There was an error trying to delete the item");
            }
        });
    }

    public void addDailyItem(ItemStack item, Double price) {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try {
                db.connect();
                String updateItem = "INSERT INTO daily_items (material, price) VALUES (?, ?)";
                PreparedStatement statement = db.con.prepareStatement(updateItem);

                NBTCompound itemData = NBTItem.convertItemtoNBT(item);
                String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                statement.setString(1, base64);
                statement.setDouble(2, price);

                statement.executeUpdate();

            } catch (SQLException err) {
                main.getLogger().warning("There was an error trying to delete the item");
            }
        });
    }

    public LinkedHashMap<ItemStack, Double> getDailyItems() {
        LinkedHashMap<ItemStack, Double> items = new LinkedHashMap<>();


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
                    if (item == null || item.getType() == Material.AIR) continue;
                    try {
                        Material.valueOf(item.getType().toString());
                    } catch (Exception e) {
                        continue;
                    }

                } catch (Exception e) {
                    main.getLogger().warning("A previous sell item registered on the db is now unsupported, skipping...");
                    continue;
                }
                items.put(main.utils.removeItemAsDaily(item), result.getDouble(2));
            }

        } catch (SQLException e) {
            main.getLogger().warning("Couldn't get daily items on database");
        }


        return items;
    }

    public void updateAllDailyItems() {
            db.connect();
            PreparedStatement statement;
            try {
                //Quitamos los elementos previos
                String deleteTable = "DELETE FROM " + "daily_items;";
                statement = db.con.prepareStatement(deleteTable);
                statement.executeUpdate();

                for (Map.Entry<ItemStack, Double> entry: main.listDailyItems.entrySet()) {

                    String updateItem = "INSERT INTO daily_items (material, price) VALUES (?, ?)";
                    statement = db.con.prepareStatement(updateItem);

                    NBTCompound itemData = NBTItem.convertItemtoNBT(entry.getKey());
                    String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                    statement.setString(1, base64);
                    statement.setDouble(2, entry.getValue());

                    statement.executeUpdate();
                }

            } catch (SQLException Ignored) {}
    }

    public void updateAllDailyItemsJson(String json) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            db.connect();
            PreparedStatement statement;
            try {
                //Quitamos los elementos previos
                String deleteTable = "DELETE FROM " + "daily_items;";
                statement = db.con.prepareStatement(deleteTable);
                statement.executeUpdate();
                LinkedHashMap<ItemStack, Double> list = new Gson().fromJson(
                        json, new TypeToken<LinkedHashMap<ItemStack, Double>>() {}.getType());

                for (Map.Entry<ItemStack, Double> entry : list.entrySet()) {

                    String updateItem = "INSERT INTO daily_items (material, price) VALUES (?, ?)";
                    statement = db.con.prepareStatement(updateItem);

                    NBTCompound itemData = NBTItem.convertItemtoNBT(entry.getKey());
                    String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

                    statement.setString(1, base64);
                    statement.setDouble(2, entry.getValue());

                    statement.executeUpdate();
                }

                } catch (SQLException Ignored) {
            }
        });
    }

}

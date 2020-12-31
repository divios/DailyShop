package io.github.divios.dailyrandomshop.Database;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DataManager {

    private final sqlite db;
    private final DailyRandomShop main;

    public DataManager(sqlite db, DailyRandomShop main) {
        this.db = db;
        this.main = main;
    }

    public void createTables() throws SQLException {
        db.connect();
        String SQL_Create = "";

        PreparedStatement statement;

        SQL_Create = "CREATE TABLE current_items"
                + "(material varchar [255], price double);";
        statement = db.con.prepareStatement(SQL_Create);
        statement.addBatch();

    }

    public int getTimer() throws SQLException {

        db.connect();
        String selectTimer = "SELECT time FROM " + "timer" + " WHERE id = 1";
        PreparedStatement statement = db.con.prepareStatement(selectTimer);
        ResultSet result = statement.executeQuery();

        result.next();
        return result.getInt("time");
    }

    public boolean isPoblated() {
        db.connect();
        try {
            String selectTimer = "SELECT * FROM timer";
            PreparedStatement statement = db.con.prepareStatement(selectTimer);
            ResultSet result = statement.executeQuery();

            return result.next();
        } catch (Exception e) {
            return false;
        }
    }

    public void updateTimer(int time) throws SQLException {
        db.connect();
        String SQL_Create = "";

        PreparedStatement statement;
        SQL_Create = "CREATE TABLE IF NOT EXISTS timer"
                + "(time int, id int);";
        statement = db.con.prepareStatement(SQL_Create);
        statement.executeUpdate();

        if(!isPoblated()) {
            SQL_Create = "INSERT INTO " + "timer (time, id) VALUES (?, ?)";
            statement = db.con.prepareStatement(SQL_Create);
            statement.setInt(1, main.getConfig().getInt("timer-duration"));
            statement.setInt(2, 1);
            statement.executeUpdate();
        }

        String updateTimer = "UPDATE " + "timer SET time = ? WHERE id = ?";
        statement = db.con.prepareStatement(updateTimer);
        statement.setInt(1, time);
        statement.setInt(2, 1);
        statement.addBatch();
        statement.executeUpdate();
    }

    public void updateCurrentItems() throws SQLException, IOException {
        db.connect();
        PreparedStatement statement;

        //Por si acaso creamos tabla en caso de que no exista
        String SQL_Create = "CREATE TABLE IF NOT EXISTS current_items"
                + "(material varchar [255], price int);";
        statement = db.con.prepareStatement(SQL_Create);
        statement.executeUpdate();

        //Quitamos los elementos previos
        String deleteTable = "DELETE FROM " + "current_items;";
        statement = db.con.prepareStatement(deleteTable);
        statement.executeUpdate();
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        ObjectOutputStream data = new ObjectOutputStream(str);

        for (ItemStack item: main.listDailyItems.keySet()) {

            String insertItem = "INSERT INTO " + "current_items (material) VALUES (?)";
            statement = db.con.prepareStatement(insertItem);
            NBTCompound itemData = NBTItem.convertItemtoNBT(item);

            String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

            statement.setString(1, base64);
            statement.executeUpdate();
        }

    }

    public ArrayList<ItemStack> getCurrentItems() throws SQLException, IOException, ClassNotFoundException {
        ArrayList<ItemStack> items = new ArrayList<>();

        db.connect();
        String SQL_Create = "SELECT * FROM current_items";
        PreparedStatement statement = db.con.prepareStatement(SQL_Create);
        ResultSet result = statement.executeQuery();

        ByteArrayInputStream str;
        ObjectInputStream data;
        String string;
        NBTCompound itemData;
        ItemStack item;
        byte[] itemserial;
        while(result.next()) {
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
        return items;
    }

    public void updateSellItems() throws SQLException, IOException {
        db.connect();
        PreparedStatement statement;

        //Por si acaso creamos tabla en caso de que no exista
        String SQL_Create = "CREATE TABLE IF NOT EXISTS sell_items"
                + "(material varchar [255], price int);";
        statement = db.con.prepareStatement(SQL_Create);
        statement.executeUpdate();

        //Quitamos los elementos previos
        String deleteTable = "DELETE FROM " + "sell_items;";
        statement = db.con.prepareStatement(deleteTable);
        statement.executeUpdate();
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        ObjectOutputStream data = new ObjectOutputStream(str);

        for (Map.Entry<ItemStack, Double> item: main.listSellItems.entrySet()) {

            String insertItem = "INSERT INTO " + "sell_items (material, price) VALUES (?, ?)";
            statement = db.con.prepareStatement(insertItem);
            NBTCompound itemData = NBTItem.convertItemtoNBT(item.getKey());

            String base64 = Base64.getEncoder().encodeToString(itemData.toString().getBytes());

            statement.setString(1, base64);
            statement.setDouble(2, item.getValue());
            statement.executeUpdate();
        }

    }

    public HashMap<ItemStack, Double> getSellItems() throws SQLException, IOException, ClassNotFoundException {
        HashMap<ItemStack, Double> items = new HashMap<>();

        db.connect();
        String SQL_Create = "SELECT * FROM sell_items";
        PreparedStatement statement = db.con.prepareStatement(SQL_Create);
        ResultSet result = statement.executeQuery();

        ByteArrayInputStream str;
        ObjectInputStream data;
        String string;
        NBTCompound itemData;
        ItemStack item;
        byte[] itemserial;
        while(result.next()) {
            itemserial = Base64.getDecoder().decode(result.getString("material"));
            try {
                string = new String(itemserial);
                itemData = new NBTContainer(string);
                item = NBTItem.convertNBTtoItem(itemData);
                if (item == null ) continue;
                try{
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
        return items;
    }


}

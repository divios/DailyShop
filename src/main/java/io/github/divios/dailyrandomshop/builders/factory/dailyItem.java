package io.github.divios.dailyrandomshop.builders.factory;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.builders.factory.blocks.*;
import io.github.divios.dailyrandomshop.builders.lorestategy.loreStrategy;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class dailyItem {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();
    private final ItemStack item;
    private final List<runnableBlocks> RunnableBlocks = new ArrayList<>();

    public dailyItem(ItemStack item) { this.item = item;}

    public dailyItem(Material m) { this.item = new ItemStack(m); }

    public dailyItem(String uuid) { this.item = getRawItem(uuid); }

    public dailyItem(String uuid, boolean clone) {
        if(clone)
            this.item = getRawItem(uuid).clone();
        else
            this.item = getRawItem(uuid);
    }

    public dailyItem(ItemStack item, boolean clone) {
        if (clone)
           this.item = item.clone();
        else
            this.item = item;
    }

    private void constructItem() {
        RunnableBlocks.forEach(r -> r.run(item));
    }

    public ItemStack getItem() {
        constructItem();
        return item;
    }

    public ItemStack craft() {
        constructItem();
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(dailyMetadataType.rds_UUID.name(), UUID.randomUUID().toString());
        return nbtItem.getItem();
    }

    public dailyItem addNbt(dailyMetadataType key, String value) {
        RunnableBlocks.add(new addMetadata(key, value));
        return this;
    }

    public dailyItem removeNbt(dailyMetadataType key) {
        RunnableBlocks.add(new removeMetadata(key));
        return this;
    }

    public boolean hasMetadata(dailyMetadataType key) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey(key.name());
    }

    public Object getMetadata(dailyItem.dailyMetadataType key) {
        NBTItem nbtItem = new NBTItem(item);
        Object obj;
        switch (key) {
            case rds_amount:
                obj = nbtItem.getInteger(key.name()); break;
            case rds_commands:
                obj = nbtItem.getObject(key.name(), List.class);
                if(obj == null) obj = new ArrayList<>();
                break;
            case rds_rarity:
                return nbtItem.getInteger(key.name());
            default: obj = nbtItem.getString(key.name()); break;
        }
        return obj;
    }

    public dailyItem removeAllMetadata() {
        for(dailyMetadataType s: dailyMetadataType.values()) {
            RunnableBlocks.add(new removeMetadata(s));
        }
        return this;
    }

    public dailyItem addLoreStrategy(loreStrategy s) {
        RunnableBlocks.add(new addLoreStrategy(s));
        return this;
    }

    @Deprecated
    public dailyItem removeLoreStrategy(loreStrategy s) {
        RunnableBlocks.add(new removeLoreStrategy(s));
        return this;
    }

    public static Double getPrice(ItemStack item) {
        return getPrice(getUuid(item));
    }

    public static Double getPrice(String uuid) {
        for(Map.Entry<ItemStack, Double> entry: dbManager.listDailyItems.entrySet()) {
            if(getUuid(entry.getKey()).equals(uuid)) return entry.getValue();
        }
        return -1D;
    }

    /**
     *
     * @param item
     * @return returns the uuid of the item
     */
    public static String getUuid(ItemStack item) {
        return new NBTItem(item).getString(dailyMetadataType.rds_UUID.name());
    }

    /**
     *
     * @param itemToSearch item uuid to search
     * @return gets the item store on db with itemToSearch item uuid
     */

    public static ItemStack getRawItem(ItemStack itemToSearch) {
        String uuid = getUuid(itemToSearch);
        if(utils.isEmpty(uuid)) return null;

        return getRawItem(uuid);
    }

    /**
     *
     * @param uuid
     * @return gets the item store on db with that uuid
     */

    public static ItemStack getRawItem(String uuid) {
        for (Map.Entry<ItemStack, Double> entry : dbManager.listDailyItems.entrySet()) {
            if (getUuid(entry.getKey()).equals(uuid)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void removeItemByUuid(ItemStack item) {
        String uuid = getUuid(item);
        dbManager.listDailyItems.entrySet().removeIf(e ->
                getUuid(e.getKey()).equals(uuid));
    }

    public static void removeItemByUuid(String uuid) {
        dbManager.listDailyItems.entrySet().removeIf(e ->
                getUuid(e.getKey()).equals(uuid));
    }

    public static void changePriceByUuid(ItemStack item, Double price) {
        String uuid = getUuid(item);
        for (Map.Entry<ItemStack, Double> e : dbManager.listDailyItems.entrySet()) {
            if (getUuid(e.getKey()).equals(uuid)) e.setValue(price);
        }
    }

    public static void changePriceByUuid(String uuid, Double price) {
        for (Map.Entry<ItemStack, Double> e : dbManager.listDailyItems.entrySet()) {
            if (getUuid(e.getKey()).equals(uuid)) e.setValue(price);
        }
    }

    public enum dailyMetadataType {
        rds_UUID,
        rds_amount,
        rds_rarity,
        rds_econ,
        rds_mmoitem,
        rds_commands
    }
}

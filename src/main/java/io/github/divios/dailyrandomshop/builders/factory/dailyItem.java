package io.github.divios.dailyrandomshop.builders.factory;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.builders.factory.blocks.*;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.lorestategy.loreStrategy;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
        new addMetadata(dailyMetadataType.rds_UUID,
                UUID.randomUUID().toString()).run(item);
        return item;
    }

    public dailyItem addNbt(dailyMetadataType key, Object value) {
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
            case rds_bundle:
            case rds_permissions:
            case rds_commands:
                obj = nbtItem.getObject(key.name(), List.class);
                if(obj == null) obj = new ArrayList<>();
                break;
            case rds_econ:
                obj = nbtItem.getObject(key.name(), AbstractMap.SimpleEntry.class);
                break;
            case rds_amount:
            case rds_rarity:
            case rds_setItems:
                return nbtItem.getInteger(key.name());
            case rds_confirm_gui:
                return nbtItem.getBoolean(key.name());
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

    public static void transferDailyMetadata(ItemStack recipient, ItemStack receiver) {
        dailyItem newItem = new dailyItem(receiver);

        if (new dailyItem(recipient).hasMetadata(dailyMetadataType.rds_econ)) {
            newItem = newItem.addNbt(dailyMetadataType.rds_econ,
                    new dailyItem(recipient).getMetadata(dailyMetadataType.rds_econ));
        }

        if (new dailyItem(recipient).hasMetadata(dailyMetadataType.rds_rarity)) {
            newItem = newItem.addNbt(dailyMetadataType.rds_rarity,
                    new dailyItem(recipient).getMetadata(dailyMetadataType.rds_rarity));
        }

        if (new dailyItem(recipient).hasMetadata(dailyMetadataType.rds_amount)) {
            newItem = newItem.addNbt(dailyMetadataType.rds_amount,
                    new dailyItem(recipient).getMetadata(dailyMetadataType.rds_amount));
        }

        if (new dailyItem(recipient).hasMetadata(dailyMetadataType.rds_commands)) {
            newItem = newItem.addNbt(dailyMetadataType.rds_commands,
                    new dailyItem(recipient).getMetadata(dailyMetadataType.rds_commands));
        }

        newItem = newItem.addNbt(dailyMetadataType.rds_UUID,
                new dailyItem(recipient).getMetadata(dailyMetadataType.rds_UUID));

        newItem.getItem();
    }

    //common (100), uncommon (80), rare (60), epic (40), ancient (20), legendary (10), mythic (5)

    /**
     *
     * @param s
     * @return returns the item that represents that rarity
     */

    public static ItemStack getItemRarity(int s) {
        ItemStack changeRarity = null;
        switch (s) {
            case 0:
                changeRarity = XMaterial.GRAY_DYE.parseItem();
                try {
                    utils.setDisplayName(changeRarity, conf_msg.RARITY_NAMES.get(0));
                } catch (Exception Ignored) { utils.setDisplayName(changeRarity, "&7Common"); }
                break;
            case 80:
                changeRarity = XMaterial.PINK_DYE.parseItem();
                try {
                    utils.setDisplayName(changeRarity, conf_msg.RARITY_NAMES.get(1));
                } catch (Exception Ignored) { utils.setDisplayName(changeRarity, "&dUnCommon"); }
                break;
            case 60:
                changeRarity = XMaterial.MAGENTA_DYE.parseItem();
                try {
                    utils.setDisplayName(changeRarity, conf_msg.RARITY_NAMES.get(2));
                } catch (Exception Ignored) { utils.setDisplayName(changeRarity, "&5Rare"); }
                break;
            case 40:
                changeRarity = XMaterial.PURPLE_DYE.parseItem();
                try {
                    utils.setDisplayName(changeRarity, conf_msg.RARITY_NAMES.get(3));
                } catch (Exception Ignored) { utils.setDisplayName(changeRarity, "&5Epic"); }
                break;
            case 20:
                changeRarity = XMaterial.CYAN_DYE.parseItem();
                try {
                    utils.setDisplayName(changeRarity, conf_msg.RARITY_NAMES.get(4));
                } catch (Exception Ignored) { utils.setDisplayName(changeRarity, "&9Ancient"); }
                break;
            case 10:
                changeRarity = XMaterial.ORANGE_DYE.parseItem();
                try {
                    utils.setDisplayName(changeRarity, conf_msg.RARITY_NAMES.get(5));
                } catch (Exception Ignored) { utils.setDisplayName(changeRarity, "&6Legendary"); }
                break;
            case 5:
                changeRarity = XMaterial.YELLOW_DYE.parseItem();
                try {
                    utils.setDisplayName(changeRarity, conf_msg.RARITY_NAMES.get(6));
                } catch (Exception Ignored) { utils.setDisplayName(changeRarity, "&eMythic"); }
                break;
            default:
                changeRarity = XMaterial.BARRIER.parseItem();
                try {
                    utils.setDisplayName(changeRarity, conf_msg.RARITY_NAMES.get(7));
                } catch (Exception Ignored) { utils.setDisplayName(changeRarity, "&cUnavailable"); }
                break;
        }
        return changeRarity;
    }

    public static ItemStack getItemRarity(ItemStack item) {
        int s = (Integer) new dailyItem(item).getMetadata(dailyMetadataType.rds_rarity);
        return getItemRarity(s);
    }

    public static String getRarityLore(int rarity) {

        switch (rarity) {
            case 0:
                return "Common";

            case 80:
                return "UnCommon";

            case 60:
                return "Rare";

            case 40:
                return "Epic";

            case 20:
                return "Ancient";

            case 10:
                return "Legendary";
            case 5:
                return "Mythic";

            default:
                return "Unavailable";

        }
    }

    public static String getRarityLore(ItemStack item) {
        int rarity = (Integer) new dailyItem(item).getMetadata(dailyMetadataType.rds_rarity);
        return getRarityLore(rarity);
    }

    public enum dailyMetadataType {
        rds_UUID,
        rds_amount,
        rds_rarity,
        rds_econ,
        rds_commands,
        rds_permissions,
        rds_confirm_gui,
        rds_setItems,
        rds_bundle
    }
}

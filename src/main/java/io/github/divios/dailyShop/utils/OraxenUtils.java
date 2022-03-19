package io.github.divios.dailyShop.utils;

import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class OraxenUtils {

    public static boolean isOraxenOn() {
        return Utils.isOperative("Oraxen");
    }

    public static boolean isOraxenItem(ItemStack item) {
        if (!isOraxenOn()) return false;

        String id = OraxenItems.getIdByItem(item);
        return id != null && OraxenItems.exists(id);
    }

    public static boolean isValidId(String id) {
        if (!isOraxenOn()) return false;
        return OraxenItems.exists(id);
    }

    public static String getId(ItemStack item) {
        if (!isOraxenOn()) return null;

        if (!isOraxenItem(item)) return null;
        return OraxenItems.getIdByItem(item);
    }

    public static ItemStack createItemByID(String id) {
        if (!isOraxenOn()) return null;
        if (!OraxenItems.exists(id)) return null;

        return OraxenItems.getItemById(id).build();
    }

    public static boolean compareItems(ItemStack item1, ItemStack item2) {
        String id1 = getId(item1);
        String id2 = getId(item2);

        return Objects.equals(id1, id2);
    }


}

package io.github.divios.dailyShop.utils;

import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class OraxenUtils {

    public static boolean isOraxenOn() {
        return Utils.isOperative("Oraxen");
    }

    public static boolean isOraxenItem(ItemStack item) {
        return getId(item) != null;
    }

    public static boolean isValidId(String id) {
        if (!isOraxenOn() || id == null) return false;

        return OraxenItems.exists(id);
    }

    public static String getId(ItemStack item) {
        if (!isOraxenOn() || item == null) return null;

        return OraxenItems.getIdByItem(item);
    }

    public static ItemStack createItemByID(String id) {
        if (!isOraxenOn() || id == null) return null;
        ItemBuilder builder;

        return (builder = OraxenItems.getItemById(id)) == null
                ? null
                : builder.build();
    }

    public static ItemStack createItemFromItem(ItemStack item) {
        return createItemByID(getId(item));
    }

    public static boolean compareItems(ItemStack item1, ItemStack item2) {
        String id1 = getId(item1);
        String id2 = getId(item2);

        return Objects.equals(id1, id2);
    }


}

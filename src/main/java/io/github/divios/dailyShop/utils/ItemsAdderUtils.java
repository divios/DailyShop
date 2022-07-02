package io.github.divios.dailyShop.utils;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderUtils {

    public static boolean isItemsAdder(ItemStack item) {
        return getAsCustomStack(item) != null;
    }

    public static CustomStack getAsCustomStack(ItemStack item) {
        return isOn() ? CustomStack.byItemStack(item) : null;
    }

    public static CustomStack getFromNameSpace(String nameSpace) {
        return CustomStack.getInstance(nameSpace);
    }

    public static String getNameSpaceId(ItemStack item) {
        CustomStack stackItem;
        return (stackItem = getAsCustomStack(item)) == null
                ? null
                : stackItem.getNamespacedID();
    }

    public static String getId(ItemStack item) {
        CustomStack stackItem;
        return (stackItem = getAsCustomStack(item)) == null
                ? null
                : stackItem.getId();
    }

    public static String getNameSpace(ItemStack item) {
        CustomStack stackItem;
        return (stackItem = getAsCustomStack(item)) == null
                ? null
                : stackItem.getNamespace();
    }

    public static boolean isOn() {
        return Utils.isOperative("ItemsAdder");
    }

}

package io.github.divios.dailyrandomshop.builders.itemBuildersHooks;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

public class itemsBuilderManager {

    private static final MMOItems mmoitems = new MMOItems();
    private static final oraxenItems oraxenItems = new oraxenItems();

    private itemsBuilderManager() {}

    public static boolean updateItem(String uuid) {
        if (utils.isEmpty(uuid)) return false;
        buyGui.getInstance().updateItem(uuid,
                buyGui.updateAction.update);

        return updateItem(dailyItem.getRawItem(uuid));
    }

    public static boolean updateItem(ItemStack item) {
        itemsBuilder builder;

        if(mmoitems.isItem(item)) builder = mmoitems;
        else if(oraxenItems.isItem(item)) builder = oraxenItems;
        else return false;

        return builder.updateItem(item);
    }

    public static boolean isUpdateItem(ItemStack item) {
        return mmoitems.isItem(item) || oraxenItems.isItem(item);
    }

    public static ItemStack getItem(ItemStack item) {
        itemsBuilder builder;

        if(mmoitems.isItem(item)) builder = mmoitems;
        else if (oraxenItems.isItem(item)) builder = oraxenItems;
        else return null;

        return builder.getItem(item);
    }

}

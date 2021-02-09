package io.github.divios.dailyrandomshop.builders.itemBuildersHooks;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.utils.utils;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class oraxenItems implements itemsBuilder{

    @Override
    public boolean isItem(ItemStack item) {
        try {
            return !utils.isEmpty(getUuid(item));
        } catch (NoClassDefFoundError | NoSuchMethodError
                | ExceptionInInitializerError e) {
            return false;
        }
    }

    @Override
    public ItemStack getItem(ItemStack item) {
        try {
        String s = OraxenItems.getIdByItem(item);
        return OraxenItems.getItemById(s).getReferenceClone();
        } catch (NoClassDefFoundError | NoSuchMethodError
                | ExceptionInInitializerError e) {
            return null;
        }
    }

    @Override
    public String getUuid(ItemStack item) {
        try {
            return OraxenItems.getIdByItem(item);
        } catch (NoClassDefFoundError | NoSuchMethodError
                | ExceptionInInitializerError e) {
            return null;
        }
    }

    @Override
    public boolean updateItem(ItemStack toUpdate) {
        if (!isItem(toUpdate)) return false;
        ItemStack auxitem = getItem(toUpdate);
        dailyItem.transferDailyMetadata(toUpdate, auxitem);
        utils.translateAllItemData(auxitem, toUpdate);
        return true;
    }
}

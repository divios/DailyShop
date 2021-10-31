package io.github.divios.dailyShop.utils;

import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class CompareItemUtils {

    public  static boolean compareItems(ItemStack itemCompared, ItemStack itemToCompare) {
        if (ItemUtils.isEmpty(itemCompared) || ItemUtils.isEmpty(itemToCompare)) return false;

        return itemCompared.isSimilar(itemToCompare);
    }
    
}

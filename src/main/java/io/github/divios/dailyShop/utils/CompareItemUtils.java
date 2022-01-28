package io.github.divios.dailyShop.utils;

import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class CompareItemUtils {

    public static boolean compareItems(ItemStack itemCompared, ItemStack itemToCompare) {
        if (ItemUtils.isEmpty(itemCompared) || ItemUtils.isEmpty(itemToCompare)) return false;

        if (MMOUtils.isMMOItemsOn() && MMOUtils.isMMOItem(itemCompared)) {
            return MMOUtils.compareMMOItems(itemCompared, itemToCompare);
        }

        if (OraxenUtils.isOraxenOn() && OraxenUtils.isOraxenItem(itemCompared)) {
            return OraxenUtils.compareItems(itemCompared, itemToCompare);
        }

        return itemCompared.isSimilar(itemToCompare);
    }

}

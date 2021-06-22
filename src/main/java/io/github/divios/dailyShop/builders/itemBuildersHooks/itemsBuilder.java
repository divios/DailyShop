package io.github.divios.dailyShop.builders.itemBuildersHooks;

import org.bukkit.inventory.ItemStack;

public interface itemsBuilder {

    boolean isItem(ItemStack item);
    ItemStack getItem(ItemStack item);
    String getUuid(ItemStack item);
    boolean updateItem(ItemStack toUpdate);

}

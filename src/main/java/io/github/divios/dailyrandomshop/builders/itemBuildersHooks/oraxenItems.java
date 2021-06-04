package io.github.divios.dailyrandomshop.builders.itemBuildersHooks;

import org.bukkit.inventory.ItemStack;

public class oraxenItems implements itemsBuilder{


    @Override
    public boolean isItem(ItemStack item) {
        return false;
    }

    @Override
    public ItemStack getItem(ItemStack item) {
        return null;
    }

    @Override
    public String getUuid(ItemStack item) {
        return null;
    }

    @Override
    public boolean updateItem(ItemStack toUpdate) {
        return false;
    }
}

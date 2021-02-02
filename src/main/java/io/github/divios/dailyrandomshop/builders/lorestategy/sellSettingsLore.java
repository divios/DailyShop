package io.github.divios.dailyrandomshop.builders.lorestategy;

import org.bukkit.inventory.ItemStack;

public class sellSettingsLore implements loreStrategy {

    @Override
    public void setLore(ItemStack item) {

    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {
        removeLore(item);
        setLore(item);
    }
}

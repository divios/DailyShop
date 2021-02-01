package io.github.divios.dailyrandomshop.builders.lorestategy;

import org.bukkit.inventory.ItemStack;

public interface loreStategy {
    void setLore(ItemStack item);
    void removeLore(ItemStack item);
}

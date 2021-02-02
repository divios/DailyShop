package io.github.divios.dailyrandomshop.builders.lorestategy;

import org.bukkit.inventory.ItemStack;

public interface loreStrategy {
    void setLore(ItemStack item);
    void removeLore(ItemStack item);
    void update(ItemStack item);
}

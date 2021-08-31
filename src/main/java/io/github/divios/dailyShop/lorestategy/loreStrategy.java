package io.github.divios.dailyShop.lorestategy;

import org.bukkit.inventory.ItemStack;

public interface loreStrategy {

    void setLore(ItemStack item);

    default ItemStack applyLore(ItemStack item) { return applyLore(item, null); }

    ItemStack applyLore(ItemStack item, Object... data);

    void removeLore(ItemStack item);

    void update(ItemStack item);

}
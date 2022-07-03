package io.github.divios.lib.dLib.confirmMenu.comparators;

import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemComparator {

    boolean compare(ItemStack a, ItemStack b);

}

package io.github.divios.lib.dLib.confirmMenu.comparators;

import org.bukkit.inventory.ItemStack;

public interface MatcherComparator {

    boolean matches(ItemStack item);
    ItemComparator getComparator();

}

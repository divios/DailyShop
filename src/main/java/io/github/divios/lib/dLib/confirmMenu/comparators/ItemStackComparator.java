package io.github.divios.lib.dLib.confirmMenu.comparators;

import org.bukkit.inventory.ItemStack;

public class ItemStackComparator implements MatcherComparator {

    @Override
    public boolean matches(ItemStack item) {
        return true;
    }

    @Override
    public ItemComparator getComparator() {
        return ItemStack::isSimilar;
    }
}

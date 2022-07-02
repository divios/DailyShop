package io.github.divios.lib.dLib.confirmMenu.comparators;

import io.github.divios.dailyShop.utils.OraxenUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class OraxenComparator implements MatcherComparator {

    @Override
    public boolean matches(ItemStack item) {
        return OraxenUtils.isOraxenItem(item);
    }

    @Override
    public ItemComparator getComparator() {
        return this::logic;
    }

    private boolean logic(ItemStack a, ItemStack b) {
        if (!OraxenUtils.isOraxenItem(b)) return false;

        return Objects.equals(OraxenUtils.getId(a), OraxenUtils.getId(b));
    }

}

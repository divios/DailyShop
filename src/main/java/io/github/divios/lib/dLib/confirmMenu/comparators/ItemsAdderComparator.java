package io.github.divios.lib.dLib.confirmMenu.comparators;

import io.github.divios.dailyShop.utils.ItemsAdderUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemsAdderComparator implements MatcherComparator {

    @Override
    public boolean matches(ItemStack item) {
        return ItemsAdderUtils.isItemsAdder(item);
    }

    @Override
    public ItemComparator getComparator() {
        return this::logic;
    }

    private boolean logic(ItemStack a, ItemStack b) {
        if (!ItemsAdderUtils.isItemsAdder(b)) return false;

        return Objects.equals(ItemsAdderUtils.getNameSpaceId(a), ItemsAdderUtils.getNameSpaceId(b));
    }

}

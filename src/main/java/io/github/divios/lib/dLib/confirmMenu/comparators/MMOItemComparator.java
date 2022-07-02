package io.github.divios.lib.dLib.confirmMenu.comparators;

import io.github.divios.dailyShop.utils.MMOUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class MMOItemComparator implements MatcherComparator {

    @Override
    public boolean matches(ItemStack item) {
        return MMOUtils.isMMOItem(item);
    }

    @Override
    public ItemComparator getComparator() {
        return this::logic;
    }

    private boolean logic(ItemStack a, ItemStack b) {
        if (!MMOUtils.isMMOItem(b)) return false;

        return Objects.equals(MMOUtils.getType(a), MMOUtils.getType(b))
                && Objects.equals(MMOUtils.getId(a), MMOUtils.getId(b));
    }

}

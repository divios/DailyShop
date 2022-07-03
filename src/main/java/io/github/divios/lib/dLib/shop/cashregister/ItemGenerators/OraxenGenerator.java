package io.github.divios.lib.dLib.shop.cashregister.ItemGenerators;

import io.github.divios.dailyShop.utils.OraxenUtils;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class OraxenGenerator implements ItemGenerator {

    @Override
    public boolean matches(ItemStack item) {
        return OraxenUtils.isOraxenItem(item);
    }

    @Override
    public Supplier<ItemStack> getGenerator(ItemStack item) {
        return () -> OraxenUtils.createItemByID(OraxenUtils.getId(item));
    }
}

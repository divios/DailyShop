package io.github.divios.lib.dLib.shop.cashregister.ItemGenerators;

import io.github.divios.dailyShop.utils.ItemsAdderUtils;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class ItemsAdderGenerator implements ItemGenerator {

    @Override
    public boolean matches(ItemStack item) {
        return ItemsAdderUtils.isItemsAdder(item);
    }

    @Override
    public Supplier<ItemStack> getGenerator(ItemStack item) {
        return () -> ItemsAdderUtils.getItemsAdderFromItem(item);
    }
}

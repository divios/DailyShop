package io.github.divios.lib.dLib.shop.cashregister.ItemGenerators;

import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class ItemStackGenerator implements ItemGenerator {

    @Override
    public boolean matches(ItemStack item) {
        return true;
    }

    @Override
    public Supplier<ItemStack> getGenerator(ItemStack item) {
        return item::clone;
    }
}

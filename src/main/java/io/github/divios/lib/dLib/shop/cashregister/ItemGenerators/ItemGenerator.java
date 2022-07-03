package io.github.divios.lib.dLib.shop.cashregister.ItemGenerators;

import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public interface ItemGenerator {

    boolean matches(ItemStack item);

    Supplier<ItemStack> getGenerator(ItemStack item);

}

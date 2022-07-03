package io.github.divios.lib.dLib.shop.cashregister.ItemGenerators;

import io.github.divios.dailyShop.utils.MMOUtils;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class MMOItemGenerator implements ItemGenerator {

    @Override
    public boolean matches(ItemStack item) {
        return MMOUtils.isMMOItem(item);
    }

    @Override
    public Supplier<ItemStack> getGenerator(ItemStack item) {
        return () -> MMOUtils.createMMOItemFromItem(item);
    }
}

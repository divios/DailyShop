package io.github.divios.dailyrandomshop.builders.factory.blocks;

import io.github.divios.dailyrandomshop.lorestategy.loreStrategy;
import org.bukkit.inventory.ItemStack;

public class removeLoreStrategy implements runnableBlocks{

    private final loreStrategy strategy;

    public removeLoreStrategy(loreStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void run(ItemStack item) {
        strategy.removeLore(item);
    }
}

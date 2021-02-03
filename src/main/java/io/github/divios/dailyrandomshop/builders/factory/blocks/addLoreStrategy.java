package io.github.divios.dailyrandomshop.builders.factory.blocks;

import io.github.divios.dailyrandomshop.lorestategy.loreStrategy;
import org.bukkit.inventory.ItemStack;

public class addLoreStrategy implements runnableBlocks{

    private final loreStrategy strategy;

    public addLoreStrategy(loreStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void run(ItemStack item) {
        strategy.setLore(item);
    }
}

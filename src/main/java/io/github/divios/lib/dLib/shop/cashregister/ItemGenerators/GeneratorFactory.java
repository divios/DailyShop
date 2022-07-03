package io.github.divios.lib.dLib.shop.cashregister.ItemGenerators;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GeneratorFactory {

    private static final List<ItemGenerator> generators;
    private static final ItemGenerator DEFAULT = new ItemStackGenerator();

    static {
        generators = new ArrayList<>();

        generators.add(new ItemsAdderGenerator());
        generators.add(new MMOItemGenerator());
        generators.add(new OraxenGenerator());
    }

    public static Supplier<ItemStack> getGenerator(ItemStack item) {
        return generators.stream()
                .filter(generator -> generator.matches(item))
                .findFirst()
                .map(generator -> generator.getGenerator(item))
                .orElse(DEFAULT.getGenerator(item));
    }

    public static void register(ItemGenerator generator) {
        generators.add(generator);
    }

}

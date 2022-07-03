package io.github.divios.lib.dLib.confirmMenu.comparators;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ComparatorFactory {

    private static final List<MatcherComparator> comparators;
    public static final ItemComparator DEFAULT = new ItemStackComparator().getComparator();

    static {
        comparators = new ArrayList<>();

        comparators.add(new OraxenComparator());
        comparators.add(new ItemsAdderComparator());
        comparators.add(new MMOItemComparator());
    }

    public static ItemComparator match(ItemStack item) {
        return comparators.stream()
                .filter(matcherComparator -> matcherComparator.matches(item))
                .findFirst()
                .map(MatcherComparator::getComparator)
                .orElse(DEFAULT);
    }

    public static void register(MatcherComparator comparator) {
        comparators.add(comparator);
    }

}

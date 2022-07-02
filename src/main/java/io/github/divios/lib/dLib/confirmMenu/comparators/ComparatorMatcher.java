package io.github.divios.lib.dLib.confirmMenu.comparators;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ComparatorMatcher {

    private static final List<MatcherComparator> comparators;
    public static final ItemComparator DEFAULT = new ItemStackComparator().getComparator();

    static {
        comparators = new ArrayList<>();

        comparators.add(new OraxenComparator());
        comparators.add(new ItemsAdderComparator());
        comparators.add(new MMOItemComparator());
    }

    public static ItemComparator match(ItemStack item) {
        ItemComparator comparator = null;
        for (MatcherComparator matcherComparator : comparators) {
            if (matcherComparator.matches(item)) {
                comparator = matcherComparator.getComparator();
                break;
            }
        }

        return comparator == null
                ? DEFAULT
                : comparator;
    }

}

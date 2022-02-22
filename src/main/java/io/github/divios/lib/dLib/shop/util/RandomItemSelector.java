package io.github.divios.lib.dLib.shop.util;

import io.github.divios.lib.dLib.dItem;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RandomItemSelector {

    private static final Predicate<dItem> filterItems = item ->
            !item.isStaticSlot() && !(item.getBuyPrice() < 0 && item.getSellPrice() <= 0)
                    && item.getRarity().getWeight() != 0;

    public static Queue<dItem> roll(Collection<dItem> items) {
        return roll(items, 54);         // 54 is the maximum amount for a chest inventory
    }

    public static Queue<dItem> roll(Collection<dItem> items, int times) {
        Queue<dItem> itemQueue = new ArrayDeque<>(20);
        itemQueue.addAll(removeStaticItems(items));

        RandomItemSelector selector = new RandomItemSelector(items);
        for (int i = 0; i < times; i++) {
            dItem rolled;
            if ((rolled = selector.roll()) == null) break;

            itemQueue.add(rolled);
            selector.remove(rolled);
        }

        return itemQueue;
    }

    private static Collection<dItem> removeStaticItems(Collection<dItem> items) {
        Map<Integer, dItem> collectedItems = new HashMap<>(items.size() / 2);

        for (Iterator<dItem> iterator = items.iterator(); iterator.hasNext(); ) {
            dItem item = iterator.next();
            if (!item.isStaticSlot()) continue;

            collectedItems.put(item.getSlot(), item);
            iterator.remove();
        }

        return collectedItems.values();
    }

    private double total;
    private List<Double> totals;
    private List<dItem> items;

    public RandomItemSelector(Collection<dItem> items) {
        initialize(items);
    }

    private void initialize(Collection<dItem> items) {
        total = 0;
        totals = new ArrayList<>(20);
        this.items = new ArrayList<>(20);
        items.forEach(item -> {
            if (!filterItems.test(item)) return;
            total += item.getRarity().getWeight();
            totals.add(total);
            this.items.add(item);
        });
    }

    public dItem roll() {
        if (totals.size() == 0) {
            return null;
        }
        double random = Math.random() * (total);
        int pos = Collections.binarySearch(totals, random);
        if (pos < 0) {
            pos = -(pos + 1);
        }
        pos = Math.min(pos, items.size() - 1);
        return items.get(pos);
    }

    public void remove(dItem outcome) {
        int index = 0;
        dItem itemRemoved = null;
        for (Iterator<dItem> iterator = items.iterator(); iterator.hasNext(); ) {
            dItem item = iterator.next();
            if (item.getID().equals(outcome.getID())) {
                itemRemoved = item;
                iterator.remove();
                break;
            }
            index++;
        }

        if (itemRemoved == null) return;
        int value = itemRemoved.getRarity().getWeight();

        totals.remove(index);
        total -= value;
        for (int i = index; i < totals.size(); i++) {
            totals.set(i, totals.get(i) - value);
        }
    }

}

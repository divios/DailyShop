package io.github.divios.lib.dLib;

import com.mojang.datafixers.types.Func;
import io.github.divios.core_lib.misc.WeightedRandom;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class dRandomItemsSelector {

    private static final Predicate<dItem> filterItems = item ->
            !(item.getBuyPrice().orElse(null).getPrice() < 0 &&
                    item.getSellPrice().orElse(null).getPrice() < 0) || item.getRarity().getWeight() != 0;

    private final Map<UUID, dItem> items;
    private final Function<dItem, Integer> getWeights;

    public static dRandomItemsSelector fromItems(Set<dItem> items) {
        return new dRandomItemsSelector(items);
    }

    public dRandomItemsSelector(Set<dItem> items) {
        this(items, dItem -> dItem.getRarity().getWeight());
    }

    public dRandomItemsSelector(Set<dItem> items, Function<dItem, Integer> getWeights) {
        this(items.stream().collect(Collectors.toMap(dItem::getUid, dItem -> dItem)), getWeights);
    }

    public dRandomItemsSelector(Map<UUID, dItem> items) {
        this(items, dItem -> dItem.getRarity().getWeight());
    }

    public dRandomItemsSelector(Map<UUID, dItem> items, Function<dItem, Integer> getWeights) {
        this.items = items.entrySet().stream()
                .filter(entry -> filterItems.test(entry.getValue()))
                .collect(Collectors
                        .toMap(Map.Entry::getKey, Map.Entry::getValue)
                );
        this.getWeights = getWeights;
    }

    public void add(dItem item) {
        items.put(item.getUid(), item);
    }

    public dItem remove(String id) {
        return remove(UUID.nameUUIDFromBytes(id.getBytes()));
    }

    public dItem remove(UUID uuid) {
        return items.remove(uuid);
    }

    public Set<dItem> getItems() {
        return Collections.unmodifiableSet(new HashSet<>(items.values()));
    }

    public Set<dItem> roll() {
        return roll(54);
    }

    public Set<dItem> roll(int max) {
        Set<dItem> rolledItems = new HashSet<>();

        WeightedRandom<dItem> randomSelector = WeightedRandom.fromCollection(items.values(), dItem::clone, getWeights::apply);

        for (int i = 0; i < max; i++) {
            dItem rolledItem = randomSelector.roll();
            if (rolledItem == null) break;

            rolledItem.generateNewBuyPrice();
            rolledItem.generateNewSellPrice();
            rolledItems.add(rolledItem);
            randomSelector.remove(rolledItem);
        }

        return rolledItems;
    }

}

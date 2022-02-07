package io.github.divios.lib.dLib.shop.util;

import io.github.divios.lib.dLib.dItem;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class DailyItemsMap {

    private final ConcurrentHashMap<Integer, dItem> itemsSlots;
    private final ConcurrentHashMap<String, dItem> itemsIds;

    public DailyItemsMap() {
        this.itemsSlots = new ConcurrentHashMap<>();
        this.itemsIds = new ConcurrentHashMap<>();
    }

    public int size() {
        return itemsIds.size();
    }

    public boolean isEmpty() {
        return itemsSlots.isEmpty();
    }

    public boolean contains(String id) {
        return itemsIds.containsKey(id);
    }

    public boolean contains(int slot) {
        return itemsSlots.containsKey(slot);
    }

    public Map<String, dItem> getItems() {
        return Collections.unmodifiableMap(itemsIds);
    }
    public dItem get(String id) {
        return itemsIds.get(id);
    }

    public dItem get(int slot) {
        return itemsSlots.get(slot);
    }

    public void put(dItem item) {
        itemsIds.put(item.getID(), item);
        itemsSlots.put(item.getSlot(), item);
    }

    public dItem remove(String id) {
        dItem removed;
        if ((removed = itemsIds.remove(id)) == null) return null;

        itemsSlots.remove(removed.getSlot());
        return removed;
    }

    public dItem remove(int slot) {
        dItem removed;
        if ((removed = itemsSlots.remove(slot)) == null) return null;

        itemsIds.remove(removed.getID());
        return removed;
    }

    public void putAll(@NotNull Collection<dItem> items) {
        items.forEach(this::put);
    }

    public void clear() {
        itemsIds.clear();
        itemsSlots.clear();
    }

    public void forEach(Consumer<dItem> itemConsumer) {
        itemsIds.values().forEach(itemConsumer);
    }

    public Iterator<dItem> iterator() {
        Iterator<dItem> innerIterator = itemsIds.values().iterator();
        final dItem[] next = new dItem[1];

        return new Iterator<dItem>() {

            @Override
            public boolean hasNext() {
                return innerIterator.hasNext();
            }

            @Override
            public dItem next() {
                return next[0] = innerIterator.next();
            }

            @Override
            public void remove() {
                innerIterator.remove();
                itemsSlots.remove(next[0].getSlot());
            }
        };
    }

}

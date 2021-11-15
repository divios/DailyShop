package io.github.divios.lib.storage.parser;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.events.updateShopEvent;
import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;

import java.util.*;

public class dShopState {

    private final String name;
    private final Integer size;
    private final Map<UUID, dButtonState> display_items = new LinkedHashMap<>();

    public dShopState(String name, Integer size, Collection<dItem> items) {
        this.name = FormatUtils.unColor(name);
        this.size = size;

        items.stream()
                .sorted(Comparator.comparingInt(dItem::getSlot))
                .forEach(dItem -> this.display_items.put(dItem.getUid(), dButtonState.of(dItem)));
    }

    public String getName() {
        return name;
    }

    public Integer getSize() {
        return size;
    }

    public Map<UUID, dButtonState> getDisplay_items() {
        return display_items;
    }

    public void apply(dShop shop) {

        dInventory newInv = new dInventory(FormatUtils.color(name), size, shop);
        display_items.entrySet().stream()
                .filter(entry -> entry.getValue().getSlot() < size)
                .map(entry -> entry.getValue().parseItem(entry.getKey()))
                .forEach(entry -> newInv.addButton(entry, entry.getSlot()));

        Events.callEvent(new updateShopEvent(shop, newInv, true));

    }

}

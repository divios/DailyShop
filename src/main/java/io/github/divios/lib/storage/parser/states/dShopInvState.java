package io.github.divios.lib.storage.parser.states;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.events.updateShopEvent;
import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;

import java.util.*;
import java.util.stream.Collectors;

public class dShopInvState {

    private final String title;
    private final Integer size;
    private final Map<UUID, dButtonState> display_items = new LinkedHashMap<>();

    public static dShopInvStateBuilder builder() { return new dShopInvStateBuilder(); }

    public static dShopInvState toState(dShop shop) {
        dInventory defaultInv = shop.getGuis().getDefault();
        return new dShopInvState(
                defaultInv.getInventoryTitle(),
                defaultInv.getInventorySize(),
                defaultInv.getButtons().values().stream()
                        .filter(dItem -> !defaultInv.getDailyItemsSlots().contains(dItem.getSlot()))      // Filter only buttons, not daily items
                        .collect(Collectors.toList()));
    }

    public dShopInvState(String name, Integer size, Collection<dItem> items) {
        this.title = FormatUtils.unColor(name);
        this.size = size;

        items.stream()
                .sorted(Comparator.comparingInt(dItem::getSlot))
                .forEach(dItem -> this.display_items.put(dItem.getUid(), dButtonState.of(dItem)));
    }

    public String getTitle() {
        return title;
    }

    public Integer getSize() {
        return size;
    }

    public Map<UUID, dButtonState> getDisplay_items() {
        return display_items;
    }

    public void apply(dShop shop) {

        dInventory newInv = new dInventory(FormatUtils.color(title), size, shop);
        display_items.entrySet().stream()
                .filter(entry -> entry.getValue().getSlot() < size)
                .map(entry -> entry.getValue().parseItem(entry.getKey()))
                .forEach(entry -> newInv.addButton(entry, entry.getSlot()));

        Events.callEvent(new updateShopEvent(shop, newInv, true));
    }

    public dInventory build() {
        return new dInventory()
    }

    public static final class dShopInvStateBuilder {
        private String title;
        private Integer size;
        private Map<UUID, dButtonState> display_items = new LinkedHashMap<>();

        private dShopInvStateBuilder() {
        }

        public static dShopInvStateBuilder adShopState() {
            return new dShopInvStateBuilder();
        }

        public dShopInvStateBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public dShopInvStateBuilder withSize(Integer size) {
            this.size = size;
            return this;
        }

        public dShopInvStateBuilder withDisplay_items(Map<UUID, dButtonState> display_items) {
            this.display_items = display_items;
            return this;
        }

        public dShopInvState build() {
            dShopInvState dShopInvState = new dShopInvState(null, size, null);
            return dShopInvState;
        }
    }
}

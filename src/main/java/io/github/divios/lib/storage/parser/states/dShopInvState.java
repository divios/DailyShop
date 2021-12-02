package io.github.divios.lib.storage.parser.states;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.events.updateShopEvent;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;

import java.util.*;
import java.util.stream.Collectors;

public class dShopInvState {

    private final String title;
    private final Integer size;
    private final List<dButtonState> display_items = new ArrayList<>();

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
        this.title = FormatUtils.color(name);
        this.size = size;

        items.stream()
                .sorted(Comparator.comparingInt(dItem::getSlot))
                .forEach(dItem -> this.display_items.add(dButtonState.of(dItem)));
    }

    protected dShopInvState (String title, Integer size, List<dButtonState> display_items) {
        this.title = title;
        this.size = size;
        this.display_items.addAll(display_items);
    }

    public String getTitle() {
        return title;
    }

    public Integer getSize() {
        return size;
    }

    public List<dButtonState> getDisplay_items() {
        return display_items;
    }

    public void apply(dShop shop) {
        dInventory newInv = new dInventory(FormatUtils.color(title), size, shop);
        display_items.stream()
                .filter(entry -> entry.getSlot() < size)
                .map(dButtonState::parseItem)
                .forEach(entry -> newInv.addButton(entry.clone(), entry.getSlot()));

        Events.callEvent(new updateShopEvent(shop, newInv, true));
    }

    public static final class dShopInvStateBuilder {
        private String title;
        private Integer size;
        private List<dButtonState> display_items = new ArrayList<>();

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

        public dShopInvStateBuilder withDisplay_items(List<dButtonState> display_items) {
            this.display_items = display_items;
            return this;
        }

        public dShopInvState build() {
            runPreconditions();
            return new dShopInvState(title, size, display_items);
        }

        private void runPreconditions() {
            if (title == null) title = "";
            if (size == null) size = 27;
            if (display_items == null) display_items = Collections.emptyList();
        }
    }

}

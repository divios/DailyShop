package io.github.divios.lib.storage.parser.states;

import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;

import java.util.*;

public class dShopState {

    private String id;
    private dInventory invState;
    private Map<UUID, dItem> itemsCollect;

    public static dShopStateBuilder builder() { return new dShopStateBuilder(); }

    public static dShopState fromShop(dShop shop) { return new dShopState(shop); }

    protected dShopState() {}

    public dShopState(dShop shop) {
        id = shop.getName();
        invState = shop.getGuis().getDefault();

        itemsCollect = new LinkedHashMap<>();
        shop.getItems().forEach(dItem -> itemsCollect.put(dItem.getUid(), dItem));
    }

    public String getId() {
        return id;
    }

    public dInventory getInvState() {
        return invState;
    }

    public Map<UUID, dItem> getItemsCollect() {
        return itemsCollect;
    }

    public static final class dShopStateBuilder {
        private String id;
        private dInventory invState;
        private Map<UUID, dItem> itemsCollect;

        private dShopStateBuilder() {
        }

        public static dShopStateBuilder adShopState() {
            return new dShopStateBuilder();
        }

        public dShopStateBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public dShopStateBuilder withInvState(dInventory invState) {
            this.invState = invState;
            return this;
        }

        public dShopStateBuilder withItemsCollect(Map<UUID, dItem> itemsCollect) {
            this.itemsCollect = itemsCollect;
            return this;
        }

        public dShopState build() {
            dShopState dShopState = new dShopState();
            dShopState.id = this.id;
            dShopState.itemsCollect = this.itemsCollect;
            dShopState.invState = this.invState;
            return dShopState;
        }
    }
}

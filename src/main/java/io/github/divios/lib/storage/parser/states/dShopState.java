package io.github.divios.lib.storage.parser.states;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class dShopState {

    private String id;
    private dShopInvState invState;
    private List<dItemState> itemsCollect;

    public static dShopStateBuilder builder() { return new dShopStateBuilder(); }

    public static dShopState fromShop(dShop shop) { return new dShopState(shop); }

    protected dShopState() {}

    public dShopState(dShop shop) {
        id = shop.getName();
        invState = dShopInvState.toState(shop);

        itemsCollect = new ArrayList<>();
        shop.getItems().forEach(dItem -> itemsCollect.add(dItemState.of(dItem)));
    }

    public String getId() {
        return id;
    }

    public dShopInvState getInvState() {
        return invState;
    }

    public List<dItemState> getItemsCollect() {
        return itemsCollect;
    }

    public dShop createShop() {
        dShop newShop = new dShop(id);
        invState.apply(newShop);
        newShop.setItems(buildItems(itemsCollect));

        return newShop;
    }

    private Set<dItem> buildItems(Collection<dItemState> itemsToBuild) {
        return itemsToBuild.stream()
                .map(dItemState::parseItem)
                .collect(Collectors.toSet());
    }

    public static final class dShopStateBuilder {
        private String id;
        private dShopInvState invState;
        private List<dItemState> itemsCollect;

        private dShopStateBuilder() {
        }

        public static dShopStateBuilder adShopState() {
            return new dShopStateBuilder();
        }

        public dShopStateBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public dShopStateBuilder withInvState(dShopInvState invState) {
            this.invState = invState;
            return this;
        }

        public dShopStateBuilder withItemsCollect(List<dItemState> itemsCollect) {
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

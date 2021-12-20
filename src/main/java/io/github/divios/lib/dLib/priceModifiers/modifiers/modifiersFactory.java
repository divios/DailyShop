package io.github.divios.lib.dLib.priceModifiers.modifiers;

import com.google.common.base.Preconditions;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;

public class modifiersFactory {

    private String id;
    private String scope;
    private String type;
    private Double value;
    private String shopID;
    private String itemID;

    public modifiersFactory withId(String id) {
        this.id = id;
        return this;
    }

    public modifiersFactory withScope(String scope) {
        this.scope = scope;
        return this;
    }

    public modifiersFactory withType(String type) {
        this.type = type;
        return this;
    }

    public modifiersFactory withValue(double value) {
        this.value = value;
        return this;
    }

    public modifiersFactory withShopID(String shopID) {
        this.shopID = shopID;
        return this;
    }

    public modifiersFactory withItemID(String itemID) {
        this.itemID = itemID;
        return this;
    }

    public priceModifier buildModifier() {

        Preconditions.checkNotNull(id, "id cannot be null");
        Preconditions.checkNotNull(scope, "Scope cannot be null");
        Preconditions.checkNotNull(type, "Type cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");
        Preconditions.checkArgument(value > -1.0, "Value cannot be less or equal than -100%");

        priceModifier.scope[] scope = {null};
        priceModifier.type[] type = {null};

        Preconditions.checkArgument(Utils.testRunnable(() -> scope[0] = priceModifier.scope.getFromKey(this.scope)), "Invalid scope");
        Preconditions.checkArgument(Utils.testRunnable(() -> type[0] = priceModifier.type.getFromKey(this.type)), "Invalid type");

        priceModifier modifier;
        switch (scope[0]) {
            case GLOBAL:
                modifier = new globalModifier(this.id, type[0], this.value);
                break;
            case SHOP:
                Preconditions.checkNotNull(shopID, "Shop id cannot be null on SHOP scope");
                modifier = new shopModifier(id, type[0], value, shopID);
                break;
            case ITEM:
                Preconditions.checkNotNull(shopID, "Shop id cannot be null on SHOP scope");
                Preconditions.checkNotNull(itemID, "Item id cannot be null on ITEM scope");
                modifier = new itemModifier(id, type[0], value, shopID, itemID);
                break;
            default:
                throw new RuntimeException("Invalid scope");
        }

        return modifier;
    }

}

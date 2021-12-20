package io.github.divios.lib.dLib.priceModifiers.modifiers;

import io.github.divios.lib.dLib.priceModifiers.abstractModifier;
import io.github.divios.lib.dLib.priceModifiers.modifierContext;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;

public class itemModifier extends abstractModifier {

    private static final scope SCOPE = scope.ITEM;

    private final String shopName;
    private final String itemID;

    itemModifier(String id, priceModifier.type type, double value, String shopName, String itemID) {
        super(id, type, value);

        this.shopName = shopName;
        this.itemID = itemID;
    }

    @Override
    public scope scope() {
        return SCOPE;
    }

    @Override
    public boolean appliesToContext(modifierContext context) {
        return this.shopName.equalsIgnoreCase(context.getShopName())
                && this.itemID.equalsIgnoreCase(context.getItemID())
                && super.type.isEquals(context.getType());
    }
}

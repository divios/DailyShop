package io.github.divios.lib.dLib.priceModifiers.modifiers;

import io.github.divios.lib.dLib.priceModifiers.abstractModifier;
import io.github.divios.lib.dLib.priceModifiers.modifierContext;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;

public class shopModifier extends abstractModifier {

    private static final scope SCOPE = scope.SHOP;

    private final String shopName;

    shopModifier(String id, priceModifier.type type, double value, String shopName) {
        super(id, type, value);

        this.shopName = shopName;
    }

    @Override
    public scope scope() {
        return SCOPE;
    }

    @Override
    public boolean appliesToContext(modifierContext context) {
        return shopName.equalsIgnoreCase(context.getShopName())
                && super.type.isEquals(context.getType());
    }
}

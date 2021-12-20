package io.github.divios.lib.dLib.priceModifiers.modifiers;

import io.github.divios.lib.dLib.priceModifiers.abstractModifier;
import io.github.divios.lib.dLib.priceModifiers.modifierContext;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;

public class globalModifier extends abstractModifier {

    private static final priceModifier.scope SCOPE = scope.GLOBAL;

    globalModifier(String id, priceModifier.type type, double value) {
        super(id, type, value);
    }

    @Override
    public scope scope() {
        return SCOPE;
    }

    @Override
    public boolean appliesToContext(modifierContext context) {
        return type.isEquals(context.getType());
    }


}

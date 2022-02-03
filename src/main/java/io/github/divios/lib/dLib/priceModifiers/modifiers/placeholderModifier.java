package io.github.divios.lib.dLib.priceModifiers.modifiers;

import io.github.divios.core_lib.utils.Primitives;
import io.github.divios.jtext.JText;
import io.github.divios.lib.dLib.priceModifiers.abstractModifier;
import io.github.divios.lib.dLib.priceModifiers.modifierContext;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;

public class placeholderModifier extends abstractModifier {

    private final String placeholder;

    protected placeholderModifier(String id, priceModifier.type type, String value) {
        super(id, type, 0.0);
        this.placeholder = value;
    }

    @Override
    public scope scope() {
        return scope.GLOBAL;
    }

    @Override
    public boolean appliesToContext(modifierContext context) {
        return true;
    }

    @Override
    public double getValue(modifierContext context) {
        String valueStr = JText.builder()
                .withTag("{", "}")
                .withTemplate("player", context.getPlayer().getName())
                .withTemplate("item_id", context.getItemID())
                .parsePlaceholderAPI()
                .parse(placeholder, context.getPlayer());

        return Primitives.isDouble(valueStr)
                ? Primitives.getAsDouble(valueStr)
                : 0.0;

    }

}

package io.github.divios.lib.dLib.priceModifiers;

import io.github.divios.lib.dLib.priceModifiers.modifiers.modifiersFactory;
import org.bukkit.permissions.Permission;

import java.util.Arrays;

public interface priceModifier extends Comparable<priceModifier> {

    String id();

    scope scope();

    type type();

    Permission getPermission();

    boolean appliesToContext(modifierContext context);

    double getValue(modifierContext context);

    default int getPriority() {
        return scope.values().length - scope().ordinal();       // Reverse of ordinal
    }

    default int compareTo(priceModifier modifier) {
        return Integer.compare(this.getPriority(), modifier.getPriority());
    }

    static modifiersFactory getFactory() {
        return new modifiersFactory();
    }


    enum scope {
        GLOBAL,
        PLACEHOLDER,
        SHOP,
        ITEM;

        public static scope getFromKey(String scope) {
            return Arrays.stream(values())
                    .filter(scope1 -> scope1.name().equalsIgnoreCase(scope))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Invalid scope"));
        }
    }

    enum type {
        BUY,
        SELL,
        BOTH;

        public static type getFromKey(String type) {
            return Arrays.stream(values())
                    .filter(type1 -> type1.name().equalsIgnoreCase(type))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Invalid key"));
        }

        public boolean isEquals(type other) {
            if (other == null) return false;

            if (this == BOTH) return true;
            else return this.equals(other);
        }

    }

}

package io.github.divios.lib.dLib.dTransaction;

import java.util.Arrays;
import java.util.Optional;

public interface Transactions {

    enum Type {
        BUY,
        SELL,
        NONE;

        public static Type getByKey(String s) {
            return Arrays.stream(values())
                    .filter(type -> type.name().equalsIgnoreCase(s))
                    .findFirst().orElseThrow(() -> new RuntimeException("Invalid type"));
        }

        public static Optional<Type> getOptionalByKey(String s) {
            return Arrays.stream(values())
                    .filter(type -> type.name().equalsIgnoreCase(s))
                    .findFirst();
        }

    }

}

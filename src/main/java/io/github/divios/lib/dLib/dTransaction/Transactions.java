package io.github.divios.lib.dLib.dTransaction;

import java.util.Arrays;

public interface Transactions {

    enum Type {
        BUY,
        SELL;

        public static Type getByKey(String s) {
            return Arrays.stream(values())
                    .filter(type -> type.name().equalsIgnoreCase(s))
                    .findFirst().orElseThrow(() -> new RuntimeException("Invalid type"));
        }

    }

}

package io.github.divios.lib.dLib.dTransaction;

import java.util.Arrays;

public interface Transactions {

    static BuyTransaction BuyTransaction() {
        return new BuyTransaction();
    }

    static SellTransaction SellTransaction() {
        return new SellTransaction();
    }

    static SingleTransaction.SingleTransactionBuilder Custom() {
        return SingleTransaction.create();
    }

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

package io.github.divios.lib.dLib.dTransaction;

public interface Transactions {

    static BuyTransaction BuyTransaction() {
        return new BuyTransaction();
    }

    static SellTransaction SellTransaction() { return new SellTransaction(); }

}

package io.github.divios.lib.dLib.dTransaction;

public interface Transactions {

    static BuyTransaction createBuyType() {
        return new BuyTransaction();
    }

}

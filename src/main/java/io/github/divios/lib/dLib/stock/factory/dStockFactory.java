package io.github.divios.lib.dLib.stock.factory;

import io.github.divios.lib.dLib.stock.dStock;

public class dStockFactory {

    private static final dStockInfinite INFINITE = new dStockInfinite();

    public static dStock INDIVIDUAL(int defaultStock) {
        return new dStockIndividual(defaultStock);
    }

    public static dStock GLOBAL(int defaultStock) {
        return new dStockGlobal(defaultStock);
    }

    public static dStock INDIVIDUAL(int defaultStock, int maxStock) {
        return new dStockIndividual(defaultStock, maxStock);
    }

    public static dStock GLOBAL(int defaultStock, int maxStock) {
        return new dStockGlobal(defaultStock, maxStock);
    }

    public static dStock INFINITE() {
        return INFINITE;
    }

}

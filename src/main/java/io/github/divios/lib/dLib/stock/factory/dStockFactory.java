package io.github.divios.lib.dLib.stock.factory;

import io.github.divios.lib.dLib.stock.dStock;

public class dStockFactory {

    public static dStock INDIVIDUAL(int defaultStock) {
        return new dStockIndividual(defaultStock);
    }

    public static dStock GLOBAL(int defaultStock) {
        return new dStockGlobal(defaultStock);
    }

}

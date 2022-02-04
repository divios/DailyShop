package io.github.divios.lib.dLib.stock.factory;

import io.github.divios.lib.dLib.stock.dStock;

import java.util.UUID;

final class dStockIndividual extends dStock {

    dStockIndividual(int defaultStock) {
        super(defaultStock);
    }

    dStockIndividual(int defaultStock, int maximumStock) {
        super(defaultStock, maximumStock);
    }

    @Override
    public String getName() {
        return "INDIVIDUAL";
    }

    @Override
    public boolean isIndividual() {
        return true;
    }

    @Override
    protected UUID getKey(UUID uuid) {
        return uuid;
    }

}

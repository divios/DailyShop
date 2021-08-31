package io.github.divios.lib.dLib.stock.factory;


import io.github.divios.core_lib.utils.Log;

import java.util.UUID;

final class dStockIndividual extends dStockAbstract {

    dStockIndividual(int defaultStock) {
        super(defaultStock);
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

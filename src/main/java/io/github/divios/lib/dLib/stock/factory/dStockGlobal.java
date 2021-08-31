package io.github.divios.lib.dLib.stock.factory;

import io.github.divios.core_lib.utils.Log;

import java.util.UUID;

final class dStockGlobal extends dStockAbstract {

    private final UUID globalKey = UUID.randomUUID();
    
    dStockGlobal(int defaultStock) {
        super(defaultStock);
        super.stocks.put(globalKey, defaultStock);
    }

    @Override
    public String getName() {
        return "GLOBAL";
    }

    @Override
    public boolean isIndividual() {
        return false;
    }

    @Override
    protected UUID getKey(UUID uuid) {
        return globalKey;
    }


}

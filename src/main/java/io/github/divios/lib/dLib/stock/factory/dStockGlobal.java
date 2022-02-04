package io.github.divios.lib.dLib.stock.factory;

import io.github.divios.lib.dLib.stock.dStock;

import java.util.UUID;

final class dStockGlobal extends dStock {

    private final UUID globalKey = UUID.nameUUIDFromBytes("randomString_rds".getBytes());

    dStockGlobal(int defaultStock) {
        super(defaultStock);
        super.stocks.put(globalKey, defaultStock);
    }

    dStockGlobal(int defaultStock, int maximumStock) {
        super(defaultStock, maximumStock);
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

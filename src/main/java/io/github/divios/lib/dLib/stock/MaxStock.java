package io.github.divios.lib.dLib.stock;

import java.util.UUID;

public class MaxStock extends dStock {

    protected MaxStock(int defaultStock) {
        super(defaultStock);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isIndividual() {
        return false;
    }

    @Override
    protected UUID getKey(UUID uuid) {
        return null;
    }
}

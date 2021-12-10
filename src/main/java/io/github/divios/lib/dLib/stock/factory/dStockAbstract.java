package io.github.divios.lib.dLib.stock.factory;

import com.google.common.collect.HashBiMap;
import io.github.divios.lib.dLib.stock.dStock;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public abstract class dStockAbstract implements dStock{

    protected final int defaultStock;
    protected final Map<UUID, Integer> stocks = Collections.synchronizedMap(HashBiMap.create());

    protected dStockAbstract(int defaultStock, Map<UUID, Integer> stocks) {
        this.defaultStock = defaultStock;
        this.stocks.putAll(stocks);
    }

    protected dStockAbstract(int defaultStock) {
        this.defaultStock = defaultStock;
    }

    @Override
    public abstract String getName();

    @Override
    public int getDefault() { return defaultStock; }

    @Override
    public Integer get(UUID p) {
        if (!exists(p) && isIndividual()) reset(p);          // If doesn't exist on individual, create it
        return stocks.get(getKey(p));
    }

    @Override
    public void set(UUID p, int stock) {
        stocks.put(getKey(p), stock);
    }

    @Override
    public boolean exists(UUID p) {
        return stocks.containsKey(getKey(p));
    }

    @Override
    public void increment(UUID p, int amount) {
        if (!exists(p)) stocks.put(p, defaultStock + amount);
        else stocks.compute(getKey(p), (uuid, integer) -> integer + amount);
    }

    @Override
    public void decrement(UUID p, int amount) {
        if (!exists(p)) stocks.put(p, defaultStock - amount);
        else stocks.compute(getKey(p), (uuid, integer) -> integer - amount);
    }

    @Override
    public void reset(UUID p) { stocks.put(getKey(p), defaultStock);
    }

    @Override
    public void resetAll() { stocks.entrySet().forEach(uuidIntegerEntry -> uuidIntegerEntry.setValue(defaultStock)); }

    @Override
    public Map<UUID, Integer> getAll() {
        return Collections.unmodifiableMap(stocks);
    }

    @Override
    public abstract boolean isIndividual();

    protected abstract UUID getKey(UUID uuid);

    @Override
    public String toString() {
        return getName() + ":" + defaultStock;
    }

}

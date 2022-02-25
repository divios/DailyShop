package io.github.divios.lib.dLib.stock.factory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.lib.dLib.stock.dStock;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

class dStockInfinite extends dStock {

    protected dStockInfinite() {
        super(Integer.MAX_VALUE);
    }

    @Override
    public String getName() {
        return "Infinite";
    }

    @Override
    public boolean isIndividual() {
        return false;
    }

    @Override
    protected UUID getKey(UUID uuid) {
        return null;
    }

    @Override
    public Integer get(@NotNull UUID p) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean exists(@NotNull UUID p) {
        return true;
    }

    @Override
    public void set(@NotNull UUID p, int stock) {
    }

    @Override
    public void increment(@NotNull UUID p, int amount) {
    }

    @Override
    public void decrement(@NotNull UUID p, int amount) {
    }

    @Override
    public void reset(@NotNull UUID p) {
    }

    @Override
    public boolean isSimilar(@NotNull dStock stock) {
        return Objects.equals(getName(), stock.getName());
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public dStock clone() {
        return this;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "Infinite");

        return json;
    }
}

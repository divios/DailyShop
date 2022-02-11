package io.github.divios.lib.dLib;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.valuegenerators.FixedValueGenerator;
import io.github.divios.dailyShop.utils.valuegenerators.RandomIntervalGenerator;
import io.github.divios.dailyShop.utils.valuegenerators.ValueGenerator;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("unused")
public class dPrice implements Serializable, Cloneable {

    private final ValueGenerator generator;
    private double actualPrice;

    public static dPrice EMPTY() {
        return new dPrice(new FixedValueGenerator(0));
    }

    public static dPrice fromJson(JsonElement json) {
        try {           // First try the legacy fromJson
            return legacyFromJson(json);
        } catch (Exception ignored) {}

        JsonObject object = json.getAsJsonObject();

        Preconditions.checkArgument(object.has("currentPrice"), "No actual price");
        Preconditions.checkArgument(object.has("generator"), "No generator");

        double currentPrice = object.get("currentPrice").getAsDouble();
        ValueGenerator generator = ValueGenerator.fromJson(object.get("generator"));

        return new dPrice(currentPrice, generator);
    }

    private static dPrice legacyFromJson(JsonElement json) {
        JsonObject object = json.getAsJsonObject();

        Preconditions.checkArgument(object.has("actualPrice"), "No actual price");

        double actualPrice = object.get("actualPrice").getAsDouble();

        if (object.has("max") && object.has("min")) {
            double min = object.get("min").getAsDouble();
            double max = object.get("max").getAsDouble();

            return new dPrice(actualPrice, new RandomIntervalGenerator(min, max));
        } else
            return new dPrice(new FixedValueGenerator(actualPrice));

    }

    public static dPrice empty() {
        return new dPrice(new FixedValueGenerator(0));
    }

    public dPrice(ValueGenerator generator) {
        this(generator.generate(), generator);
    }

    public dPrice(double price, ValueGenerator generator) {
        this.actualPrice = price;
        this.generator = generator;
    }

    public double getPrice() {
        return actualPrice;
    }

    @Deprecated
    public double getPriceForPlayer(Player p, dShop shop, String itemID, priceModifier.type type) {
        double price = getPrice();
        double modifier = DailyShop.get().getPriceModifiers().getModifier(p, shop == null ? null : shop.getName(), itemID, type);

        return price + (price * modifier);
    }

    public void generateNewPrice() {
        actualPrice = generator.generate();
    }

    protected double getActualPrice() {
        return actualPrice;
    }

    public ValueGenerator getGenerator() {
        return generator;
    }

    @Override
    public String toString() {
        return "dPrice{" +
                "generator=" + generator +
                ", actualPrice=" + actualPrice +
                '}';
    }

    public boolean isSimilar(@Nullable dPrice price) {
        if (this == price) return true;
        if (price == null) return false;

        return generator.isSimilar(price.generator);
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add("generator", generator.toJson());
        json.addProperty("currentPrice", actualPrice);

        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        dPrice that = (dPrice) o;
        return Double.compare(actualPrice, that.actualPrice) == 0
                && generator.isSimilar(that.generator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actualPrice, generator);
    }

    @Override
    public dPrice clone() {
        dPrice T;
        try {
            T = (dPrice) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        return T;
    }

}

package io.github.divios.lib.dLib;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("unused")
public class dPrice implements Serializable, Cloneable {

    private boolean randomFlag = false;
    private double minPrice = 0;
    private double maxPrice = 0;
    private double actualPrice;

    public static dPrice EMPTY() {
        return new dPrice();
    }

    public static dPrice fromString(String str) {
        String[] prices = str.split(":");
        if (prices.length == 1)
            return new dPrice(Double.parseDouble(prices[0]));
        else
            return new dPrice(Double.parseDouble(prices[0]), Double.parseDouble(prices[1]));
    }

    public static dPrice fromJson(JsonElement json) {
        JsonObject object = json.getAsJsonObject();

        Preconditions.checkArgument(object.has("actualPrice"), "No actual price");

        dPrice price = EMPTY();

        price.actualPrice = object.get("actualPrice").getAsDouble();
        price.minPrice = object.get("minPrice").getAsDouble();
        price.maxPrice = object.get("maxPrice").getAsDouble();
        price.randomFlag = object.get("randomFlag").getAsBoolean();

        return price;
    }

    private dPrice() {
    }

    public dPrice(double price) {
        this.actualPrice = price;
    }

    public dPrice(double minPrice, double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        randomFlag = true;
        this.actualPrice = generateRandomPrice();
    }

    public static dPrice empty() {
        return new dPrice();
    }

    /**
     * Returns the price of the item. If it was constructed with two values, returns
     * a random value between them, if not, returns the same value passed
     *
     * @return the price of the item
     */
    private double generateRandomPrice() {
        return Utils.round(minPrice + Math.random() * (maxPrice - minPrice), 2);
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
        if (randomFlag)
            actualPrice = generateRandomPrice();
    }

    protected boolean isRandomFlag() {
        return randomFlag;
    }

    protected double getMinPrice() {
        return minPrice;
    }

    protected double getMaxPrice() {
        return maxPrice;
    }

    protected double getActualPrice() {
        return actualPrice;
    }

    public String toPrettyString() {
        if (randomFlag)
            return PrettyPrice.pretty(minPrice) + " : " + PrettyPrice.pretty(maxPrice);
        else return PrettyPrice.pretty(actualPrice);
    }

    @Override
    public String toString() {
        if (randomFlag)
            return minPrice + " : " + maxPrice;
        else
            return String.valueOf(actualPrice);
    }

    public boolean isSimilar(@Nullable dPrice price) {
        if (this == price) return true;
        if (price == null || randomFlag != price.randomFlag) return false;

        //DebugLog.info("minprice: " + minPrice + "-" + price.minPrice);
        //DebugLog.info("maxprice: " + maxPrice + "-" + price.maxPrice);
        //DebugLog.info("actualPrice: " + actualPrice + "-" + price.actualPrice);

        if (randomFlag) {
            return Double.compare(maxPrice, price.maxPrice) == 0
                    && Double.compare(minPrice, price.minPrice) == 0;
        } else {
            return Double.compare(actualPrice, price.actualPrice) == 0;
        }
    }

    public JsonElement toJson() {
        return JsonBuilder.object()
                .add("randomFlag", randomFlag)
                .add("minPrice", minPrice)
                .add("maxPrice", maxPrice)
                .add("actualPrice", actualPrice)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        dPrice dPrice = (dPrice) o;
        return randomFlag == dPrice.randomFlag
                && Double.compare(dPrice.minPrice, minPrice) == 0
                && Double.compare(dPrice.maxPrice, maxPrice) == 0
                && Double.compare(actualPrice, dPrice.actualPrice) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomFlag, minPrice, maxPrice, actualPrice);
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

    public enum type {
        BUY,
        SELL
    }


}

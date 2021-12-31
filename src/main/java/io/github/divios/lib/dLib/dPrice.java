package io.github.divios.lib.dLib;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.Objects;

public class dPrice implements Serializable {

    private boolean randomFlag = false;
    private double minPrice = 0;
    private double maxPrice = 0;
    private double actualPrice;

    public static dPrice EMPTY() {
        return new dPrice();
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

    public double getPriceForPlayer(Player p, dShop shop, String itemID, priceModifier.type type) {
        double price = getPrice();
        double modifier = DailyShop.get().getPriceModifiers().getModifier(p, shop == null ? null : shop.getName(), itemID, type);

        return price + (price * modifier);
    }

    public void generateNewPrice() {
        if (randomFlag)
            actualPrice = generateRandomPrice();
    }

    /**
     * Return a visual representation of the price (Ex: 150 - 170)
     *
     * @return price as string
     */
    public String getVisualPrice() {
        if (randomFlag)
            return PriceWrapper.format(minPrice) + " - " + PriceWrapper.format(maxPrice);
        else if (actualPrice <= 0) return FormatUtils.color("&c" + XSymbols.TIMES_3.parseSymbol());
        else return PriceWrapper.format(actualPrice);
    }

    @Override
    public String toString() {
        if (randomFlag)
            return minPrice + " : " + maxPrice;
        else return String.valueOf(actualPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        dPrice dPrice = (dPrice) o;
        return randomFlag == dPrice.randomFlag && Double.compare(dPrice.minPrice, minPrice) == 0 && Double.compare(dPrice.maxPrice, maxPrice) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomFlag, minPrice, maxPrice, actualPrice);
    }

    public enum type {
        BUY,
        SELL
    }


}

package io.github.divios.lib.itemHolder;

import io.github.divios.dailyrandomshop.utils.utils;

import java.io.Serializable;

public class dPrice implements Serializable {

    private boolean randomFlag = false;
    private double minPrice = 0;
    private double maxPrice = 0;

    public dPrice(double price) {
        this.minPrice = price;
    }

    public dPrice(double minPrice, double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        randomFlag = true;
    }

    /**
     * Returns the price of the item. If it was constructed with two values, returns
     * a random value between them, if not, returns the same value passed
     * @return the price of the item
     */
    public double generateRandomPrice() {

        if (!randomFlag)
            return minPrice;
        else {
            return utils.round(minPrice + Math.random() *
                    (maxPrice - minPrice), 2);
        }
    }

    /**
     * Return a visual representation of the price (Ex: 150 - 170)
     * @return price as string
     */
    public String getVisualPrice() {
        if (randomFlag)
            return minPrice + " - " + maxPrice;
        else
            return "" + minPrice;
    }





}

package io.github.divios.dailyShop.utils;

import io.github.divios.dailyShop.DailyShop;

import java.text.DecimalFormat;

public class PriceWrapper {

    protected PriceWrapper() {}

    public static String format(double d) {
        DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"
        return df.format(d);
    }

}

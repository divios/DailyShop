package io.github.divios.dailyShop.utils;

import io.github.divios.dailyShop.DailyShop;

import java.text.DecimalFormat;

public class PriceFormatter {

    protected PriceFormatter() {}

    public static String format(double d) {
        if (DailyShop.getInstance().configM.getSettingsYml().INTEGER_VAL)
            return String.valueOf(d);
        else
            return DecimalFormat.getNumberInstance().format(d);
    }

}

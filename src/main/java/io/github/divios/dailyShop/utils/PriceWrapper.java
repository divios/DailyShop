package io.github.divios.dailyShop.utils;

import io.github.divios.dailyShop.DailyShop;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PriceWrapper {

    protected PriceWrapper() {}

    public static String format(double d) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("###,###.##", symbols); // or pattern "###,###.##$"
        return df.format(d);
    }

}

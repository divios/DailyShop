package io.github.divios.dailyShop.utils;

import io.github.divios.dailyShop.files.Settings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class PrettyPrice {

    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    private static final DecimalFormat df = new DecimalFormat("###,###.##", symbols); // or pattern "###,###.##$"

    public static String pretty(double d) {
        String out;
        d = Utils.round(d, 2);

        out = String.format("%,.2f", d);

        if (Settings.INTEGER_VAL.getValue().getAsBoolean()
                && (out.endsWith(",00") || out.endsWith(".00")))
            out = out.substring(0, out.length() - 3);

        return out;
    }

    private PrettyPrice() {
        throw new RuntimeException("This class cannot be instantiated");
    }

}

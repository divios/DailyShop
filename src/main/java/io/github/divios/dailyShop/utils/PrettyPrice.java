package io.github.divios.dailyShop.utils;

import io.github.divios.dailyShop.files.Settings;
import org.apache.commons.lang.LocaleUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class PrettyPrice {

    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    private static final DecimalFormat df = new DecimalFormat("###,###.##", symbols); // or pattern "###,###.##$"
    
    /**
     * Formats double in a pretty default way (US locale).
     */
    public static String pretty(double d) {
        String priceStr = df.format(d);

        if (Settings.INTEGER_VAL.getValue().getAsBoolean())
            priceStr = roundPrice(priceStr);

        return priceStr;
    }

    private static String roundPrice(String price) {
        String aux = price;
        if (price.endsWith(",00") || price.endsWith(".00"))
            aux = price.substring(price.length() - 3);

        return aux;
    }

    private PrettyPrice() {
        throw new RuntimeException("This class cannot be instantiated");
    }

}

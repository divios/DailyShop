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

    private static Locale getLocaleOrDefault(String tag) {
        try {
            return Objects.requireNonNull(LocaleUtils.toLocale(tag), "locale is null");
        } catch (Exception e) {
            e.printStackTrace();
            return Locale.US;
        }
    }

    public static String pretty(double d) {
        String priceStr = formatOrDefault(Settings.PRICE_FORMAT.toString(), d);

        if (Settings.INTEGER_VAL.getValue().getAsBoolean())
            priceStr = roundPrice(priceStr);

        return priceStr;
    }


    private static String formatOrDefault(String format, double price) {
        try {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(getLocaleOrDefault(Settings.PRICE_LOCALE.toString()));
            DecimalFormat clientFormat = new DecimalFormat(format, symbols);
            return clientFormat.format(price);
        } catch (Exception e) {
            e.printStackTrace();
            return df.format(price);
        }
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

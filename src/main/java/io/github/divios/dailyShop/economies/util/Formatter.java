package io.github.divios.dailyShop.economies.util;

import io.github.divios.dailyShop.files.Settings;
import org.apache.commons.lang.LocaleUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class Formatter {

    private static final Formatter def =  new Formatter("###,###.##", "en");

    public static Formatter create(Settings.FormatterData data) {
        if (data == null)
            return def;

        return new Formatter(data.format, data.locale);
    }

    private final DecimalFormatSymbols symbols;
    private final DecimalFormat df;

    private Formatter(String format, String locale) {
        symbols = getSymbol(locale);
        df = getFormatter(format);
    }

    private DecimalFormatSymbols getSymbol(String localeStr) {
        try {
            Locale locale = Objects.requireNonNull(LocaleUtils.toLocale(localeStr));
            return new DecimalFormatSymbols(locale);
        } catch (Exception e) {
            return def.symbols;
        }
    }

    private DecimalFormat getFormatter(String format) {
        try {
            return new DecimalFormat(format, symbols);
        } catch (Exception e) {
            return def.df;
        }
    }

    public String format(Double d) {
        String priceStr = df.format(d);

        if (Settings.INTEGER_VAL.getValue().getAsBoolean())
            priceStr = roundPrice(priceStr);

        return priceStr;
    }

    private String roundPrice(String price) {
        String aux = price;
        if (price.endsWith(",00") || price.endsWith(".00"))
            aux = price.substring(price.length() - 3);

        return aux;
    }

}

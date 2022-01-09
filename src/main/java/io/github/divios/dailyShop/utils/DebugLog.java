package io.github.divios.dailyShop.utils;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.files.Settings;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class DebugLog {

    private static final String PREFIX = "&7Debug > ";

    private static boolean isDebugEnabled() {
        return Settings.DEBUG.getValue().getAsBoolean();
    }

    public static void info(@NotNull String s) {
        if (isDebugEnabled())
            Log.info(Utils.JTEXT_PARSER.parse(PREFIX + s));
    }

    public static void warn(@NotNull String s) {
        if (isDebugEnabled())
            Log.warn(Utils.JTEXT_PARSER.parse(PREFIX + s));
    }

    public static void severe(@NotNull String s) {
        if (isDebugEnabled())
            Log.severe(Utils.JTEXT_PARSER.parse(PREFIX + s));
    }

}

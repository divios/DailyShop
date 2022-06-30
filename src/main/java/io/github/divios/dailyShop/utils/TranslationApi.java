package io.github.divios.dailyShop.utils;

import me.rubix327.itemslangapi.ItemsLangAPI;
import me.rubix327.itemslangapi.Lang;

import java.util.ArrayList;
import java.util.List;

public class TranslationApi {

    static {
        if (isOperative()) {
            loadLangs();
        }
    }

    public static boolean isOperative() {
        return Utils.isOperative("ItemsLangApi");
    }

    private static void loadLangs() {
        ItemsLangAPI.getApi().hideWarnings();

        List<Lang> values = new ArrayList<>();
        for (Lang value : Lang.values()) {
            if (value.name().toUpperCase().startsWith("ES")
                    || value.name().toUpperCase().startsWith("EN")
                    || value.name().toUpperCase().startsWith("JA"))
                values.add(value);
        }

        Lang[] array = new Lang[values.size()];
        values.toArray(array);

        ItemsLangAPI.getApi().load(array);
    }

    public static String translate(Object object, String lang) {
        if (!isOperative()) return null;

        return ItemsLangAPI.getApi().translate(object, lang);
    }

}

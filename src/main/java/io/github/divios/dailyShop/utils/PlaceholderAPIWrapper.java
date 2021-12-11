package io.github.divios.dailyShop.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIWrapper {

    public static String setPlaceholders(Player p, String s) {
        if (p == null || !Utils.isOperative("PlaceholderAPI")) {
            return s;
        }

        try {
            return PlaceholderAPI.setPlaceholders(p, s);
        } catch (Exception e) {
            return s;
        }
    }

}

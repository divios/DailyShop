package io.github.divios.lib.dLib.shop.view.gui;

import com.google.gson.JsonElement;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.utils.Utils;
import org.bukkit.Bukkit;

public class GuiButtonFactory {

    public static ButtonGui createMultiGui(String title, int size) {
        ButtonGui gui = new ButtonGui(title, Bukkit.createInventory(null, size, Utils.JTEXT_PARSER.parse(title)));
        return new MultiButtonGui(gui);
    }

}

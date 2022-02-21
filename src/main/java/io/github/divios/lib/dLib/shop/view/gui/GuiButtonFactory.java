package io.github.divios.lib.dLib.shop.view.gui;

import com.google.gson.JsonElement;
import org.bukkit.Bukkit;

public class GuiButtonFactory {

    public static ButtonGui createMultiGui(String title, int size) {
        ButtonGui gui = new ButtonGui(title, Bukkit.createInventory(null, size, title));
        return new MultiButtonGui(gui);
    }

}

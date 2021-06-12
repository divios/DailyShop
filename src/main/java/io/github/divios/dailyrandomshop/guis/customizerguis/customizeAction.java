package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.core_lib.inventory.InventoryGUI;
import org.bukkit.entity.Player;

public class customizeAction {

    private final Player p;
    private final InventoryGUI inv;

    private customizeAction(Player p, InventoryGUI inv) {
        this.p = p;
        this.inv = inv;
    }


}

package io.github.divios.lib.dLib.shop.view.buttons;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface Button {

    void execute(InventoryClickEvent e);
    ItemStack getItem();

}

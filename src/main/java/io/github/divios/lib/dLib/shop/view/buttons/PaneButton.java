package io.github.divios.lib.dLib.shop.view.buttons;

import io.github.divios.lib.dLib.dItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PaneButton implements Button {

    private static final ItemStack AIR = new ItemStack(Material.AIR);

    private final dItem pane;

    public PaneButton(dItem pane) {
        this.pane = pane;
    }

    @Override
    public void execute(InventoryClickEvent e) {
        pane.getAction().execute((Player) e.getWhoClicked());
    }

    @Override
    public ItemStack getItem() {
        return pane.isAir()
                ? AIR
                : pane.getItem();
    }
}

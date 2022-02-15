package io.github.divios.lib.dLib.shop.buttons;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EmptyButton implements Button {

    private static final ItemStack AIR = new ItemStack(Material.AIR);

    @Override
    public void execute(InventoryClickEvent e) {
        // do nothing
    }

    @Override
    public ItemStack getItem() {
        return AIR;
    }
}

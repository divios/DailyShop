package io.github.divios.lib.dLib.shop.factory.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.factory.Precondition;
import io.github.divios.lib.dLib.shop.factory.exceptions.IllegalPrecondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class InventoryFullPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        Inventory cloneInv = Bukkit.createInventory(null, 36);
        cloneInv.setContents(Arrays.copyOf(p.getInventory().getContents(), 36));
        if (!cloneInv.addItem(item.getItem()).isEmpty())
            throw new IllegalPrecondition(Messages.MSG_INV_FULL);
    }
}

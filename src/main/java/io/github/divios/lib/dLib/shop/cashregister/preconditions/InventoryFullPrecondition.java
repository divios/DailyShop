package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class InventoryFullPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        Inventory cloneInv = Bukkit.createInventory(null, 36);
        cloneInv.setContents(Arrays.copyOf(p.getInventory().getContents(), 36));
        if (!cloneInv.addItem(ItemUtils.setAmount(item.getItem(), quantity)).isEmpty())
            throw new IllegalPrecondition(Messages.MSG_INV_FULL);
    }
}

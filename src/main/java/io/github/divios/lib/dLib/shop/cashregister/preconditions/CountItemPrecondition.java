package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public class CountItemPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        if (ItemUtils.count(p.getInventory(), item.getItem()) < quantity)
            throw new IllegalPrecondition(Messages.MSG_NOT_ITEMS);
    }
}

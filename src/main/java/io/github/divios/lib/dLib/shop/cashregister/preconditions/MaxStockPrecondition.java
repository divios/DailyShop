package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public class MaxStockPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        if (item.hasStock() && (!item.getDStock().allowSellOnMax()
                && (item.getPlayerStock(p) + quantity) >= item.getDStock().getMaximum())) {
            throw new IllegalPrecondition(Messages.MSG_FULL_STOCK);
        }
    }
}

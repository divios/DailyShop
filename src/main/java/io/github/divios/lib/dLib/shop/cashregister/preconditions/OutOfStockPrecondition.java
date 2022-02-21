package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import org.bukkit.entity.Player;

public class OutOfStockPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        if (item.hasStock() && item.getPlayerStock(p) <= 0)
            throw new IllegalPrecondition(Messages.MSG_OUT_STOCK);
    }

}

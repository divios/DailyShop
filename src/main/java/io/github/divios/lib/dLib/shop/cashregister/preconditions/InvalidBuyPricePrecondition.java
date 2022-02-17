package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import org.bukkit.entity.Player;

public class InvalidBuyPricePrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        if (item.getBuyPrice() < 0)
            throw new IllegalPrecondition(Messages.MSG_INVALID_BUY);
    }
}

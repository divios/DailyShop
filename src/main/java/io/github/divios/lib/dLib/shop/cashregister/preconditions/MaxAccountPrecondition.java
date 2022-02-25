package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public class MaxAccountPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        double price = item.getPlayerFloorBuyPrice(p, shop) * quantity;

        if (shop.getAccount() != null
                && Double.compare(shop.getAccount().getBalance() + price, shop.getAccount().getMaxBalance()) > 0) {
            throw new IllegalPrecondition(Messages.MSG_BALANCE_MAX_LIMIT);
        }
    }
}

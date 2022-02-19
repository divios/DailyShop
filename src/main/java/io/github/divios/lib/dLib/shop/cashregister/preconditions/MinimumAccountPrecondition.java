package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public class MinimumAccountPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        double price = item.getPlayerFloorSellPrice(p, shop) * item.getItem().getAmount();
        if (shop.getAccount() != null
                && Double.compare(shop.getAccount().getBalance() - price, 0) <= 0) {
            throw new IllegalPrecondition(Messages.MSG_BALANCE_MIN_LIMIT);
        }
    }
}

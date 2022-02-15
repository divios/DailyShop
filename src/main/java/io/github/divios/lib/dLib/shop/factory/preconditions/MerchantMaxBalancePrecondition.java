package io.github.divios.lib.dLib.shop.factory.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.factory.Precondition;
import io.github.divios.lib.dLib.shop.factory.exceptions.IllegalPrecondition;
import org.bukkit.entity.Player;

public class MerchantMaxBalancePrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        if (shop.getAccount() != null
                && Double.compare(shop.getAccount().getBalance() + item.getPlayerFloorBuyPrice(p, shop), shop.getAccount().getMaxBalance()) >= 0) {
            throw new IllegalPrecondition(Messages.MSG_BALANCE_MAX_LIMIT);
        }
    }
}

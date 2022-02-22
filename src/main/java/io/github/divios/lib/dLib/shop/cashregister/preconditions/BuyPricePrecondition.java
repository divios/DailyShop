package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import org.bukkit.entity.Player;

public class BuyPricePrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        double basePrice = item.getPlayerFloorBuyPrice(p, shop) * quantity;

        if (!item.getEcon().hasMoney(p, basePrice))
            throw new IllegalPrecondition(Messages.MSG_NOT_MONEY);
    }

}
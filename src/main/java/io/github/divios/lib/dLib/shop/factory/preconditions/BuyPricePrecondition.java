package io.github.divios.lib.dLib.shop.factory.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.factory.Precondition;
import io.github.divios.lib.dLib.shop.factory.exceptions.IllegalPrecondition;
import org.bukkit.entity.Player;

public class BuyPricePrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        double basePrice = item.getPlayerFloorBuyPrice(p, shop);
        if (!item.getEcon().hasMoney(p, basePrice))
            throw new IllegalPrecondition(Messages.MSG_NOT_MONEY);
    }

}

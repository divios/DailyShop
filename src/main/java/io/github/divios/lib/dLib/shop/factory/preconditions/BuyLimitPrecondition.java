package io.github.divios.lib.dLib.shop.factory.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.LimitHelper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.factory.Precondition;
import io.github.divios.lib.dLib.shop.factory.exceptions.IllegalPrecondition;
import org.bukkit.entity.Player;

public class BuyLimitPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        if (LimitHelper.getPlayerLimit(p, shop, item, Transactions.Type.BUY) == 0)
            throw new IllegalPrecondition(Messages.MSG_LIMIT);
    }
}

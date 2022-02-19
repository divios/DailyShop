package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.LimitHelper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public class SellLimitPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        if ((LimitHelper.getPlayerLimit(p, shop, item, Transactions.Type.SELL)) == 0)
            throw new IllegalPrecondition(Messages.MSG_LIMIT);
    }
}

package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public class EnoughStockCondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        int stock;
        if ((stock = item.getPlayerStock(p)) <= 0 || (stock < quantity))
            throw new IllegalPrecondition(Messages.MSG_NOT_STOCK);
    }
}

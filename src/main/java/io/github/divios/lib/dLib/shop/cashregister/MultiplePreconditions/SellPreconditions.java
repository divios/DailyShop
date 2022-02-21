package io.github.divios.lib.dLib.shop.cashregister.MultiplePreconditions;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.preconditions.*;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SellPreconditions implements Precondition {

    private static final List<Precondition> conditions;

    static {
        conditions = new ArrayList<>();

        conditions.add(new SellPermsPrecondition());
        conditions.add(new InvalidSellPricePrecondition());
        conditions.add(new CountItemPrecondition());
        conditions.add(new SellLimitPrecondition());
        conditions.add(new MinimumAccountPrecondition());
        conditions.add(new MaxStockPrecondition());
    }

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        conditions.forEach(precondition -> precondition.validate(shop, p, item, quantity));
    }
}

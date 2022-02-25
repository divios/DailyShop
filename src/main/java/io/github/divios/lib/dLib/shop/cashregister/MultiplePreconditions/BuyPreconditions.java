package io.github.divios.lib.dLib.shop.cashregister.MultiplePreconditions;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.preconditions.*;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BuyPreconditions implements Precondition {

    private static final List<Precondition> preconditions;

    static {
        preconditions = new ArrayList<>();

        preconditions.add(new BuyPermsPrecondition());
        preconditions.add(new OutOfStockPrecondition());
        preconditions.add(new InvalidBuyPricePrecondition());
        preconditions.add(new InventoryFullPrecondition());
        preconditions.add(new BuyPricePrecondition());
        preconditions.add(new BuyLimitPrecondition());
        preconditions.add(new MaxAccountPrecondition());
    }

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        preconditions.forEach(precondition -> precondition.validate(shop, p, item, quantity));
    }
}

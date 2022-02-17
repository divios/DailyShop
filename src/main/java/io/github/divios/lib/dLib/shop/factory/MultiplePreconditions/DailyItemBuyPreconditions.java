package io.github.divios.lib.dLib.shop.factory.MultiplePreconditions;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.factory.Precondition;
import io.github.divios.lib.dLib.shop.factory.preconditions.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DailyItemBuyPreconditions implements Precondition {

    private static final List<Precondition> preconditions;

    static {
        preconditions = new ArrayList<>();

        preconditions.add(new BuyPermsPrecondition());
        preconditions.add(new OutOfStockPrecondition());
        preconditions.add(new InvalidBuyPricePrecondition());
        preconditions.add(new InventoryFullPrecondition());
        preconditions.add(new BuyPricePrecondition());
        preconditions.add(new BuyLimitPrecondition());
        preconditions.add(new MerchantMaxBalancePrecondition());
    }

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        preconditions.forEach(precondition ->
                precondition.validate(shop, p, item, quantity)
        );
    }
}

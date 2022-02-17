package io.github.divios.lib.dLib.shop.cashregister.carts;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public class SellCart extends Cart {

    public SellCart(dShop shop, Player p, dItem item) {
        super(shop, p, item);
    }

    @Override
    public void addToCart() {

    }

    @Override
    public void confirmOperation() {
    }

    @Override
    public void checkOut(int amount) {

    }
}

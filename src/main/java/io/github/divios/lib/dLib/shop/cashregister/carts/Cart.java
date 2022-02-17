package io.github.divios.lib.dLib.shop.cashregister.carts;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public abstract class Cart {

    protected final dShop shop;
    protected final Player p;
    protected final dItem item;

    public Cart(dShop shop, Player p, dItem item) {
        this.shop = shop;
        this.p = p;
        this.item = item;

        addToCart();
        confirmOperation();
    }

    public abstract void addToCart();

    public abstract void confirmOperation();

    public abstract void checkOut(int amount);

}

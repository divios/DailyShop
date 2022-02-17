package io.github.divios.lib.dLib.shop.cashregister;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.dailyItemClickEvent;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.carts.BuyCart;
import io.github.divios.lib.dLib.shop.cashregister.carts.Cart;
import io.github.divios.lib.dLib.shop.cashregister.carts.SellCart;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

public class CashRegister implements Listener {

    private final dShop shop;

    public CashRegister(dShop shop) {
        this.shop = shop;

        Bukkit.getPluginManager().registerEvents(this, DailyShop.get());
    }

    public void initialize(Player p, dItem item, ClickType clickType) {
        Cart cart;
        try {
            cart = (clickType == ClickType.LEFT)
                    ? new BuyCart(shop, p, item)
                    : new SellCart(shop, p, item);
        } catch (IllegalPrecondition err) {
            err.sendErrMsg(p);
            p.closeInventory();
        }
    }

    public void destroy() {
        dailyItemClickEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    private void onItemClick(dailyItemClickEvent e) {
        if (Objects.equals(shop, e.getShop()))
            initialize(e.getPlayer(), e.getItem(), e.getClickType());
    }

}

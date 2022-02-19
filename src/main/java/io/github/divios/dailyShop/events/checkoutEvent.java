package io.github.divios.dailyShop.events;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class checkoutEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    private final dShop shop;
    private final Transactions.Type type;
    private final Player player;
    private final dItem item;
    private final int amount;
    private final double price;

    public checkoutEvent(dShop shop,
                         Transactions.Type type,
                         Player player,
                         dItem item,
                         int amount,
                         double price
    ) {
        this.shop = shop;
        this.type = type;
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.price = price;
    }

    public dShop getShop() {
        return shop;
    }

    public Transactions.Type getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    public dItem getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}

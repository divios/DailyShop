package io.github.divios.dailyShop.events;

import io.github.divios.lib.dLib.dShop;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;


/**
 * Event that fires when a player manually
 * reStock a shop or when the timer expires
 */

public class reStockShopEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCanceled = false;

    private final dShop reStockedShop;
    private final Timestamp timestamp;

    public reStockShopEvent(dShop reStockedShop) {
        this.reStockedShop = reStockedShop;
        this.timestamp = new Timestamp(System.currentTimeMillis());

    }

    public dShop getShop() { return this.reStockedShop; }

    public Timestamp getTimestamp() { return this.timestamp; }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}

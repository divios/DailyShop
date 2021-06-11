package io.github.divios.dailyrandomshop.events;

import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;


/**
 * Event that fires when a player manually
 * reStock a shop
 */

public class reStockShop extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCanceled = false;

    private final dShop reStockedShop;
    private final Timestamp timestamp;

    public reStockShop(dShop reStockedShop) {
        this.reStockedShop = reStockedShop;
        this.timestamp = new Timestamp(System.currentTimeMillis());

    }

    public dShop getShop() { return this.reStockedShop; }

    public Timestamp getTimestamp() { return this.timestamp; }

    @Override
    public boolean isCancelled() {
        return isCanceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCanceled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}

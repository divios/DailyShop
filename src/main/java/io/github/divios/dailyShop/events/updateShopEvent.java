package io.github.divios.dailyShop.events;

import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

/**
 * Event fired when a player updates
 * the shop "display" from {@link io.github.divios.dailyShop.guis.customizerguis.customizeGui}
 */

public class updateShopEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final dShop shop;
    private final dInventory inv;
    private final boolean response;
    private final Timestamp timestamp;

    public updateShopEvent(dShop shop, dInventory newInv, boolean response) {
        this.shop = shop;
        inv = newInv;
        this.response = response;
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public dShop getShop() {
        return shop;
    }

    public dInventory getInv() {
        return inv;
    }

    public boolean isResponse() {
        return response;
    }

    public Timestamp getTimestamp() {
        return timestamp;
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

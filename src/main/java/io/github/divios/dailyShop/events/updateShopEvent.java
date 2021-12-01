package io.github.divios.dailyShop.events;

import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;
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
    private final dInventory newInv;
    private final boolean silent;
    private final Timestamp timestamp;

    public updateShopEvent(dShop shop, dInventory newInv, boolean silent) {
        this.shop = shop;
        this.newInv = newInv;
        this.silent = silent;
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public updateShopEvent(dShop shop, dInventory newInv) {
        this(shop, newInv, false);
    }

    public dShop getShop() {
        return shop;
    }

    public dInventory getNewInv() {
        return newInv;
    }

    public boolean isSilent() {
        return silent;
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

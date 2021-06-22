package io.github.divios.dailyShop.events;

import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public class updateItemEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCanceled = false;

    private final dItem item;

    private final updatetype type;
    private final dShop shop;
    private final Timestamp timestamp;

    public updateItemEvent(ItemStack item, updatetype type, dShop shop) {
        this(new dItem(item), type, shop);
    }

    public updateItemEvent(dItem item, updatetype type, dShop shop) {
        this.item = item;
        this.type = type;
        this.shop = shop;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public dItem getItem() { return this.item; }

    public updatetype getType() {
        return type;
    }

    public dShop getShop() { return this.shop; }

    public Timestamp getTimestamp() { return this.timestamp; }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }


    public enum updatetype {
        UPDATE_ITEM,
        NEXT_AMOUNT,
        DELETE_ITEM
    }

}

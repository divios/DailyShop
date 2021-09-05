package io.github.divios.dailyShop.events;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public class updateItemEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final dItem item;
    private final int amount;
    private final Player p;
    private updatetype type;
    private final dShop shop;
    private final Timestamp timestamp;

    public updateItemEvent(ItemStack item, updatetype type, dShop shop) {
        this(null, new dItem(item), 1, type, shop);
    }

    public updateItemEvent(dItem item, updatetype type, dShop shop) {
        this(null, item, 1, type, shop);
    }

    public updateItemEvent(Player p, ItemStack item, updatetype type, dShop shop) {
        this(p, new dItem(item), 1, type, shop);
    }

    public updateItemEvent(ItemStack item, int amount, updatetype type, dShop shop) {
        this(null, new dItem(item), amount, type, shop);
    }

    public updateItemEvent(dItem item, int amount, updatetype type, dShop shop) {
        this(null, item, amount, type, shop);
    }

    public updateItemEvent(Player p, ItemStack item, int amount, updatetype type, dShop shop) {
        this(p, new dItem(item), amount, type, shop);
    }

    public updateItemEvent(Player p, dItem item, int amount, updatetype type, dShop shop) {
        this.p = p;
        this.item = item.clone();
        this.amount = amount;
        this.type = type;
        this.shop = shop;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Player getPlayer() {
        return p;
    }

    public dItem getItem() { return this.item; }

    public int getAmount() {
        return amount;
    }

    public updatetype getType() {
        return type;
    }

    public dShop getShop() { return this.shop; }

    public Timestamp getTimestamp() { return this.timestamp; }

    public void setType(updatetype type) {
        this.type = type;
    }

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

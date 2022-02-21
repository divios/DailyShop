package io.github.divios.dailyShop.events;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

public class dailyItemClickEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    private final dShop shop;
    private final Player player;
    private final dItem item;
    private final ClickType clickType;

    public dailyItemClickEvent(dShop shop, Player player, dItem item, ClickType clickType) {
        this.shop = shop;
        this.player = player;
        this.item = item;
        this.clickType = clickType;
    }

    public dShop getShop() {
        return shop;
    }

    public Player getPlayer() {
        return player;
    }

    public dItem getItem() {
        return item;
    }

    public ClickType getClickType() {
        return clickType;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}

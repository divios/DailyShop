package io.github.divios.dailyShop.events;

import io.github.divios.lib.dLib.shop.view.ShopView;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class shopViewUpdateEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public final ShopView gui;
    public final Instant timestamp;

    public shopViewUpdateEvent(ShopView gui) {
        this.gui = gui;
        this.timestamp = Instant.now();
    }

    public shopViewUpdateEvent(ShopView gui, Instant timestamp) {
        this.gui = gui;
        this.timestamp = timestamp;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}

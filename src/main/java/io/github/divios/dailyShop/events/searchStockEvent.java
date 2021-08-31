package io.github.divios.dailyShop.events;

import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class searchStockEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Player p;
    private final dShop shop;
    private final UUID uuid;
    private final CompletableFuture<Integer> callBack;

    public searchStockEvent(Player p, dShop shop, UUID uuid, CompletableFuture<Integer> callBack) {
        this.p = p;
        this.shop = shop;
        this.uuid = uuid;
        this.callBack = callBack;
    }

    public Player getPlayer() {
        return p;
    }

    public dShop getShop() {
        return shop;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void respond(int value) { callBack.complete(value); }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}

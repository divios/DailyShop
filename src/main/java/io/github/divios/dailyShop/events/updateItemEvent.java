package io.github.divios.dailyShop.events;

import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
public class updateItemEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final UUID uuid;
    private final int amount;
    private final Player p;
    private final updateItemEvent.type type;
    private final dShop shop;
    private final Timestamp timestamp;

    public updateItemEvent(UUID uuid, updateItemEvent.type type, dShop shop) {
        this(null, uuid, 1, type, shop);
    }

    public updateItemEvent(Player p, UUID uuid, updateItemEvent.type type, dShop shop) {
        this(p, uuid, 1, type, shop);
    }

    public updateItemEvent(UUID uuid, int amount, updateItemEvent.type type, dShop shop) {
        this(null, uuid, amount, type, shop);
    }

    public updateItemEvent(Player p, UUID uuid, int amount, updateItemEvent.type type, dShop shop) {
        this.p = p;
        this.uuid = Objects.requireNonNull(uuid);
        this.amount = amount;
        this.type = Objects.requireNonNull(type);
        this.shop = Objects.requireNonNull(shop);
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Player getPlayer() {
        return p;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getAmount() {
        return amount;
    }

    public updateItemEvent.type getType() {
        return type;
    }

    public dShop getShop() {
        return this.shop;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }


    public enum type {
        UPDATE_ITEM,
        NEXT_AMOUNT,
        REPLENISH,
        DELETE_ITEM
    }

}

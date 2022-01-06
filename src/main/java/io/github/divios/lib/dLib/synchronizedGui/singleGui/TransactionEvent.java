package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import io.github.divios.lib.dLib.dTransaction.SingleTransaction;
import io.github.divios.lib.dLib.newDItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

class TransactionEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final dInventory caller;
    private final SingleTransaction.Type type;
    private final Player player;
    private final newDItem item;


    public TransactionEvent(@NotNull dInventory caller,
                            @NotNull SingleTransaction.Type type,
                            @NotNull Player player,
                            @NotNull newDItem item) {
        this.caller = caller;
        this.type = type;
        this.player = player;
        this.item = item;
    }

    public dInventory getCaller() {
        return caller;
    }

    public SingleTransaction.Type getType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    public newDItem getItem() {
        return item;
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

package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.synchronizedGui.taskPool.updatePool;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Class that holds a {@link dInventory} for a unique player and
 * also its base.
 *
 * Subscribes to the updatePool to update placeholders
 */

public class singleGuiImpl implements singleGui {

    private final Player p;
    private final dShop shop;
    private final dInventory base;
    private final dInventory own;

    protected singleGuiImpl(Player p, dShop shop, singleGui base) {
        this(p, shop, base.getInventory());
    }

    protected singleGuiImpl(Player p, dShop shop, dInventory base) {
        this.p = p;
        this.shop = shop;
        this.base = base;
        this.own = base.clone();

        if (p != null) {
            if (utils.isOperative("PlaceholderAPI")) updatePool.subscribe(this);
            this.own.open(p);
        }
    }

    @Override
    public void updateItem(dItem item, updateItemEvent.updatetype type) {
        own.updateItem(item, type);
    }

    @Override
    public void renovate() {
        own.renovate();
    }

    @Override
    public Player getPlayer() {
        return p;
    }

    @Override
    public dInventory getBase() {
        return base;
    }

    @Override
    public dInventory getInventory() {
        return own;
    }

    @Override
    public dShop getShop() {
        return shop;
    }

    @Override
    public void destroy() {
        own.destroy();
        updatePool.cancel(this);
    }

    @Override
    public int hashCode() {
        return Arrays.stream(own.getInventory().getContents())
                .mapToInt(value -> utils.isEmpty(value) ? 0 : value.hashCode())
                .sum();
    }

    @Override
    public singleGui clone() { return singleGui.fromJson(toJson(), getShop()); }

}

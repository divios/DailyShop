package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.synchronizedGui.taskPool.updatePool;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * Class that holds a {@link dInventory} for a unique player and
 * also its base.
 * <p>
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
            //if (utils.isOperative("PlaceholderAPI")) updatePool.subscribe(this);
            this.own.open(p);
        }
    }

    @Override
    public synchronized void updateItem(dItem item, updateItemEvent.updatetype type) {
        own.updateItem(item, type);
    }

    @Override
    public synchronized void updateTask() {

        loreStrategy strategy = new shopItemsLore();
        IntStream.range(0, own.getSize())
                .filter(value -> !ItemUtils.isEmpty(own.getInventory().getItem(value)))
                .forEach(value -> {

                    Inventory inv = own.getInventory();
                    ItemStack oldItem = base.getOpenSlots().contains(value) ?
                            strategy.applyLore(base.getButtons().get(value).getItem().clone())
                            : base.getInventory().getItem(value);
                    ItemBuilder newItem = ItemBuilder.of(oldItem.clone()).setLore(Collections.emptyList());

                    newItem = newItem.setName(PlaceholderAPI.setPlaceholders(p, ItemUtils.getName(oldItem)));

                    for (String s : ItemUtils.getLore(oldItem))
                        newItem = newItem.addLore(PlaceholderAPI.setPlaceholders(p, s));

                    inv.setItem(value, newItem);

                });
    }

    @Override
    public synchronized void renovate() {
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
    public synchronized void destroy() {
        own.destroy();
        //updatePool.cancel(this);
    }

    @Override
    public synchronized int hashCode() {
        return Arrays.stream(own.getInventory().getContents())
                .mapToInt(value -> utils.isEmpty(value) ? 0 : value.hashCode())
                .sum();
    }

    @Override
    public synchronized singleGui clone() {
        return singleGui.fromJson(toJson(), getShop());
    }

}

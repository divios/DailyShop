package io.github.divios.lib.dLib.synchronizedGui.singleGui;

import io.github.divios.core_lib.Schedulers;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

public class singleGuiImpl implements singleGui {

    private final Player p;
    private final dShop shop;
    private final dInventory base;
    private Task updateTask = null;

    protected singleGuiImpl(Player p, dShop shop, singleGui base) {
        this(p, shop, base.getInventory());
    }

    protected singleGuiImpl(Player p, dShop shop, dInventory base) {
        this.p = p;
        this.shop = shop;
        this.base = base.clone();

        if (p != null) {
            if (utils.isOperative("PlaceholderAPI")) ready();
            this.base.open(p);
        }
    }

    private void ready() {

        Inventory aux = inventoryUtils.cloneInventory(base.getInventory(), base.getTitle());
        updateTask =
                Schedulers.builder()
                        .sync()
                        .every(20)
                        .run(() ->
                                IntStream.range(0, base.getInventory().getSize())
                                        .filter(value -> !base.getOpenSlots().contains(value))
                                        .filter(value -> !ItemUtils.isEmpty(base.getInventory().getItem(value)))
                                        .forEach(value -> {

                                            Inventory inv = base.getInventory();
                                            ItemStack oldItem = aux.getItem(value);
                                            ItemBuilder newItem = ItemBuilder.of(oldItem.clone()).setLore(Collections.emptyList());

                                            newItem = newItem.setName(PlaceholderAPI.setPlaceholders(p, ItemUtils.getName(oldItem)));

                                            for (String s : ItemUtils.getLore(oldItem))
                                                newItem = newItem.addLore(PlaceholderAPI.setPlaceholders(p, s));

                                            inv.setItem(value, newItem);

                                        })
                        );

    }


    @Override
    public void updateItem(dItem item, updateItemEvent.updatetype type) {

    }

    @Override
    public void renovate() {
        base.renovate();
    }

    @Override
    public Player getPlayer() {
        return p;
    }

    @Override
    public dInventory getInventory() {
        return base;
    }

    @Override
    public dShop getShop() {
        return shop;
    }

    @Override
    public void destroy() {
        base.destroy();
        if (updateTask != null) updateTask.stop();
    }

    @Override
    public int hashCode() {
        return Arrays.stream(base.getInventory().getContents())
                .mapToInt(value -> utils.isEmpty(value) ? 0 : value.hashCode())
                .sum();
    }

    @Override
    public singleGui clone() { return singleGui.fromJson(toJson(), getShop()); }

}

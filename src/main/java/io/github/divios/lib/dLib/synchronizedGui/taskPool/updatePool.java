package io.github.divios.lib.dLib.synchronizedGui.taskPool;

import io.github.divios.core_lib.Schedulers;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class updatePool {

    private static final Set<singleGui> bucket = new HashSet<>();

    static {
        Schedulers.sync().runRepeating(() -> {
            bucket.stream().parallel().forEach(gui -> {
                dInventory base = gui.getInventory();
                IntStream.range(0, base.getSize())
                        .filter(value -> !base.getOpenSlots().contains(value))
                        .filter(value -> !ItemUtils.isEmpty(base.getInventory().getItem(value)))
                        .forEach(value -> {

                            Inventory inv = base.getInventory();
                            ItemStack oldItem = gui.getBase().getInventory().getItem(value);
                            ItemBuilder newItem = ItemBuilder.of(oldItem.clone()).setLore(Collections.emptyList());

                            newItem = newItem.setName(PlaceholderAPI.setPlaceholders(gui.getPlayer(), ItemUtils.getName(oldItem)));

                            for (String s : ItemUtils.getLore(oldItem))
                                newItem = newItem.addLore(PlaceholderAPI.setPlaceholders(gui.getPlayer(), s));

                            inv.setItem(value, newItem);

                        });
            });
        }, 20L, 20L);
    }


    public static void subscribe(singleGui gui) {
        bucket.add(gui);
    }

    public static void cancel(singleGui gui) {
        bucket.remove(gui);
    }

}

package io.github.divios.dailyShop.tasks;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public class updatePlaceholdersTask {

    public static final DRShop plugin = DRShop.getInstance();
    public static final shopsManager SManager = shopsManager.getInstance();

    private static boolean load = false;
    private static Task task;

    public static void load() {
        if (load) return;

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return;

        load = true;

        task = Task.asyncRepeating(plugin, () -> {

            SManager.getShops().forEach(dShop -> {

                Inventory inv = dShop.getGui().getInventory();

                IntStream.range(0, inv.getSize()).forEach(i -> {

                    ItemStack toReplace = inv.getItem(i);
                    ItemStack replace;
                    if (ItemUtils.isEmpty(toReplace)) return;

                    /*replace = new ItemBuilder(toReplace.clone())
                            .setName() */

                });

            });


        }, 20L, 20L);
    }

    public static void unload() {
        if (!load) return;

        load = false;
        task.cancel();
    }

}

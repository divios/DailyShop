package io.github.divios.lib.dLib.synchronizedGui.taskPool;

import io.github.divios.core_lib.Schedulers;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

public class updatePool {

    private static final Set<singleGui> bucket = new HashSet<>();

    static {
        Schedulers.async().runRepeating(() -> bucket.stream().parallel().forEach(singleGui::updateTask),
                10L, 10L);
    }

    public static void subscribe(singleGui gui) {
        bucket.add(gui);
    }

    public static void cancel(singleGui gui) {
        bucket.remove(gui);
    }


}

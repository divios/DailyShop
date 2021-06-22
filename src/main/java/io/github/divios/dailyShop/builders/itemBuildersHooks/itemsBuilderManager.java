package io.github.divios.dailyShop.builders.itemBuildersHooks;

import io.github.divios.dailyShop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class itemsBuilderManager {

    static List<itemsBuilder> builders = new ArrayList<>(Arrays.asList(
            new MMOItems(),
            new oraxenItems()       /* Just add here new implementations */
        ));


    private itemsBuilderManager() {}

    public static boolean updateItem(String uuid) {
        if (utils.isEmpty(uuid)) return false;
        //buyGui.getInstance().updateItem(uuid,   /* Internally async */
               //buyGui.updateAction.update);

        //return updateItem(dailyItem.getRawItem(uuid));
        return true;
    }

    public static boolean updateItem(ItemStack item) {
        AtomicReference<itemsBuilder> builder = new AtomicReference<>(null);
        builders.forEach(i -> {
            if (i.isItem(item))
                builder.set(i);
        });

        if (builder.get() == null) return false;

        return builder.get().updateItem(item);
    }

    public static boolean isUpdateItem(ItemStack item) {
        AtomicReference<Boolean> response = new AtomicReference<>(false);
        builders.forEach(i -> {
            if (i.isItem(item))
                response.set(true);
        });

        return response.get();
    }

    public static ItemStack getItem(ItemStack item) {
        AtomicReference<itemsBuilder> builder = new AtomicReference<>(null);
        builders.forEach(i -> {
            if (i.isItem(item))
                builder.set(i);
        });

        if (builder.get() == null) return null;

        return builder.get().getItem(item);
    }

}

package io.github.divios.lib.serialize.wrappers;

import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WrappedItemFlags {

    private final ItemStack item;

    public static WrappedItemFlags of(ItemStack item) {
        return new WrappedItemFlags(item);
    }

    public WrappedItemFlags(ItemStack item) {
        this.item = item;
    }

    public List<String> getFlags() {
        return Arrays.stream(ItemFlag.values())
                .filter(itemFlag -> ItemUtils.hasItemFlags(item, itemFlag))
                .map(ItemFlag::name)
                .collect(Collectors.toList());
    }

}

package io.github.divios.lib.serialize.wrappers;

import io.github.divios.lib.dLib.dItem;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WrappedItemFlags {

    private final dItem item;

    public static WrappedItemFlags of(dItem item) {
        return new WrappedItemFlags(item);
    }

    public WrappedItemFlags(dItem item) {
        this.item = item;
    }

    public List<String> getFlags() {
        return Arrays.stream(ItemFlag.values())
                .filter(item::hasFlag)
                .map(ItemFlag::name)
                .collect(Collectors.toList());
    }

}

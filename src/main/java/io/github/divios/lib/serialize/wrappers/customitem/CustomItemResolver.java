package io.github.divios.lib.serialize.wrappers.customitem;

import com.google.gson.JsonElement;
import org.bukkit.inventory.ItemStack;

public interface CustomItemResolver {

    JsonElement toJson(ItemStack item);
    ItemStack fromJson(JsonElement json);
    boolean matches(ItemStack item);
    boolean matches(JsonElement json);

}

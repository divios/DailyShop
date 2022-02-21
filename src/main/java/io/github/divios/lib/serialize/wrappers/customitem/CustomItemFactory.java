package io.github.divios.lib.serialize.wrappers.customitem;

import com.google.gson.JsonElement;
import io.github.divios.lib.serialize.wrappers.customitem.resolvers.ItemsAdderResolver;
import io.github.divios.lib.serialize.wrappers.customitem.resolvers.MMOItemResolver;
import io.github.divios.lib.serialize.wrappers.customitem.resolvers.OraxenResolver;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CustomItemFactory {

    private static final Set<CustomItemResolver> resolvers = new HashSet<>();

    static {
        resolvers.add(new MMOItemResolver());
        resolvers.add(new OraxenResolver());
        resolvers.add(new ItemsAdderResolver());
    }

    public static ItemStack fromJson(JsonElement json) {
        ItemStack newItem = null;
        for (CustomItemResolver resolver : resolvers) {
            if (resolver.matches(json)) {
                newItem = resolver.fromJson(json);
                break;
            }
        }

        return Objects.requireNonNull(newItem, "Couldn't deserialize item, is the plugin on?");
    }

    public static JsonElement toJson(ItemStack item) {
        JsonElement json = null;
        for (CustomItemResolver resolver : resolvers) {
            if (resolver.matches(item)) {
                json = resolver.toJson(item);
                break;
            }
        }

        return Objects.requireNonNull(json, "Couldn't find a strategy to serialize custom item");
    }

    public static boolean isCustomItem(ItemStack item) {
        return resolvers.stream().anyMatch(resolver -> resolver.matches(item));
    }

}

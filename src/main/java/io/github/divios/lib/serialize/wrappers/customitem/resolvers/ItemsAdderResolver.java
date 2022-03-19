package io.github.divios.lib.serialize.wrappers.customitem.resolvers;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lone.itemsadder.api.CustomStack;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.serialize.wrappers.customitem.CustomItemResolver;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class ItemsAdderResolver implements CustomItemResolver {

    @Override
    public JsonElement toJson(ItemStack item) {
        JsonObject json = new JsonObject();
        JsonObject innerJson = new JsonObject();

        CustomStack customStack = CustomStack.byItemStack(item);

        innerJson.addProperty("namespace", customStack.getNamespace());
        innerJson.addProperty("id", customStack.getId());

        json.add("itemsAdder", innerJson);

        return json;
    }

    @Override
    public ItemStack fromJson(JsonElement json) {
        if (!Utils.isOperative("ItemsAdder")) return null;
        JsonObject itemsAdderItem = json.getAsJsonObject().get("itemsAdder").getAsJsonObject();

        Preconditions.checkArgument(itemsAdderItem.has("id"), "ItemsAdder needs an id field");
        Preconditions.checkArgument(itemsAdderItem.has("namespace"), "ItemsAdder needs a type field");

        String namespace = itemsAdderItem.get("namespace").getAsString();
        String id = itemsAdderItem.get("id").getAsString();

        CustomStack item = CustomStack.getInstance(namespace + ":" + id);

        return Objects.requireNonNull(item == null ? null : item.getItemStack()
                , "Could not find the ItemsAdder item with the given namespace/id: " + id);
    }

    @Override
    public boolean matches(ItemStack item) {
        if (Utils.isOperative("ItemsAdder")) {
            return CustomStack.byItemStack(item) != null;
        }
        return false;
    }

    @Override
    public boolean matches(JsonElement json) {
        return json.getAsJsonObject().has("itemsAdder");
    }
}

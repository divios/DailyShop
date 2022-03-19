package io.github.divios.lib.serialize.wrappers.customitem.resolvers;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.dailyShop.utils.OraxenUtils;
import io.github.divios.lib.serialize.wrappers.customitem.CustomItemResolver;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class OraxenResolver implements CustomItemResolver {

    @Override
    public JsonElement toJson(ItemStack item) {
        JsonObject json = new JsonObject();
        JsonObject innerJson = new JsonObject();

        innerJson.addProperty("id", OraxenUtils.getId(item));

        json.add("oraxenItem", innerJson);

        return json;
    }

    @Override
    public ItemStack fromJson(JsonElement json) {
        JsonObject oraxenObject = json.getAsJsonObject().get("oraxenItem").getAsJsonObject();
        String id;

        Preconditions.checkArgument(oraxenObject.has("id"), "Oraxen item needs an id");
        Preconditions.checkArgument(OraxenUtils.isValidId(id = oraxenObject.get("id").getAsString()), "That oraxen ID does not exist");

        ItemStack oraxenItem = OraxenUtils.createItemByID(id);
        return Objects.requireNonNull(oraxenItem, "Could not find an oraxen item with the given id: " + id);
    }

    @Override
    public boolean matches(ItemStack item) {
        return OraxenUtils.isOraxenItem(item);
    }

    @Override
    public boolean matches(JsonElement json) {
        return json.getAsJsonObject().has("oraxenItem");
    }
}

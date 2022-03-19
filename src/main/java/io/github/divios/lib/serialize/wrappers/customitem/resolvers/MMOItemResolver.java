package io.github.divios.lib.serialize.wrappers.customitem.resolvers;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.dailyShop.utils.MMOUtils;
import io.github.divios.lib.serialize.wrappers.customitem.CustomItemResolver;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class MMOItemResolver implements CustomItemResolver {

    @Override
    public JsonElement toJson(ItemStack item) {
        JsonObject json = new JsonObject();
        JsonObject innerJson = new JsonObject();

        innerJson.addProperty("type", MMOUtils.getType(item));
        innerJson.addProperty("id", MMOUtils.getId(item));

        json.add("mmoItem", innerJson);

        return json;
    }

    @Override
    public ItemStack fromJson(JsonElement json) {
        JsonObject mmoItemObject = json.getAsJsonObject().get("mmoItem").getAsJsonObject();

        Preconditions.checkArgument(mmoItemObject.has("type"), "MMOItem needs a type field");
        Preconditions.checkArgument(mmoItemObject.has("id"), "MMOItem needs an id field");

        ItemStack mmoItem = MMOUtils.createMMOItem(mmoItemObject.get("type").getAsString(), mmoItemObject.get("id").getAsString());
        return Objects.requireNonNull(mmoItem, "Could not find the mmoitem with the given type/id: " + mmoItemObject.get("id").getAsString());
    }

    @Override
    public boolean matches(ItemStack item) {
        return MMOUtils.isMMOItem(item);
    }

    @Override
    public boolean matches(JsonElement json) {
        return json.getAsJsonObject().has("mmoItem");
    }
}

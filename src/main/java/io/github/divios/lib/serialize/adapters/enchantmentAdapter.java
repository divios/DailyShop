package io.github.divios.lib.serialize.adapters;

import com.cryptomorin.xseries.XEnchantment;
import com.google.common.base.Preconditions;
import com.google.gson.*;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.serialize.wrappers.WrappedEnchantment;

import java.lang.reflect.Type;

public class enchantmentAdapter implements JsonSerializer<WrappedEnchantment>, JsonDeserializer<WrappedEnchantment> {

    @Override
    public WrappedEnchantment deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        String[] enchant = {null};
        int level[] = {-1};

        Preconditions.checkArgument(object.has("enchant"), "An enchantment needs an enchant field");
        Preconditions.checkArgument(Utils.testRunnable(() -> XEnchantment.valueOf(enchant[0] = object.get("enchant").getAsString())));
        Preconditions.checkArgument(Utils.testRunnable(() -> level[0] = object.get("level").getAsInt()), "Level field of an enchantment needs to be an integer");

        return WrappedEnchantment.of(XEnchantment.valueOf(enchant[0]).parseEnchantment(), level[0]);
    }

    @Override
    public JsonElement serialize(WrappedEnchantment WrappedEnchantment, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject object = new JsonObject();
        object.addProperty("enchant", WrappedEnchantment.getEnchant().getName());
        object.addProperty("level", WrappedEnchantment.getLevel());

        return object;
    }
}

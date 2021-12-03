package io.github.divios.lib.serialize.adapters;

import com.google.gson.*;
import io.github.divios.lib.serialize.wrappers.WrappedEnchantment;

import java.lang.reflect.Type;

public class enchantmentAdapter implements JsonSerializer<WrappedEnchantment>, JsonDeserializer<WrappedEnchantment> {

    @Override
    public WrappedEnchantment deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(WrappedEnchantment WrappedEnchantment, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject object = new JsonObject();
        object.addProperty("enchant", WrappedEnchantment.getEnchant().getName());
        object.addProperty("level", WrappedEnchantment.getLevel());

        return object;
    }
}

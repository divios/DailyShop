package io.github.divios.lib.serialize.adapters;

import com.google.gson.*;
import io.github.divios.dailyShop.economies.economy;

import java.lang.reflect.Type;

public class economyAdapter implements JsonSerializer<economy>, JsonDeserializer<economy> {

    @Override
    public economy deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(economy economy, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("name", economy.getKey());
        if (!economy.getCurrency().isEmpty()) object.addProperty("currency", economy.getCurrency());

        return object;
    }
}

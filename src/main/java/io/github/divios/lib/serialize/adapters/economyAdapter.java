package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import io.github.divios.dailyShop.economies.economy;

import java.lang.reflect.Type;

public class economyAdapter implements JsonSerializer<economy>, JsonDeserializer<economy> {

    @Override
    public economy deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Preconditions.checkArgument(object.has("name"), "econ needs a name");

        return economy.getFromKey(object.get("name").getAsString(), object.has("currency") ? object.get("currency").getAsString() : "");
    }

    @Override
    public JsonElement serialize(economy economy, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("name", economy.getKey());
        if (!economy.getCurrency().isEmpty()) object.addProperty("currency", economy.getCurrency());

        return object;
    }
}

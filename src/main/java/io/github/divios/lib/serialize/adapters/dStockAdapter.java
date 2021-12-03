package io.github.divios.lib.serialize.adapters;

import com.google.gson.*;
import io.github.divios.lib.dLib.stock.dStock;

import java.lang.reflect.Type;

public class dStockAdapter implements JsonSerializer<dStock>, JsonDeserializer<dStock> {

    @Override
    public JsonElement serialize(dStock dStock, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("type", dStock.getName());
        object.addProperty("amount", dStock.getDefault());

        return object;
    }

    @Override
    public dStock deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;
    }
}

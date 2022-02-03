package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import io.github.divios.lib.dLib.dPrice;

import java.lang.reflect.Type;

public class dPriceAdapter implements JsonSerializer<dPrice>, JsonDeserializer<dPrice> {

    @Override
    public dPrice deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Preconditions.checkArgument(object.has("fixed")
                || (object.has("min") && object.has("max")), "Invalid price fields");

        if (object.has("fixed"))
            return object.get("fixed").getAsDouble() >= 0 ? new dPrice(object.get("fixed").getAsDouble()) : null;
        else
            return new dPrice(object.get("min").getAsDouble(), object.get("max").getAsDouble());

    }

    @Override
    public JsonElement serialize(dPrice dPrice, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();

        String[] priceStr;
        if ((priceStr = dPrice.toString().split(":")).length == 1) {
            object.addProperty("fixed", priceStr[0]);
        } else {
            object.addProperty("min", priceStr[0].trim());
            object.addProperty("max", priceStr[1].trim());
        }

        return object;
    }
}

package io.github.divios.lib.serialize.adapters;

import com.google.gson.*;
import io.github.divios.dailyShop.utils.valuegenerators.ValueGenerator;
import io.github.divios.lib.dLib.dPrice;

import java.lang.reflect.Type;

public class dPriceAdapter implements JsonSerializer<dPrice>, JsonDeserializer<dPrice> {

    @Override
    public dPrice deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new dPrice(ValueGenerator.fromJson(jsonElement));
    }

    @Override
    public JsonElement serialize(dPrice dPrice, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonElement json = dPrice.getGenerator().toJson();

        if (json.getAsJsonObject().has("fixed")
                && json.getAsJsonObject().get("fixed").getAsDouble() <= -1)
            return null;

        return json;
    }

}

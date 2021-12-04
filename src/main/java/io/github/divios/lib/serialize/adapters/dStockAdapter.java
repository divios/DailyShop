package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;

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
        JsonObject object = jsonElement.getAsJsonObject();

        String typeStr;
        int[] amount = {0};

        Preconditions.checkArgument(object.has("type"), "A stock needs a type field");
        Preconditions.checkArgument(object.has("amount"), "A stock needs an amount field");
        Preconditions.checkArgument(Utils.testRunnable(() -> amount[0] = object.get("amount").getAsInt()), "Amount field of stock needs to be an integer");

        typeStr = object.get("type").getAsString().toUpperCase();

        Preconditions.checkArgument((typeStr.equals("INDIVIDUAL") || typeStr.equals("GLOBAL")), "Invalid type field on stock");

        return type.equals("INDIVIDUAL") ? dStockFactory.INDIVIDUAL(amount[0]) : dStockFactory.GLOBAL(amount[0]);
    }
}

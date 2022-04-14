package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.apache.commons.lang.Validate;

import java.lang.reflect.Type;

public class dStockAdapter implements JsonSerializer<dStock>, JsonDeserializer<dStock> {

    @Override
    public JsonElement serialize(dStock stock, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("type", stock.getName());
        object.addProperty("amount", stock.getDefault());

        if (stock.getMaximum() != stock.getDefault())
            object.addProperty("max", stock.getMaximum());

        if (stock.exceedsDefault())
            object.addProperty("exceedDefault", true);

        if (stock.incrementsOnSell())
            object.addProperty("incrementOnSell", true);

        object.addProperty("allowSellOnMax", stock.allowSellOnMax());

        return object;
    }

    @Override
    public dStock deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        String typeStr;
        int[] amount = {0};
        int max;

        Preconditions.checkArgument(object.has("type"), "A stock needs a type field");
        Preconditions.checkArgument(object.has("amount"), "A stock needs an amount field");
        Preconditions.checkArgument(Utils.testRunnable(() -> amount[0] = object.get("amount").getAsInt()), "Amount field of stock needs to be an integer");

        typeStr = object.get("type").getAsString().toUpperCase();

        Preconditions.checkArgument((typeStr.equals("INDIVIDUAL") || typeStr.equals("GLOBAL")), "Invalid type field on stock");

        max = object.has("max") ? object.get("max").getAsInt() : amount[0];
        boolean incrementOnSell = object.has("incrementOnSell") && object.get("incrementOnSell").getAsBoolean();
        boolean exceedDefault = object.has("exceedDefault") && object.get("exceedDefault").getAsBoolean();
        boolean allowSellOnMax = !object.has("allowSellOnMax") || object.get("allowSellOnMax").getAsBoolean();

        Validate.isTrue(amount[0] <= max, "max cannot be less than default amount");

        dStock stock = typeStr.equals("INDIVIDUAL") ? dStockFactory.INDIVIDUAL(amount[0], max) : dStockFactory.GLOBAL(amount[0], max);
        stock.setIncrementOnSell(incrementOnSell);
        stock.setExceedDefault(exceedDefault);
        stock.setAllowSellOnMax(allowSellOnMax);

        return stock;
    }
}

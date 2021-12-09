package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class dShopAdapter implements JsonSerializer<dShop>, JsonDeserializer<dShop> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(dItem.class, new dItemAdapter())
            .registerTypeAdapter(dInventory.class, new dInventoryAdapter())
            .create();

    private static final TypeToken<LinkedHashMap<String, JsonElement>> diItemsToken = new TypeToken<LinkedHashMap<String, JsonElement>>(){};

    @Override
    public dShop deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Preconditions.checkArgument(object.has("id"), "A shop needs an ID");
        Preconditions.checkArgument(object.has("items"), "A shop needs items");
        if (object.has("timer")) Preconditions.checkArgument(Utils.testRunnable(() -> object.get("timer").getAsInt()), "Timer needs to be an integer");

        dShop deserializedShop;

        String id = object.get("id").getAsString();
        int timer = object.has("timer") ? object.get("timer").getAsInt() : DailyShop.getInstance().configM.getSettingsYml().DEFAULT_TIMER;
        Timestamp timestamp = object.has("timestamp") ? new Timestamp(wrappedParse(object.get("timestamp").getAsString()).getTime()) : new Timestamp(System.currentTimeMillis());

        deserializedShop = new dShop(id, timer, timestamp);

        // Deserialize shop display
        if (object.has("shop")) {
            dInventory inv = gson.fromJson(object.get("shop"), dInventory.class);
            deserializedShop.updateShopGui(inv, true);
        }

        // Deserialize Items
        Map<String, JsonElement> items = gson.fromJson(object.get("items").getAsJsonObject(), diItemsToken.getType());
        for (Map.Entry<String, JsonElement> itemEntry : items.entrySet()) {
            try {
                dItem ditem = dItem.encodeOptions.JSON.fromJson(itemEntry.getValue());
                deserializedShop.addItem(ditem.setID(itemEntry.getKey()));
            } catch (Exception e) {
                Log.warn("There was a problem parsing the item with id " + itemEntry.getKey());
                Log.warn(e.getMessage());
            }
        }

        return deserializedShop;
    }

    @Override
    public JsonElement serialize(dShop shop, Type type, JsonSerializationContext jsonSerializationContext) {
        return JsonBuilder.object()
                .add("id", shop.getName())
                .add("timer", shop.getTimer())
                .add("timeStamp", dateFormat.format(shop.getTimestamp()))
                .add("shop", gson.toJsonTree(shop.getGuis().getDefault()))
                .add("items", gson.toJsonTree(parseUUIDs(shop.getItems())))
                .build();
    }

    /** Utils **/

    private Map<String, dItem> parseUUIDs(Set<dItem> items) {
        Map<String, dItem> newMap = new HashMap<>();
        items.forEach(dItem -> newMap.put(dItem.getID(), dItem));

        return newMap;
    }

    private Date wrappedParse(String s) {
        try {
            return dateFormat.parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}

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

        String id;
        int[] timer = {86400};
        Timestamp timestamp;

        Preconditions.checkArgument(object.has("id"), "A shop needs an ID");
        Preconditions.checkArgument(object.has("items"), "A shop needs items");
        if (object.has("timer")) Preconditions.checkArgument(Utils.testRunnable(() -> timer[0] = object.get("timer").getAsInt()), "Timer needs to be an integer");
        Preconditions.checkArgument((timer[0] >= 50 || timer[0] == -1), "timer needs to be >= 50");
        if (object.has("announce_restock")) Preconditions.checkArgument(Utils.testRunnable(() -> object.get("announce_restock").getAsBoolean()), "announce_restock field needs to be a boolean");
        if (object.has("default")) Preconditions.checkArgument(Utils.testRunnable(() -> object.get("default").getAsBoolean()), "default field needs to be a boolean");

        dShop deserializedShop;

        id = object.get("id").getAsString();
        timer[0] = object.has("timer") ? object.get("timer").getAsInt() : DailyShop.get().configM.getSettingsYml().DEFAULT_TIMER;
        timestamp = object.has("timestamp") ? new Timestamp(wrappedParse(object.get("timestamp").getAsString()).getTime()) : new Timestamp(System.currentTimeMillis());

        deserializedShop = new dShop(id, timer[0], timestamp);

        // Set miscellaneous fields
        if (object.has("announce_restock")) deserializedShop.set_announce(object.get("announce_restock").getAsBoolean());
        if (object.has("default")) deserializedShop.setDefault(object.get("default").getAsBoolean());

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

        JsonBuilder.JsonObjectBuilder builder = JsonBuilder.object()
                .add("id", shop.getName())
                .add("timer", shop.getTimer());

        if (!shop.get_announce()) builder.add("announce_restock", false);
        if (!shop.isDefault()) builder.add("default", true);

        return builder.add("timeStamp", (Boolean) null)  // dateFormat.format(shop.getTimestamp()
                .add("shop", gson.toJsonTree(shop.getGuis().getDefault()))
                .add("items", gson.toJsonTree(parseUUIDs(shop.getItems())))
                .build();

    }

    /** Utils **/

    private Map<String, dItem> parseUUIDs(Collection<dItem> items) {
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

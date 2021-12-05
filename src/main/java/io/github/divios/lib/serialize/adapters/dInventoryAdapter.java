package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class dInventoryAdapter implements JsonSerializer<dInventory>, JsonDeserializer<dInventory> {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(dItem.class, new dButtonAdapter())
            .create();

    private static final TypeToken<LinkedHashMap<String, dItem>> itemsToken = new TypeToken<LinkedHashMap<String, dItem>>() {};

    @Override
    public dInventory deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        String title = object.has("title") ? object.get("title").getAsString() : "";
        int size[] = {27};
        Map<String, dItem> buttons = new LinkedHashMap<>();

        if (object.has("size"))
            Preconditions.checkArgument(Utils.testRunnable(() -> size[0] = object.get("size").getAsInt()), "Size field needs to be an integer");

        if (object.has("items"))
            buttons.putAll(gson.fromJson(object.get("items").getAsJsonObject(), itemsToken.getType()));

        dInventory inv = new dInventory(title, size[0], null);
        buttons.forEach((s, dItem) -> inv.addButton(dItem.setID(s), dItem.getSlot()));

        return inv;
    }

    @Override
    public JsonElement serialize(dInventory dInventory, Type type, JsonSerializationContext jsonSerializationContext) {
        return JsonBuilder.object()
                .add("title", dInventory.getInventoryTitle())
                .add("size", dInventory.getInventorySize())
                .add("items", gson.toJsonTree(getButtons(dInventory.skeleton())))
                .build();
    }

    /** Utils  **/

    private Map<String, dItem> getButtons(dInventory inv) {
        Map<String, dItem> buttons = new LinkedHashMap<>();

        inv.getButtons().values().forEach(dItem -> {
            buttons.put(dItem.getID(), dItem);
        });

        return buttons;
    }

}

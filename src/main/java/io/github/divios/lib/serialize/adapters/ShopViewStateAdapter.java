package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.ShopGui;
import io.github.divios.lib.dLib.shop.view.ShopViewState;
import io.github.divios.lib.serialize.wrappers.WrappedDButton;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnstableApiUsage", "UnusedReturnValue"})
public class ShopViewStateAdapter implements JsonSerializer<ShopViewState>, JsonDeserializer<ShopViewState> {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(dItem.class, new dButtonAdapter())
            .registerTypeAdapter(WrappedDButton.class, new dButtonAdapter())
            .create();

    private static final TypeToken<LinkedHashMap<String, JsonElement>> diItemsToken = new TypeToken<LinkedHashMap<String, JsonElement>>() {
    };

    @Override
    public ShopViewState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        String title = object.has("title") ? object.get("title").getAsString() : "";
        int[] size = {27};
        Map<Integer, dItem> buttons = new LinkedHashMap<>();

        if (object.has("size"))
            Preconditions.checkArgument(Utils.testRunnable(() -> size[0] = object.get("size").getAsInt()), "Size field needs to be an integer");

        Preconditions.checkArgument(size[0] % 9 == 0, "Inventory size must be a multiple of 9");

        Map<String, JsonElement> items = gson.fromJson(object.get("items").getAsJsonObject(), diItemsToken.getType());
        for (Map.Entry<String, JsonElement> itemEntry : items.entrySet()) {
            try {
                dItem item = gson.fromJson(itemEntry.getValue(), dItem.class);
                buttons.put(item.getSlot(), item.setID(itemEntry.getKey()));
            } catch (Exception | Error e) {
                Log.warn("There was a problem parsing the item with id " + itemEntry.getKey());
                // e.printStackTrace();
                Log.warn(e.getMessage());
            }
        }

        addItemsWithMultipleSlots(object, buttons, size[0]);
        buttons.values().removeIf(dItem -> dItem.getSlot() >= size[0]);

        return new ShopViewState(title, size[0], buttons);
    }

    @Override
    public JsonElement serialize(ShopViewState dInventory, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.addProperty("title", dInventory.getTitle());
        json.addProperty("size", dInventory.getSize());
        json.add("items", gson.toJsonTree(getButtons(dInventory)));

        return json;
    }

    /**
     * Utils
     **/

    private Map<String, WrappedDButton> getButtons(ShopViewState inv) {
        Map<String, dItem> buttons = new LinkedHashMap<>();
        Set<Integer> flaggedSlots = new HashSet<>();
        Map<String, WrappedDButton> finalButtons = new LinkedHashMap<>();

        inv.getButtons().entrySet().stream()       // Get buttons sorted by slots
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(entry -> entry.getValue().clone())
                .forEach(dItem -> buttons.put(dItem.getID(), dItem));

        buttons.values().forEach(dItem -> {
            if (flaggedSlots.contains(dItem.getSlot())) return;

            List<Integer> similarItemsSlots = buttons.values().stream()
                    .filter(dItem1 -> dItem1.getItem().isSimilar(dItem.getItem()))
                    .map(io.github.divios.lib.dLib.dItem::getSlot)
                    .sorted()
                    .collect(Collectors.toList());

            WrappedDButton dButton = WrappedDButton.of(dItem);
            if (similarItemsSlots.size() == 1)
                finalButtons.put(dItem.getID(), dButton);
            else {
                io.github.divios.lib.dLib.dItem baseItem = inv.getButtons().get(similarItemsSlots.get(0)).clone();  // Less slot should be baseItem
                similarItemsSlots.remove(0);   // remove first since is added on WrappedDButton
                dButton.addMultipleSlots(similarItemsSlots);
                finalButtons.put(baseItem.getID(), dButton);
                flaggedSlots.addAll(similarItemsSlots);
            }
        });

        return finalButtons;
    }

    private static final TypeToken<LinkedHashMap<String, JsonObject>> itemsJsonToken = new TypeToken<LinkedHashMap<String, JsonObject>>() {
    };

    private void addItemsWithMultipleSlots(JsonObject object, Map<Integer, dItem> buttons, int size) {
        LinkedHashMap<String, JsonObject> items = gson.fromJson(object.get("items"), itemsJsonToken.getType());

        for (JsonObject item : items.values()) {
            TreeSet<Integer> multipleSlots = new TreeSet<>();

            if (!item.has("slot") || !item.get("slot").isJsonArray()) continue;

            item.get("slot").getAsJsonArray().forEach(element -> multipleSlots.add(element.getAsInt()));

            dItem baseItem = buttons.get(multipleSlots.pollFirst());
            if (baseItem == null) return;

            multipleSlots.forEach(integer -> {
                if (integer >= size) return;

                String newId = baseItem.getID() + integer;
                buttons.put(integer, baseItem.setID(newId));
            });
        }
    }

}

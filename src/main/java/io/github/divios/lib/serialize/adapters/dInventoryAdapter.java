package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.ShopGui;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;
import io.github.divios.lib.serialize.wrappers.WrappedDButton;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnstableApiUsage", "UnusedReturnValue"})
public class dInventoryAdapter implements JsonSerializer<ShopGui>, JsonDeserializer<ShopGui> {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(dItem.class, new dButtonAdapter())
            .registerTypeAdapter(WrappedDButton.class, new dButtonAdapter())
            .create();

    private static final TypeToken<LinkedHashMap<String, JsonElement>> diItemsToken = new TypeToken<LinkedHashMap<String, JsonElement>>() {
    };

    @Override
    public ShopGui deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        String title = object.has("title") ? object.get("title").getAsString() : "";
        int[] size = {27};
        Map<String, dItem> buttons = new LinkedHashMap<>();

        if (object.has("size"))
            Preconditions.checkArgument(Utils.testRunnable(() -> size[0] = object.get("size").getAsInt()), "Size field needs to be an integer");

        Preconditions.checkArgument(size[0] % 9 == 0, "Inventory size must be a multiple of 9");

        Map<String, JsonElement> items = gson.fromJson(object.get("items").getAsJsonObject(), diItemsToken.getType());
        for (Map.Entry<String, JsonElement> itemEntry : items.entrySet()) {
            try {
                buttons.put(itemEntry.getKey(), gson.fromJson(itemEntry.getValue(), dItem.class));
            } catch (Exception | Error e) {
                Log.warn("There was a problem parsing the item with id " + itemEntry.getKey());
                e.printStackTrace();
                Log.warn(e.getMessage());
            }
        }


        ShopGui inv = new ShopGui(null, title, Bukkit.createInventory(null, size[0]));
        buttons.forEach((s, dItem) -> inv.setButton(dItem.getSlot(), dItem.setID(s)));

        addItemsWithMultipleSlots(object, inv);

        return inv;
    }

    @Override
    public JsonElement serialize(ShopGui dInventory, Type type, JsonSerializationContext jsonSerializationContext) {
        return JsonBuilder.object()
                .add("title", dInventory.getTitle())
                .add("size", dInventory.size())
                .add("items", gson.toJsonTree(getButtons(dInventory)))
                .build();
    }

    /**
     * Utils
     **/

    private Map<String, WrappedDButton> getButtons(ShopGui inv) {
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

    private void addItemsWithMultipleSlots(JsonObject object, ShopGui inv) {
        LinkedHashMap<String, JsonObject> items = gson.fromJson(object.get("items"), itemsJsonToken.getType());

        for (JsonObject item : items.values()) {
            TreeSet<Integer> multipleSlots = new TreeSet<>();

            if (!item.has("slot") || !item.get("slot").isJsonArray()) continue;

            item.get("slot").getAsJsonArray().forEach(element -> multipleSlots.add(element.getAsInt()));

            dItem baseItem = inv.getButtons().get(multipleSlots.pollFirst());
            if (baseItem == null) return;

            multipleSlots.forEach(integer -> {
                String newId = baseItem.getID() + integer;
                inv.setButton(integer, baseItem.setID(newId));
            });
        }
    }

}

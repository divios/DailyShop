package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;
import io.github.divios.lib.serialize.wrappers.WrappedDButton;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnstableApiUsage", "UnusedReturnValue"})
public class dInventoryAdapter implements JsonSerializer<dInventory>, JsonDeserializer<dInventory> {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(dItem.class, new dButtonAdapter())
            .registerTypeAdapter(WrappedDButton.class, new dButtonAdapter())
            .create();

    private static final TypeToken<LinkedHashMap<String, dItem>> itemsToken = new TypeToken<LinkedHashMap<String, dItem>>() {
    };

    @Override
    public dInventory deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        String title = object.has("title") ? object.get("title").getAsString() : "";
        int[] size = {27};
        Map<String, dItem> buttons = new LinkedHashMap<>();

        if (object.has("size"))
            Preconditions.checkArgument(Utils.testRunnable(() -> size[0] = object.get("size").getAsInt()), "Size field needs to be an integer");

        Preconditions.checkArgument(size[0] % 9 == 0, "Inventory size must be a multiple of 9");

        if (object.has("items"))
            buttons.putAll(gson.fromJson(object.get("items").getAsJsonObject(), itemsToken.getType()));

        dInventory inv = new dInventory(title, size[0]);
        buttons.forEach((s, dItem) -> inv.addButton(dItem.setID(s), dItem.getSlot()));

        addItemsWithMultipleSlots(object, inv);

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

    /**
     * Utils
     **/

    private Map<String, WrappedDButton> getButtons(dInventory inv) {
        Map<String, dItem> buttons = new LinkedHashMap<>();
        Set<Integer> flaggedSlots = new HashSet<>();
        Map<String, WrappedDButton> finalButtons = new LinkedHashMap<>();

        inv.getButtonsSlots().entrySet().stream()       // Get buttons sorted by slots
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
                io.github.divios.lib.dLib.dItem baseItem = inv.getButtonsSlots().get(similarItemsSlots.get(0)).clone();  // Less slot should be baseItem
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

    private void addItemsWithMultipleSlots(JsonObject object, dInventory inv) {
        LinkedHashMap<String, JsonObject> items = gson.fromJson(object.get("items"), itemsJsonToken.getType());

        for (JsonObject item : items.values()) {
            TreeSet<Integer> multipleSlots = new TreeSet<>();

            if (!item.has("slot") || !item.get("slot").isJsonArray()) continue;

            item.get("slot").getAsJsonArray().forEach(element -> multipleSlots.add(element.getAsInt()));

            dItem baseItem = inv.getButtonsSlots().get(multipleSlots.pollFirst());
            if (baseItem == null) return;

            multipleSlots.forEach(integer -> {
                String newId = baseItem.getID() + integer;
                inv.addButton(baseItem.setID(newId), integer);
            });
        }
    }

}

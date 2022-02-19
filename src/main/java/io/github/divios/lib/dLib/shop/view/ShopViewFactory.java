package io.github.divios.lib.dLib.shop.view;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.shop.view.buttons.DailyItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class ShopViewFactory {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(dItem.class, (JsonSerializer<dItem>) (dItem, type, jsonSerializationContext) -> dItem.toJson())
            .registerTypeAdapter(dItem.class, (JsonDeserializer<dItem>) (jsonElement, type, jsonDeserializationContext) -> dItem.fromJson(jsonElement))
            .create();

    private static final TypeToken<ConcurrentHashMap<UUID, dItem>> buttonsToken = new TypeToken<ConcurrentHashMap<UUID, dItem>>() {
    };
    private static final TypeToken<ConcurrentSkipListSet<Integer>> dailySlotsToken = new TypeToken<ConcurrentSkipListSet<Integer>>() {
    };

    public static ShopView fromJson(JsonElement element, DailyItemFactory itemFactory) {
        JsonObject json = element.getAsJsonObject();

        Preconditions.checkArgument(!json.get("title").isJsonNull());
        Preconditions.checkArgument(!json.get("inventory").isJsonNull());

        String title = json.get("title").getAsString();

        Inventory inv = inventoryUtils.fromJson(json.get("inventory"));
        ConcurrentSkipListSet<Integer> dailyItemsSlots = gson.fromJson(json.get("dailySlots"), dailySlotsToken.getType());
        ConcurrentHashMap<UUID, dItem> buttons = gson.fromJson(json.get("buttons"), buttonsToken.getType());

        ShopView gui = new ShopView(title, inv, itemFactory);

        buttons.values().forEach(dItem -> {
            if (dailyItemsSlots.contains(dItem.getSlot()))
                gui.setDailyItem(dItem.getSlot(), dItem);
            else
                gui.setPaneItem(dItem.getSlot(), dItem);
        });

        return gui;
    }

    public static JsonElement toJson(ShopView gui) {

        HashMap<UUID, dItem> buttons = new HashMap<>();
        Set<Integer> dailyItemsSlots = new HashSet<>();

        gui.buttons.values().forEach(dItem -> buttons.put(dItem.getUUID(), dItem));

        gui.dailyItemsMap.forEach(dItem -> {
            dailyItemsSlots.add(dItem.getSlot());
            buttons.put(dItem.getUUID(), dItem);
        });

        return JsonBuilder.object()
                .add("title", gui.gui.getTitle())
                .add("inventory", inventoryUtils.toJson(gui.gui.getTitle(), Bukkit.createInventory(null, gui.gui.getSize())))
                .add("dailySlots", gson.toJsonTree(dailyItemsSlots, dailySlotsToken.getType()))
                .add("buttons", gson.toJsonTree(buttons, buttonsToken.getType()))
                .build();
    }

    public static ShopView createGui(dShop shop) {
        return new ShopView(shop.getName(), Bukkit.createInventory(null, 27, shop.getName()), new DailyItemFactory(shop));
    }

}

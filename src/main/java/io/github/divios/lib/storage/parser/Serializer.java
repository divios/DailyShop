package io.github.divios.lib.storage.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.storage.parser.states.dItemState;
import io.github.divios.lib.storage.parser.states.dShopInvState;
import io.github.divios.lib.storage.parser.states.dShopState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Serializer {

    private static final Gson gson = new Gson();

    private static JsonElement toJson(Object o) {
        return gson.toJsonTree(o);
    }

    protected static JsonObject serializeShop(dShop shop) {
        return JsonBuilder.object()
                .add("id", shop.getName())
                .add("shop", serializeShopInventory(shop))
                .add("items", serializeShopItems(shop))
                .build();
    }

    private static JsonElement serializeShopInventory(dShop shop) {
        return toJson(dShopInvState.toState(shop));
    }

    private static JsonElement serializeShopItems(dShop shop) {
        Map<UUID, dItemState> itemsCollect = new LinkedHashMap<>();
        shop.getItems().forEach(dItem -> itemsCollect.put(dItem.getUid(), dItemState.of(dItem.getItem())));
        return toJson(itemsCollect);
    }

}

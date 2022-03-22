package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.files.Settings;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.Exceptions;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.ShopAccount;
import io.github.divios.lib.dLib.shop.ShopOptions;
import io.github.divios.lib.dLib.shop.dShopState;
import io.github.divios.lib.dLib.shop.view.ShopViewState;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({"unused", "UnstableApiUsage", "UnusedReturnValue"})
public class dShopStateAdapter implements JsonSerializer<dShopState>, JsonDeserializer<dShopState> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(dItem.class, new dItemAdapter())
            .registerTypeAdapter(ShopViewState.class, new ShopViewStateAdapter())
            .create();

    private static final TypeToken<LinkedHashMap<String, JsonElement>> diItemsToken = new TypeToken<LinkedHashMap<String, JsonElement>>() {
    };

    @Override
    public dShopState deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        String id;
        int[] timer = {86400};
        Timestamp timestamp;
        boolean announce = true, Default = false;
        ShopAccount account = null;
        ShopViewState view = null;
        ShopOptions options = ShopOptions.DEFAULT;
        Collection<dItem> items = new ArrayList<>();

        Preconditions.checkArgument(object.has("id"), "A shop needs an ID");
        Preconditions.checkArgument(object.has("items"), "A shop needs items");

        if (object.has("timer"))
            Preconditions.checkArgument(Utils.testRunnable(() -> timer[0] = object.get("timer").getAsInt()), "Timer needs to be an integer");
        Preconditions.checkArgument((timer[0] >= 50 || timer[0] == -1), "timer needs to be >= 50");
        if (object.has("announce_restock"))
            Preconditions.checkArgument(Utils.testRunnable(() -> object.get("announce_restock").getAsBoolean()), "announce_restock field needs to be a boolean");
        if (object.has("default"))
            Preconditions.checkArgument(Utils.testRunnable(() -> object.get("default").getAsBoolean()), "default field needs to be a boolean");

        dShopState deserializedShop;

        id = object.get("id").getAsString();
        timer[0] = object.has("timer") ? object.get("timer").getAsInt() : Settings.DEFAULT_TIMER.getValue().getAsInt();
        timestamp = object.has("timestamp") ? new Timestamp(wrappedParse(object.get("timestamp").getAsString()).getTime()) : new Timestamp(System.currentTimeMillis());

        // Set miscellaneous fields
        if (object.has("announce_restock"))
            announce = object.get("announce_restock").getAsBoolean();
        if (object.has("default"))
            Default = object.get("default").getAsBoolean();
        if (object.has("clickActions"))
            try { options = ShopOptions.fromJson(object.get("clickActions")); } catch (Exception e) { Log.warn(e.getMessage()); }
        if (object.has("balance"))
            account = ShopAccount.fromJson(object.get("balance"));

        // Deserialize shop Gui
        DebugLog.info("Reading shop from shop " + id);
        if (object.has("shop")) {
            view = gson.fromJson(object.get("shop"), ShopViewState.class);
        }

        // Deserialize Items
        DebugLog.info("Reading items from shop " + id);
        Map<String, JsonElement> itemsMap = gson.fromJson(object.get("items").getAsJsonObject(), diItemsToken.getType());
        for (Map.Entry<String, JsonElement> itemEntry : itemsMap.entrySet()) {
            try {
                dItem dItem = gson.fromJson(itemEntry.getValue(), io.github.divios.lib.dLib.dItem.class);
                items.add(dItem.setID(itemEntry.getKey()));
            } catch (Exception | Error e) {
                Log.warn("There was a problem parsing the item with id " + itemEntry.getKey());
                // e.printStackTrace();
                Log.warn(e.getMessage());
            }
        }

        return new dShopState(id, timer[0], announce, Default, options, account, view, items);
    }

    @Override
    public JsonElement serialize(dShopState shop, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();

        json.addProperty("id", shop.getName());
        json.addProperty("timer", shop.getTimer());
        json.add("clickActions", shop.getOptions().toJson());

        if (!shop.isAnnounce()) json.addProperty("announce_restock", false);
        if (shop.isDefault()) json.addProperty("default", true);
        if (shop.getAccount() != null) {
            JsonElement accountJson = shop.getAccount().toJson();
            accountJson.getAsJsonObject().remove("current_balance");
            json.add("balance", json);
        }

        json.addProperty("timeStamp", (Boolean) null);
        json.add("shop", gson.toJsonTree(shop.getView(), ShopViewState.class));
        json.add("items", gson.toJsonTree(parseUUIDs(shop.getItems())));

        return json;
    }

    /**
     * Utils
     **/

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

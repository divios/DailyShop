package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dAction;

import java.lang.reflect.Type;
import java.util.List;

public class dActionAdapter implements JsonSerializer<Pair<dAction, String>>, JsonDeserializer<Pair<dAction, String>> {

    private final static Gson gson = new Gson();
    private final static TypeToken<List<String>> listStringType = new TypeToken<List<String>>() {};

    @Override
    public Pair<dAction, String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject action = jsonElement.getAsJsonObject();
        dAction typeAction[] = {null};
        String data = "";

        Preconditions.checkArgument(action.has("type"), "An action needs a type field");
        Preconditions.checkArgument(Utils.testRunnable(() -> typeAction[0] = dAction.valueOf(action.get("type").getAsString())));

        if (action.has("data")) {
            switch (typeAction[0]) {
                case RUN_CMD:
                case RUN_PLAYER_CMD:
                    StringBuilder stringBuilder = new StringBuilder();
                    action.get("data").getAsJsonArray().forEach(element ->
                            stringBuilder.append(element.getAsString() + ";:"));
                    data = stringBuilder.substring(0, stringBuilder.length() - 2);  // remove last ";:"
                    break;
                case OPEN_SHOP:
                case SHOW_ALL_ITEMS:
                    data = action.get("data").getAsString();
                    break;
            }
        }

        return Pair.of(typeAction[0], data);
    }

    @Override
    public JsonElement serialize(Pair<dAction, String> action, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();

        object.addProperty("type", action.get1().name());

        switch (action.get1()) {
            case RUN_CMD:
            case RUN_PLAYER_CMD:
                object.add("data", gson.toJsonTree(action.get2().split(";:")));
                break;
            case OPEN_SHOP:
            case SHOW_ALL_ITEMS:
                object.addProperty("data", action.get2());
                break;
        }

        return object;
    }

}

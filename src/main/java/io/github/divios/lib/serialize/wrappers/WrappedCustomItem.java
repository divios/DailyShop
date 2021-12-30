package io.github.divios.lib.serialize.wrappers;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.dailyShop.utils.MMOUtils;
import io.github.divios.dailyShop.utils.OraxenUtils;
import io.github.divios.lib.dLib.dItem;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public class WrappedCustomItem {

    private final JsonObject object;

    public static boolean isCustomItem(dItem item) {
        return customItemType.isCustomItem(item);
    }

    public static WrappedCustomItem from(JsonObject element) {
        return new WrappedCustomItem(element);
    }

    private WrappedCustomItem(JsonObject element) {
        this.object = element;
    }

    public dItem parseItem() {
        dItem item = null;

        Preconditions.checkArgument(customItemType.isCustomObject(object), "Invalid object, check the format or if the plugin is running");

        switch (customItemType.getObjectType(object)) {
            case MMOITEM:
                JsonObject mmoItemObject = object.get("mmoItem").getAsJsonObject();

                Preconditions.checkArgument(mmoItemObject.has("type"), "MMOItem needs a type field");
                Preconditions.checkArgument(mmoItemObject.has("id"), "MMOItem needs an id field");

                item = dItem.of(MMOUtils.createMMOItem(object.get("type").getAsString(), object.get("id").getAsString()));
                break;

            case ORAXEN:
                JsonObject oraxenObject = object.get("oraxen").getAsJsonObject();
                String id;

                Preconditions.checkArgument(oraxenObject.has("id"), "Oraxen item needs an id");
                Preconditions.checkArgument(OraxenUtils.isValidId(id = object.get("id").getAsString()), "That oraxen ID does not exist");

                item = dItem.of(OraxenUtils.createItemByID(id));
                break;

            default:
                throw new RuntimeException("Invalid customItemType");
        }

        return Objects.requireNonNull(item, "Something went wrong while parsing custom Item");
    }

    public static JsonElement serializeCustomItem(dItem item) {
        Preconditions.checkArgument(customItemType.isCustomItem(item), "Can't serialize item, is not custom item");

        JsonObject object = new JsonObject();

        switch (customItemType.getItemType(item)) {
            case MMOITEM:
                object.add("mmoItem",
                        JsonBuilder.object()
                                .add("type", MMOUtils.getType(item.getDailyItem()))
                                .add("id", MMOUtils.getType(item.getDailyItem()))
                                .build()
                );
                break;
            case ORAXEN:
                object.add("oraxenItem",
                        JsonBuilder.object()
                                .add("id", OraxenUtils.getId(item.getDailyItem()))
                                .build()
                );
                break;
            default:
                throw new RuntimeException("Invalid customItemType");
        }

        return object;
    }


    private enum customItemType {
        MMOITEM(object -> object.has("mmoItem"), dItem -> MMOUtils.isMMOItem(dItem.getDailyItem())),
        ORAXEN(object -> object.has("oraxenItem"), dItem -> OraxenUtils.isOraxenItem(dItem.getDailyItem()));

        Predicate<JsonObject> isElementType;
        Predicate<dItem> isType;

        customItemType(Predicate<JsonObject> isElementType, Predicate<dItem> isType) {
            this.isElementType = isElementType;
            this.isType = isType;
        }

        public static boolean isCustomItem(dItem item) {
            for (customItemType value : values()) {
                if (value.isType.test(item)) return true;
            }
            return false;
        }

        public static boolean isCustomObject(JsonObject object) {
            for (customItemType value : values()) {
                if (value.isElementType.test(object)) return true;
            }
            return false;
        }

        public static customItemType getObjectType(JsonObject object) {
            return Arrays.stream(values())
                    .filter(customItemType -> customItemType.isElementType.test(object))
                    .findFirst()
                    .orElse(null);
        }

        public static customItemType getItemType(dItem item) {
            return Arrays.stream(values())
                    .filter(customItemType -> customItemType.isType.test(item))
                    .findFirst()
                    .orElse(null);
        }

    }

}

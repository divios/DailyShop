package io.github.divios.lib.serialize.adapters;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dAction;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.serialize.wrappers.WrappedEnchantment;
import io.github.divios.lib.serialize.wrappers.WrappedItemFlags;
import io.github.divios.lib.serialize.wrappers.WrappedNBT;
import org.bukkit.inventory.ItemFlag;

import java.lang.reflect.Type;
import java.util.List;

public class dButtonAdapter implements JsonSerializer<dItem>, JsonDeserializer<dItem> {

    private static final TypeToken<List<String>> stringListToken = new TypeToken<List<String>>() {};
    private static final TypeToken<List<WrappedEnchantment>> enchantsListToken = new TypeToken<List<WrappedEnchantment>>() {};

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Pair.class, new dActionAdapter())
            .create();

    @Override
    public JsonElement serialize(dItem dItem, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject merchant = new JsonObject();

        if (dItem.isAIR()) {
            return getAirItemAsJson(dItem);
        }

        String name = ItemUtils.getName(dItem.getDailyItem());
        if (!name.isEmpty())
            merchant.addProperty("name", FormatUtils.unColor(name));

        List<String> lore = ItemUtils.getLore(dItem.getDailyItem());
        if (!lore.isEmpty())
            merchant.add("lore", gson.toJsonTree(lore));

        if (!dItem.isCustomHead())
            merchant.addProperty("material", ItemUtils.getMaterial(dItem.getRealItem()).name());
        else
            merchant.addProperty("material", "base64:" + dItem.getCustomHeadUrl());

        Pair<dAction, String> pair = dItem.getAction();
        if (pair.get1() != dAction.EMPTY)
            merchant.add("action", gson.toJsonTree(pair, Pair.class));

        if (dItem.isMultipleSlots())
            merchant.add("slot", gson.toJsonTree(dItem.getMultipleSlots()));
        else
            merchant.addProperty("slot", dItem.getSlot());

        if (dItem.isUnbreakble()) merchant.addProperty("unbreakable", true);

        List<String> flags;
        if (!(flags = WrappedItemFlags.of(dItem).getFlags()).isEmpty())
            merchant.add("flags", gson.toJsonTree(flags, stringListToken.getType()));

        WrappedNBT nbt = WrappedNBT.valueOf(dItem.getNBT());
        if (!nbt.isEmpty()) merchant.add("nbt", nbt.getNbt());

        return merchant;
    }

    @Override
    public dItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        dItem ditem = dItem.of(XMaterial.DIRT_PATH.parseItem());

        Preconditions.checkArgument(object.has("material"), "An item needs a material");
        Preconditions.checkArgument(Utils.testRunnable(() -> XMaterial.valueOf(object.get("material").getAsString())) || object.get("material").getAsString().startsWith("base64:"), "Invalid material");
        Preconditions.checkArgument(object.has("slot"), "An item needs a slot");

        if (object.get("material").getAsString().equals("AIR")) {
            dItem air = dItem.AIR();
            deserializeSlots(object.get("slot"), air);
            return air;
        }

        String material = object.get("material").getAsString();
        if (material.startsWith("base64:"))
            ditem.setCustomPlayerHead(material.replace("base64:", ""));
        else
            ditem.setMaterial(XMaterial.valueOf(material));

        if (object.has("name"))
            ditem.setDisplayName(object.get("name").getAsString());

        if (object.has("lore"))
            ditem.setLore(gson.fromJson(object.get("lore"), stringListToken.getType()));

        if (object.has("enchantments")) {
            List <WrappedEnchantment> enchants = gson.fromJson(object.get("enchantments"), enchantsListToken.getType());
            enchants.forEach(enchant -> ditem.addEnchantments(enchant.getEnchant(), enchant.getLevel()));
        }

        if (object.has("action")) {
            Pair<dAction, String> action = gson.fromJson(object.get("action"), Pair.class);
            ditem.setAction(action.get1(), action.get2());
        }

        if (object.has("unbreakable") && object.get("unbreakable").getAsBoolean()) ditem.setUnbreakable();

        if (object.has("flags")) {
            List<String> flags = gson.fromJson(object.get("flags"), stringListToken.getType());
            flags.forEach(s -> {
                Preconditions.checkArgument(Utils.testRunnable(() -> ItemFlag.valueOf(s.toUpperCase())), "Incorrect flag " + s);
                ditem.setFlag(ItemFlag.valueOf(s.toUpperCase()));
            });
        }

        deserializeSlots(object.get("slot"), ditem);

        if (object.has("nbt")) ditem.setNBT(object.get("nbt").getAsJsonObject());

        return ditem;
    }

    // UTILS //

    private JsonObject getAirItemAsJson(dItem dItem) {
        JsonBuilder.JsonObjectBuilder builder = JsonBuilder.object()
                .add("material", "AIR");

        if (dItem.isMultipleSlots())
            builder.add("slot", gson.toJsonTree(dItem.getMultipleSlots()));
        else
            builder.add("slot", dItem.getSlot());

        return builder.build();
    }

    private void deserializeSlots(JsonElement object, dItem ditem) {
        if (object.isJsonArray()) {     // Get the min slot if multipleSlots
            int minSlot = 999;
            for (JsonElement element : object.getAsJsonArray())
                if (element.getAsInt() < minSlot) minSlot = element.getAsInt();
            ditem.setSlot(minSlot);
        } else
            ditem.setSlot(object.getAsInt());
    }

}

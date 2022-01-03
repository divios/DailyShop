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
import io.github.divios.jcommands.utils.Primitives;
import io.github.divios.lib.dLib.dAction;
import io.github.divios.lib.dLib.newDItem;
import io.github.divios.lib.serialize.wrappers.WrappedDButton;
import io.github.divios.lib.serialize.wrappers.WrappedEnchantment;
import io.github.divios.lib.serialize.wrappers.WrappedItemFlags;
import io.github.divios.lib.serialize.wrappers.WrappedNBT;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@SuppressWarnings({"unused", "UnstableApiUsage", "UnusedReturnValue"})
public class dButtonAdapter implements JsonSerializer<WrappedDButton>, JsonDeserializer<newDItem> {

    private static final TypeToken<List<String>> stringListToken = new TypeToken<List<String>>() {
    };
    private static final TypeToken<List<WrappedEnchantment>> enchantsListToken = new TypeToken<List<WrappedEnchantment>>() {
    };

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Pair.class, new dActionAdapter())
            .create();

    @Override
    public JsonElement serialize(WrappedDButton dItem, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject merchant = new JsonObject();

        if (dItem.getDItem().isAir()) {
            return getAirItemAsJson(dItem);
        }

        ItemStack item = dItem.getDItem().getItem();
        String name = ItemUtils.getName(item);
        if (!name.isEmpty())
            merchant.addProperty("name", FormatUtils.unColor(name));

        List<String> lore = ItemUtils.getLore(item);
        if (!lore.isEmpty())
            merchant.add("lore", gson.toJsonTree(lore));

        // if (!dItem.isCustomHead())   // TODO
        merchant.addProperty("material", ItemUtils.getMaterial(item).name());
        //else
        //  merchant.addProperty("material", "base64:" + dItem.getCustomHeadUrl());

        newDItem.WrapperAction action = dItem.getDItem().getAction();
        Pair<dAction, String> pair = Pair.of(action.getAction(), action.getData());
        if (pair.get1() != dAction.EMPTY)
            merchant.add("action", gson.toJsonTree(pair, Pair.class));

        if (dItem.isMultipleSlots())
            merchant.add("slot", gson.toJsonTree(dItem.getMultipleSlots()));
        else
            merchant.addProperty("slot", dItem.getDItem().getSlot());

        if (ItemUtils.getMetadata(item).isUnbreakable()) merchant.addProperty("unbreakable", true);

        List<String> flags;
        if (!(flags = WrappedItemFlags.of(item).getFlags()).isEmpty())
            merchant.add("flags", gson.toJsonTree(flags, stringListToken.getType()));

        WrappedNBT nbt = WrappedNBT.valueOf(dItem.getDItem().getNBT());
        if (!nbt.isEmpty()) merchant.add("nbt", nbt.getNbt());

        return merchant;
    }

    @Override
    public newDItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        newDItem ditem = newDItem.of(XMaterial.DIRT_PATH.parseItem());

        Preconditions.checkArgument(object.has("material"), "An item needs a material");
        Preconditions.checkArgument(Utils.testRunnable(() -> XMaterial.valueOf(object.get("material").getAsString())) || object.get("material").getAsString().startsWith("base64:"), "Invalid material");
        Preconditions.checkArgument(object.has("slot"), "An item needs a slot");

        if (object.get("material").getAsString().equals("AIR")) {
            newDItem air = newDItem.AIR();
            deserializeSlots(object.get("slot"), air);
            return air;
        }

        String material = object.get("material").getAsString();
        //if (material.startsWith("base64:"))
        //  ditem.setCustomPlayerHead(material.replace("base64:", ""));
        //else
        ditem.setItem(ItemUtils.setMaterial(ditem.getItem(), XMaterial.valueOf(material)));

        if (object.has("name"))
            ditem.setItem(ItemUtils.setName(ditem.getItem(), object.get("name").getAsString()));

        if (object.has("lore"))
            ditem.setItem(ItemUtils.setLore(ditem.getItem(), (List<String>) gson.fromJson(object.get("lore"), stringListToken.getType())));

        if (object.has("enchantments")) {
            List<WrappedEnchantment> enchants = gson.fromJson(object.get("enchantments"), enchantsListToken.getType());
            enchants.forEach(enchant -> ditem.setItem(ItemUtils.addEnchant(ditem.getItem(), enchant.getEnchant(), enchant.getLevel())));
        }

        if (object.has("action")) {
            Pair<dAction, String> action = gson.fromJson(object.get("action"), Pair.class);
            ditem.setAction(action.get1(), action.get2());
        }

        if (object.has("unbreakable") && object.get("unbreakable").getAsBoolean())
            ditem.setItem(ItemUtils.setUnbreakable(ditem.getItem()));

        if (object.has("flags")) {
            List<String> flags = gson.fromJson(object.get("flags"), stringListToken.getType());
            flags.forEach(s -> {
                Preconditions.checkArgument(Utils.testRunnable(() -> ItemFlag.valueOf(s.toUpperCase())), "Incorrect flag " + s);
                ditem.setItem(ItemUtils.addItemFlags(ditem.getItem(), ItemFlag.valueOf(s.toUpperCase())));
            });
        }

        if (object.has("nbt")) ditem.setNBT(object.get("nbt").getAsJsonObject());

        deserializeSlots(object.get("slot"), ditem);

        return ditem;
    }

    // UTILS //

    private JsonObject getAirItemAsJson(WrappedDButton dItem) {
        JsonBuilder.JsonObjectBuilder builder = JsonBuilder.object()
                .add("material", "AIR");

        if (dItem.isMultipleSlots())
            builder.add("slot", gson.toJsonTree(dItem.getMultipleSlots()));
        else
            builder.add("slot", dItem.getDItem().getSlot());

        return builder.build();
    }

    private void deserializeSlots(JsonElement object, newDItem item) {

        if (object.isJsonArray()) {     // Get the min slot if multipleSlots
            int minSlot = 999;
            for (JsonElement element : object.getAsJsonArray())
                if (element.getAsInt() < minSlot) minSlot = Primitives.getAsInteger(element.getAsString());
            item.setSlot(minSlot);
        } else
            item.setSlot(object.getAsInt());
    }

}

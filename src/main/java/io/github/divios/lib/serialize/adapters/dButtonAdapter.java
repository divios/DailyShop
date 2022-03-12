package io.github.divios.lib.serialize.adapters;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jcommands.util.Primitives;
import io.github.divios.lib.dLib.dAction;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.serialize.wrappers.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@SuppressWarnings({"unused", "UnstableApiUsage", "UnusedReturnValue"})
public class dButtonAdapter implements JsonSerializer<WrappedDButton>, JsonDeserializer<dItem> {

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

        if (ItemUtils.getMetadata(item).hasDisplayName())
            merchant.addProperty("name", Utils.JTEXT_PARSER.unParse(ItemUtils.getName(item)));

        List<String> lore = ItemUtils.getLore(item);
        if (!lore.isEmpty())
            merchant.add("lore", gson.toJsonTree(Utils.JTEXT_PARSER.unParse(lore)));

        if (ItemUtils.getMaterial(item) == XMaterial.PLAYER_HEAD.parseMaterial()
                && SkullUtils.getSkinValue(ItemUtils.getMetadata(item)) != null)
            merchant.addProperty("material", "base64:" + SkullUtils.getSkinValue(ItemUtils.getMetadata(item)));
        else
            merchant.addProperty("material", WrappedMaterial.getMaterial(item));

        io.github.divios.lib.dLib.dItem.WrapperAction action = dItem.getDItem().getAction();
        Pair<dAction, String> pair = Pair.of(action.getAction(), action.getData());
        if (pair.get1() != dAction.EMPTY)
            merchant.add("action", gson.toJsonTree(pair, Pair.class));

        if (dItem.isMultipleSlots())
            merchant.add("slot", gson.toJsonTree(dItem.getMultipleSlots()));
        else
            merchant.addProperty("slot", dItem.getDItem().getSlot());

        if (ReflectionUtils.VER >= 12 && ItemUtils.getMetadata(item).isUnbreakable())
            merchant.addProperty("unbreakable", true);

        List<String> flags;
        if (!(flags = WrappedItemFlags.of(item).getFlags()).isEmpty())
            merchant.add("flags", gson.toJsonTree(flags, stringListToken.getType()));

        WrappedNBT nbt = WrappedNBT.valueOf(dItem.getDItem().getItem());
        if (!nbt.isEmpty()) merchant.addProperty("nbt", nbt.getNbt());

        return merchant;
    }

    @Override
    public dItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        dItem ditem = dItem.of(XMaterial.DIRT.parseItem());

        Preconditions.checkArgument(object.has("material"), "An item needs a material");
        Preconditions.checkArgument(object.has("slot"), "An item needs a slot");

        if (object.get("material").getAsString().equals("AIR")) {
            dItem air = dItem.AIR();
            deserializeSlots(object.get("slot"), air);
            return air;
        }

        ditem.setItem(WrappedMaterial.of(object.get("material").getAsString()).parseItem());

        if (object.has("name"))
            ditem.setItem(ItemUtils.setName(ditem.getItem(), Utils.JTEXT_PARSER.parse(object.get("name").getAsString())));

        if (object.has("lore"))
            ditem.setItem(ItemUtils.setLore(ditem.getItem(), Utils.JTEXT_PARSER.parse((List<String>) gson.fromJson(object.get("lore"), stringListToken.getType()))));

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

        if (object.has("nbt"))
            ditem.setItem(WrappedNBT.mergeNBT(ditem.getItem(), object.get("nbt")));

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

    private void deserializeSlots(JsonElement object, dItem item) {

        if (object.isJsonArray()) {     // Get the min slot if multipleSlots
            int minSlot = 999;
            for (JsonElement element : object.getAsJsonArray())
                if (element.getAsInt() < minSlot) minSlot = Primitives.getAsInteger(element.getAsString());
            item.setSlot(minSlot);
        } else
            item.setSlot(object.getAsInt());
    }

}

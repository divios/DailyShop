package io.github.divios.lib.serialize.adapters;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.dailyShop.economies.economy;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dAction;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dRarity;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.serialize.wrappers.WrappedEnchantment;
import io.github.divios.lib.serialize.wrappers.WrappedNBT;

import java.lang.reflect.Type;
import java.util.List;

public class dButtonAdapter implements JsonSerializer<dItem>, JsonDeserializer<dItem> {

    private static TypeToken<List<String>> stringListToken = new TypeToken<List<String>>() {};
    private static TypeToken<List<WrappedEnchantment>> enchantsListToken = new TypeToken<List<WrappedEnchantment>>() {};

    private static final Gson gson = new GsonBuilder().create();

    @Override
    public JsonElement serialize(dItem dItem, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject merchant = new JsonObject();

        String name = ItemUtils.getName(dItem.getItem());
        if (!name.isEmpty()) merchant.addProperty("name", FormatUtils.unColor(name));

        List<String> lore = ItemUtils.getLore(dItem.getItem());
        if (!lore.isEmpty()) merchant.add("lore", gson.toJsonTree(lore));

        merchant.addProperty("material", XMaterial.matchXMaterial(dItem.getItem()).name());

        Pair<dAction, String> pair = dItem.getAction();
        if (!pair.get1().name().equals("EMPTY"))
            merchant.add("action", JsonBuilder.object()
                    .add("type", pair.get1().name())
                    .add("data", pair.get2())
                    .build()
            );

        merchant.addProperty("slot", dItem.getSlot());

        WrappedNBT nbt = WrappedNBT.valueOf(dItem.getNBT());
        if (!nbt.isEmpty()) merchant.add("nbt", nbt.getNbt());

        return merchant;
    }

    @Override
    public dItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        dItem ditem = dItem.of(XMaterial.DIRT_PATH.parseItem());

        Preconditions.checkArgument(object.has("material"), "An item needs a material");
        Preconditions.checkArgument(Utils.testRunnable(() -> XMaterial.valueOf(object.get("material").getAsString())), "Invalid material");
        Preconditions.checkArgument(object.has("slot"), "An item needs a slot");
        Preconditions.checkArgument(Utils.testRunnable(() -> object.get("slot").getAsInt()), "Slot field needs to be an integer");

        ditem.setMaterial(XMaterial.valueOf(object.get("material").getAsString()));
        if (object.has("name")) ditem.setDisplayName(object.get("name").getAsString());
        if (object.has("lore")) ditem.setLore(gson.fromJson(object.get("lore"), stringListToken.getType()));
        if (object.has("enchantments")) {
            List <WrappedEnchantment> enchants = gson.fromJson(object.get("enchantments"), enchantsListToken.getType());
            enchants.forEach(enchant -> ditem.addEnchantments(enchant.getEnchant(), enchant.getLevel()));
        }
        if (object.has("action")) {
            JsonObject action = object.get("action").getAsJsonObject();
            dAction typeAction[] = {null};

            Preconditions.checkArgument(action.has("type"), "An action needs a type field");
            Preconditions.checkArgument(Utils.testRunnable(() -> typeAction[0] = dAction.valueOf(action.get("type").getAsString())));

            ditem.setAction(typeAction[0], action.has("data") ? action.get("data").getAsString() : "");
        }

        ditem.setSlot(object.get("slot").getAsInt());
        if (object.has("nbt")) ditem.setNBT(object.get("nbt").getAsJsonObject());

        return ditem;
    }
}

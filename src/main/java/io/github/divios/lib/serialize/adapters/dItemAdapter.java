package io.github.divios.lib.serialize.adapters;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.economies.economy;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dRarity;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.serialize.wrappers.WrappedEnchantment;
import io.github.divios.lib.serialize.wrappers.WrappedNBT;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class dItemAdapter implements JsonSerializer<dItem>, JsonDeserializer<dItem> {

    private static TypeToken<List<String>> stringListToken = new TypeToken<List<String>>() {};
    private static TypeToken<List<WrappedEnchantment>> enchantsListToken = new TypeToken<List<WrappedEnchantment>>() {};

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(WrappedEnchantment.class, new enchantmentAdapter())
            .registerTypeHierarchyAdapter(dStock.class, new dStockAdapter())
            .registerTypeHierarchyAdapter(economy.class, new economyAdapter())
            .create();

    @Override
    public JsonElement serialize(dItem dItem, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject merchant = new JsonObject();

        String name = ItemUtils.getName(dItem.getRawItem());
        if (!name.isEmpty()) merchant.addProperty("name", FormatUtils.unColor(name));

        List<String> lore = ItemUtils.getLore(dItem.getRawItem());
        if (!lore.isEmpty()) merchant.add("lore", gson.toJsonTree(ItemUtils.getLore(dItem.getRawItem())));

        merchant.addProperty("material", ItemUtils.getMaterial(dItem.getRawItem()).name());
        merchant.addProperty("buyPrice", dItem.getBuyPrice().get().toString());
        merchant.addProperty("sellPrice", dItem.getSellPrice().get().toString());
        dItem.getSetItems().ifPresent(integer -> merchant.addProperty("set", integer));
        if (dItem.hasStock()) merchant.add("stock", gson.toJsonTree(dItem.getStock()));
        if (!dItem.getEnchantments().isEmpty()) merchant.add("enchantments", gson.toJsonTree(wrapEnchants(dItem.getEnchantments())));
        dItem.getCommands().ifPresent(strings -> merchant.add("commands", gson.toJsonTree(strings)));
        dItem.getPermsBuy().ifPresent(strings -> merchant.add("buyPerms", gson.toJsonTree(strings)));
        dItem.getPermsSell().ifPresent(strings -> merchant.add("sellPerms", gson.toJsonTree(strings)));
        merchant.addProperty("rarity", dItem.getRarity().getKey());
        merchant.add("econ", gson.toJsonTree(dItem.getEconomy()));
        merchant.addProperty("confirm_gui", dItem.isConfirmGuiEnabled());

        WrappedNBT nbt = WrappedNBT.valueOf(dItem.getNBT());
        if (!nbt.isEmpty()) merchant.add("nbt", nbt.getNbt());

        return merchant;
    }

    @Override
    public dItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Preconditions.checkArgument(object.has("material"), "An item needs a material");
        Preconditions.checkArgument(Utils.testRunnable(() -> XMaterial.valueOf(object.get("material").getAsString())), "Invalid material");

        dItem ditem = dItem.of(XMaterial.DIRT.parseItem());

        ditem.setMaterial(XMaterial.valueOf(object.get("material").getAsString()));
        if (object.has("name")) ditem.setDisplayName(object.get("name").getAsString());
        if (object.has("lore")) ditem.setLore(gson.fromJson(object.get("lore"), stringListToken.getType()));
        if (object.has("rarity")) ditem.setRarity(dRarity.fromKey(object.get("rarity").getAsString()));
        if (object.has("econ")) ditem.setEconomy(gson.fromJson(object.get("econ").getAsJsonObject(), economy.class));
        if (object.has("buyPrice")) ditem.setBuyPrice(object.get("buyPrice").getAsDouble());
        if (object.has("sellPrice")) ditem.setSellPrice(object.get("sellPrice").getAsDouble());
        if (object.has("buyPerms")) ditem.setPermsBuy(gson.fromJson(object.get("buyPerms"), stringListToken.getType()));
        if (object.has("sellPerms")) ditem.setPermsSell(gson.fromJson(object.get("sellPerms"), stringListToken.getType()));
        if (object.has("enchantments")) {
            List <WrappedEnchantment> enchants = gson.fromJson(object.get("enchantments"), enchantsListToken.getType());
            enchants.forEach(enchant -> ditem.addEnchantments(enchant.getEnchant(), enchant.getLevel()));
        }
        if (object.has("set")) ditem.setSetItems(object.get("set").getAsInt());
        if (object.has("stock")) ditem.setStock(gson.fromJson(object.get("stock"), dStock.class));
        if (object.has("confirm_gui")) ditem.setConfirm_gui(object.get("confirm_gui").getAsBoolean());
        if (object.has("nbt")) ditem.setNBT(object.get("nbt").getAsJsonObject());

        return ditem;
    }

    /** Utils **/

    private List<WrappedEnchantment> wrapEnchants(Map<Enchantment, Integer> enchants) {
        return enchants.entrySet().stream()
                .map(entry -> WrappedEnchantment.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}

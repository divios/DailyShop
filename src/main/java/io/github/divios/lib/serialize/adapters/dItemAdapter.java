package io.github.divios.lib.serialize.adapters;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.economies.economy;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dRarity;
import io.github.divios.lib.dLib.newDItem;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.serialize.wrappers.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnstableApiUsage", "UnusedReturnValue"})
public class dItemAdapter implements JsonSerializer<newDItem>, JsonDeserializer<newDItem> {

    private static final TypeToken<List<String>> stringListToken = new TypeToken<List<String>>() {
    };
    private static final TypeToken<List<WrappedEnchantment>> enchantsListToken = new TypeToken<List<WrappedEnchantment>>() {
    };

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(WrappedEnchantment.class, new enchantmentAdapter())
            .registerTypeHierarchyAdapter(dStock.class, new dStockAdapter())
            .registerTypeHierarchyAdapter(economy.class, new economyAdapter())
            .registerTypeAdapter(dPrice.class, new dPriceAdapter())
            .registerTypeHierarchyAdapter(PotionMeta.class, new potionEffectsAdapter())
            .create();

    @Override
    public JsonElement serialize(newDItem dItem, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject merchant = new JsonObject();

        ItemStack item = dItem.getItem();

        String name = ItemUtils.getName(item);
        if (!name.isEmpty()) merchant.addProperty("name", FormatUtils.unColor(name));

        List<String> lore = ItemUtils.getLore(dItem.getItem());
        if (!lore.isEmpty()) merchant.add("lore", gson.toJsonTree(lore));

        if (WrappedCustomItem.isCustomItem(item))
            merchant.add("item", WrappedCustomItem.serializeCustomItem(item));
        else {
            merchant.addProperty("material", WrappedMaterial.getMaterial(item));
            //if (dItem.isSpawner()) merchant.addProperty("mob", dItem.getSpawnerType().name());
        }

        if (item.getAmount() > 1) merchant.addProperty("quantity", item.getAmount());
        merchant.add("buyPrice", gson.toJsonTree(dItem.getDBuyPrice()));
        merchant.add("sellPrice", gson.toJsonTree(dItem.getDSellPrice()));
        if (dItem.getDStock() != null) merchant.add("stock", gson.toJsonTree(dItem.getDStock()));
        if (!item.getEnchantments().isEmpty())
            merchant.add("enchantments", gson.toJsonTree(wrapEnchants(item.getEnchantments())));
        if (dItem.getCommands() != null) merchant.add("commands", gson.toJsonTree(dItem.getCommands()));
        if (dItem.getBuyPerms() != null) merchant.add("buyPerms", gson.toJsonTree(dItem.getBuyPerms()));
        if (dItem.getSellPerms() != null) merchant.add("sellPerms", gson.toJsonTree(dItem.getSellPerms()));
        merchant.addProperty("rarity", dItem.getRarity().getKey());
        merchant.add("econ", gson.toJsonTree(dItem.getEcon()));
        if (!dItem.isConfirmGui()) merchant.addProperty("confirm_gui", false);
        if (dItem.getBundle() != null) merchant.add("bundle", gson.toJsonTree(dItem.getBundle()));

        if (ItemUtils.getMetadata(item).isUnbreakable()) merchant.addProperty("unbreakable", true);

        if (XPotion.canHaveEffects(item.getType())) {
            merchant.add("potion", gson.toJsonTree(ItemUtils.getPotionMeta(item), PotionMeta.class));
        }

        List<String> flags;
        if (!(flags = WrappedItemFlags.of(item).getFlags()).isEmpty())
            merchant.add("flags", gson.toJsonTree(flags, stringListToken.getType()));

        WrappedNBT nbt;
        if (!(nbt = WrappedNBT.valueOf(dItem.getNBT())).isEmpty())
            merchant.add("nbt", nbt.getNbt());

        return merchant;
    }

    @Override
    public newDItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Preconditions.checkArgument(object.has("material") || object.has("item"), "An item needs a material");
        Preconditions.checkArgument(Utils.testRunnable(() -> XMaterial.valueOf(object.get("material").getAsString())) || object.get("material").getAsString().startsWith("base64:"), "Invalid material");

        newDItem ditem;

        if (object.has("material"))    // Normal Item
            ditem = newDItem.of(WrappedMaterial.of(object.get("material").getAsString()).parseItem());
        else if (object.has("item"))   // Custom item
            ditem = newDItem.of(WrappedCustomItem.from(object).parseItem());
        else
            throw new RuntimeException("Invalid configuration");

        //if (ditem.isPotion() && object.has("potion"))         // TODO
        //   ditem.setMeta(gson.fromJson(object.get("potion"), PotionMeta.class));

        if (object.has("mob")) {
            Preconditions.checkArgument(Arrays.stream(EntityType.values()).anyMatch(entityType -> entityType.getName().equalsIgnoreCase(object.get("mob").getAsString())), "Invalid mob type");
            // ditem.setSpawnerType(EntityType.fromName(object.get("mob").getAsString())); // TODO
        }

        if (object.has("name")) ditem.setItem(ItemUtils.setName(ditem.getItem(), object.get("name").getAsString()));
        if (object.has("lore"))
            ditem.setItem(ItemUtils.setLore(ditem.getItem(), (List<String>) gson.fromJson(object.get("lore"), stringListToken.getType())));
        if (object.has("rarity")) ditem.setRarity(dRarity.fromKey(object.get("rarity").getAsString()));
        if (object.has("econ")) ditem.setEcon(gson.fromJson(object.get("econ").getAsJsonObject(), economy.class));
        if (object.has("buyPrice")) ditem.setBuyPrice(gson.fromJson(object.get("buyPrice"), dPrice.class));
        if (object.has("sellPrice")) ditem.setSellPrice(gson.fromJson(object.get("sellPrice"), dPrice.class));
        if (object.has("buyPerms")) ditem.setBuyPerms(gson.fromJson(object.get("buyPerms"), stringListToken.getType()));
        if (object.has("sellPerms"))
            ditem.setSellPerms(gson.fromJson(object.get("sellPerms"), stringListToken.getType()));
        if (object.has("enchantments")) {
            List<WrappedEnchantment> enchants = gson.fromJson(object.get("enchantments"), enchantsListToken.getType());
            enchants.forEach(enchant -> ditem.setItem(ItemUtils.addEnchant(ditem.getItem(), enchant.getEnchant(), enchant.getLevel())));
        }
        if (object.has("commands")) ditem.setCommands(gson.fromJson(object.get("commands"), stringListToken.getType()));
        if (object.has("quantity")) ditem.setItemQuantity(object.get("quantity").getAsInt());
        if (object.has("stock")) ditem.setStock(gson.fromJson(object.get("stock"), dStock.class));
        if (object.has("confirm_gui")) ditem.setConfirmGui(object.get("confirm_gui").getAsBoolean());
        if (object.has("bundle")) ditem.setBundle(gson.fromJson(object.get("bundle"), stringListToken.getType()));
        if (object.has("nbt")) ditem.setNBT(object.get("nbt").getAsJsonObject());
        if (object.has("unbreakable") && object.get("unbreakable").getAsBoolean())
            ditem.setItem(ItemUtils.setUnbreakable(ditem.getItem()));

        if (object.has("flags")) {
            List<String> flags = gson.fromJson(object.get("flags"), stringListToken.getType());
            flags.forEach(s -> {
                Preconditions.checkArgument(Utils.testRunnable(() -> ItemFlag.valueOf(s.toUpperCase())), "Incorrect flag " + s);
                ditem.setItem(ItemUtils.addItemFlags(ditem.getItem(), ItemFlag.valueOf(s.toUpperCase())));
            });
        }

        return ditem;
    }

    /**
     * Utils
     **/

    private List<WrappedEnchantment> wrapEnchants(Map<Enchantment, Integer> enchants) {
        return enchants.entrySet().stream()
                .map(entry -> WrappedEnchantment.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}

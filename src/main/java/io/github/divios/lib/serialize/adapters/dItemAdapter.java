package io.github.divios.lib.serialize.adapters;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.economies.Economy;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.rarities.Rarity;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.serialize.wrappers.*;
import io.github.divios.lib.serialize.wrappers.customitem.CustomItemFactory;
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
public class dItemAdapter implements JsonSerializer<dItem>, JsonDeserializer<dItem> {

    private static final TypeToken<List<String>> stringListToken = new TypeToken<List<String>>() {
    };
    private static final TypeToken<List<WrappedEnchantment>> enchantsListToken = new TypeToken<List<WrappedEnchantment>>() {
    };

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(WrappedEnchantment.class, new enchantmentAdapter())
            .registerTypeHierarchyAdapter(dStock.class, new dStockAdapter())
            .registerTypeHierarchyAdapter(Economy.class, new economyAdapter())
            .registerTypeAdapter(dPrice.class, new dPriceAdapter())
            .registerTypeHierarchyAdapter(PotionMeta.class, new potionEffectsAdapter())
            .create();

    @Override
    public JsonElement serialize(dItem dItem, Type type, JsonSerializationContext jsonSerializationContext) {
        boolean customItemFlag = false;
        JsonObject merchant = new JsonObject();

        ItemStack item = dItem.getItem();

        if (CustomItemFactory.isCustomItem(item)) {
            customItemFlag = true;
            merchant.add("item", CustomItemFactory.toJson(item));
        } else {
            if (ItemUtils.getMetadata(item).hasDisplayName())
                merchant.addProperty("name", Utils.JTEXT_PARSER.unParse(ItemUtils.getName(item)));

            List<String> lore = ItemUtils.getLore(dItem.getItem());
            if (!lore.isEmpty()) merchant.add("lore", gson.toJsonTree(Utils.JTEXT_PARSER.unParse(lore)));

            merchant.addProperty("material", WrappedMaterial.getMaterial(item));

            if (ItemUtils.getMaterial(item) == XMaterial.SPAWNER.parseMaterial())
                merchant.addProperty("mob", WrapperSpawnerItem.getSpawnerName(item));
        }

        if (item.getAmount() > 1) merchant.addProperty("quantity", item.getAmount());
        merchant.add("buyPrice", gson.toJsonTree(dItem.getDBuyPrice()));
        merchant.add("sellPrice", gson.toJsonTree(dItem.getDSellPrice()));
        if (dItem.hasStock())
            merchant.add("stock", gson.toJsonTree(dItem.getDStock()));
        if (!item.getEnchantments().isEmpty() && !customItemFlag)
            merchant.add("enchantments", gson.toJsonTree(wrapEnchants(item.getEnchantments())));
        if (dItem.getCommands() != null) merchant.add("commands", gson.toJsonTree(dItem.getCommands()));
        if (dItem.getBuyPerms() != null) merchant.add("buyPerms", gson.toJsonTree(dItem.getBuyPerms()));
        if (dItem.getSellPerms() != null) merchant.add("sellPerms", gson.toJsonTree(dItem.getSellPerms()));
        merchant.addProperty("rarity", dItem.getRarity().getId());
        merchant.add("econ", gson.toJsonTree(dItem.getEcon()));
        if (dItem.isStaticSlot()) merchant.addProperty("static", dItem.getSlot());
        merchant.addProperty("confirm_gui", dItem.isConfirmGui());
        if (dItem.getBundle() != null && !customItemFlag) merchant.add("bundle", gson.toJsonTree(dItem.getBundle()));

        if (ReflectionUtils.VER >= 12 && ItemUtils.getMetadata(item).isUnbreakable() && !customItemFlag)
            merchant.addProperty("unbreakable", true);

        if (XPotion.canHaveEffects(item.getType()) && !customItemFlag) {
            merchant.add("potion", gson.toJsonTree(ItemUtils.getPotionMeta(item), PotionMeta.class));
        }

        List<String> flags;
        if (!(flags = WrappedItemFlags.of(item).getFlags()).isEmpty() && !customItemFlag)
            merchant.add("flags", gson.toJsonTree(flags, stringListToken.getType()));

        WrappedNBT nbt;
        if (!(nbt = WrappedNBT.valueOf(dItem.getItem())).isEmpty() && !customItemFlag)
            merchant.addProperty("nbt", nbt.getNbt());

        return merchant;
    }

    @Override
    public dItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Preconditions.checkArgument(object.has("material") || object.has("item"), "An item needs a material/item field");

        dItem ditem;

        if (object.has("material"))    // Normal Item
            ditem = dItem.of(WrappedMaterial.of(object.get("material").getAsString()).parseItem());
        else if (object.has("item"))   // Custom item
            ditem = dItem.of(CustomItemFactory.fromJson(object.get("item").getAsJsonObject()));
        else
            throw new RuntimeException("Invalid configuration");

        if (ReflectionUtils.VER >= 9 &&
                XPotion.canHaveEffects(ItemUtils.getMaterial(ditem.getItem())) && object.has("potion")) {
            ItemStack potionItem = ditem.getItem();
            potionItem.setItemMeta(gson.fromJson(object.get("potion"), PotionMeta.class));
            ditem.setItem(potionItem);
        }

        if (object.has("mob")) {
            Preconditions.checkArgument(Arrays.stream(EntityType.values()).anyMatch(entityType -> entityType.getName().equalsIgnoreCase(object.get("mob").getAsString())), "Invalid mob type");
            ItemStack spawner = WrapperSpawnerItem.setSpawnerMeta(ditem.getItem(), EntityType.fromName(object.get("mob").getAsString()));
            ditem.setItem(spawner);
        }

        if (object.has("name"))
            ditem.setItem(ItemUtils.setName(ditem.getItem(), Utils.JTEXT_PARSER.parse(object.get("name").getAsString())));
        if (object.has("lore"))
            ditem.setItem(ItemUtils.setLore(ditem.getItem(), Utils.JTEXT_PARSER.parse((List<String>) gson.fromJson(object.get("lore"), stringListToken.getType()))));
        if (object.has("rarity")) ditem.setRarity(DailyShop.get().getRarityManager().get(object.get("rarity").getAsString()).orElse(Rarity.UNAVAILABLE));
        if (object.has("econ")) ditem.setEcon(gson.fromJson(object.get("econ").getAsJsonObject(), Economy.class));
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
        if (object.has("static")) {
            ditem.setStaticSlot(true);
            ditem.setSlot(object.get("static").getAsInt());
        }
        if (object.has("confirm_gui")) ditem.setConfirmGui(object.get("confirm_gui").getAsBoolean());
        if (object.has("bundle")) ditem.setBundle(gson.fromJson(object.get("bundle"), stringListToken.getType()));

        if (object.has("nbt"))
            ditem.setItem(WrappedNBT.mergeNBT(ditem.getItem(), object.get("nbt")));

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

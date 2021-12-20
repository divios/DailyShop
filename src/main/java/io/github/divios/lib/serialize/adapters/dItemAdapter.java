package io.github.divios.lib.serialize.adapters;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.economies.economy;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dRarity;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.serialize.wrappers.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class dItemAdapter implements JsonSerializer<dItem>, JsonDeserializer<dItem> {

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
    public JsonElement serialize(dItem dItem, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject merchant = new JsonObject();

        String name = ItemUtils.getName(dItem.getRawItem());
        if (!name.isEmpty()) merchant.addProperty("name", FormatUtils.unColor(name));

        List<String> lore = ItemUtils.getLore(dItem.getRawItem());
        if (!lore.isEmpty()) merchant.add("lore", gson.toJsonTree(ItemUtils.getLore(dItem.getRawItem())));

        if (WrappedCustomItem.isCustomItem(dItem))
            merchant.add("item", WrappedCustomItem.serializeCustomItem(dItem));
        else {
            merchant.addProperty("material", WrappedMaterial.getMaterial(dItem));
            if (dItem.isSpawner()) merchant.addProperty("mob", dItem.getSpawnerType().name());
        }

        dItem.getSetItems().ifPresent(integer -> merchant.addProperty("quantity", integer));
        merchant.add("buyPrice", gson.toJsonTree(dItem.getBuyPrice().get()));
        merchant.add("sellPrice", gson.toJsonTree(dItem.getSellPrice().get()));
        if (dItem.hasStock()) merchant.add("stock", gson.toJsonTree(dItem.getStock()));
        if (!dItem.getEnchantments().isEmpty()) merchant.add("enchantments", gson.toJsonTree(wrapEnchants(dItem.getEnchantments())));
        dItem.getCommands().ifPresent(strings -> merchant.add("commands", gson.toJsonTree(strings)));
        dItem.getPermsBuy().ifPresent(strings -> merchant.add("buyPerms", gson.toJsonTree(strings)));
        dItem.getPermsSell().ifPresent(strings -> merchant.add("sellPerms", gson.toJsonTree(strings)));
        merchant.addProperty("rarity", dItem.getRarity().getKey());
        merchant.add("econ", gson.toJsonTree(dItem.getEconomy()));
        merchant.addProperty("confirm_gui", dItem.isConfirmGuiEnabled());

        if (dItem.isUnbreakble()) merchant.addProperty("unbreakable", true);

        if (dItem.isPotion()) {
            merchant.add("potion", gson.toJsonTree(ItemUtils.getPotionMeta(dItem.getItem()), PotionMeta.class));
        }

        List<String> flags;
        if (!(flags = WrappedItemFlags.of(dItem).getFlags()).isEmpty())
            merchant.add("flags", gson.toJsonTree(flags, stringListToken.getType()));

        WrappedNBT nbt;
        if (!(nbt = WrappedNBT.valueOf(dItem.getNBT())).isEmpty())
            merchant.add("nbt", nbt.getNbt());

        return merchant;
    }

    @Override
    public dItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Preconditions.checkArgument(object.has("material") || object.has("item"), "An item needs a material");
        Preconditions.checkArgument(Utils.testRunnable(() -> XMaterial.valueOf(object.get("material").getAsString())) || object.get("material").getAsString().startsWith("base64:"), "Invalid material");

        dItem ditem;

        if (object.has("material"))    // Normal Item
            ditem = WrappedMaterial.of(object.get("material").getAsString()).parseItem();
        else if (object.has("item"))   // Custom item
            ditem = WrappedCustomItem.from(object).parseItem();
        else
            throw new RuntimeException("Invalid configuration");

        if (ditem.isPotion() && object.has("potion"))
            ditem.setMeta(gson.fromJson(object.get("potion"), PotionMeta.class));

        if (object.has("mob")) {
            Preconditions.checkArgument(Arrays.asList(EntityType.values()).stream().anyMatch(entityType -> entityType.getName().equalsIgnoreCase(object.get("mob").getAsString())), "Invalid mob type");
            ditem.setSpawnerType(EntityType.fromName(object.get("mob").getAsString()));
        }

        if (object.has("name")) ditem.setDisplayName(object.get("name").getAsString());
        if (object.has("lore")) ditem.setLore(gson.fromJson(object.get("lore"), stringListToken.getType()));
        if (object.has("rarity")) ditem.setRarity(dRarity.fromKey(object.get("rarity").getAsString()));
        if (object.has("econ")) ditem.setEconomy(gson.fromJson(object.get("econ").getAsJsonObject(), economy.class));
        if (object.has("buyPrice")) ditem.setBuyPrice(gson.fromJson(object.get("buyPrice"), dPrice.class));
        if (object.has("sellPrice")) ditem.setSellPrice(gson.fromJson(object.get("sellPrice"), dPrice.class));
        if (object.has("buyPerms")) ditem.setPermsBuy(gson.fromJson(object.get("buyPerms"), stringListToken.getType()));
        if (object.has("sellPerms"))
            ditem.setPermsSell(gson.fromJson(object.get("sellPerms"), stringListToken.getType()));
        if (object.has("enchantments")) {
            List<WrappedEnchantment> enchants = gson.fromJson(object.get("enchantments"), enchantsListToken.getType());
            enchants.forEach(enchant -> ditem.addEnchantments(enchant.getEnchant(), enchant.getLevel()));
        }
        if (object.has("commands")) ditem.setCommands(gson.fromJson(object.get("commands"), stringListToken.getType()));
        if (object.has("quantity")) ditem.setSetItems(object.get("quantity").getAsInt());
        if (object.has("stock")) ditem.setStock(gson.fromJson(object.get("stock"), dStock.class));
        if (object.has("confirm_gui")) ditem.setConfirm_gui(object.get("confirm_gui").getAsBoolean());
        if (object.has("nbt")) ditem.setNBT(object.get("nbt").getAsJsonObject());
        if (object.has("unbreakable") && object.get("unbreakable").getAsBoolean()) ditem.setUnbreakable();

        if (object.has("flags")) {
            List<String> flags = gson.fromJson(object.get("flags"), stringListToken.getType());
            flags.forEach(s -> {
                Preconditions.checkArgument(Utils.testRunnable(() -> ItemFlag.valueOf(s.toUpperCase())), "Incorrect flag " + s);
                ditem.setFlag(ItemFlag.valueOf(s.toUpperCase()));
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

package io.github.divios.lib.storage.parser;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.storage.parser.states.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Deserializer {

    private static final String shopPath = "shop.";

    public static dShop deserializeShop(File file) {
        return deserializeShop(YamlConfiguration.loadConfiguration(file));
    }

    public static dShop deserializeShop(YamlConfiguration yaml) {

        String shopID = yaml.getString("id");

        if (shopID == null || shopID.isEmpty()) {
            throw new RuntimeException("Shop id cannot be null or empty");
        }

        dShopState.builder()
                .withId(shopID)
                .withInvState(getDShopInv(yaml))
                .withItemsCollect(getShopItems(yaml))
                .build();


        return shop;
    }

    private static dShopInvState getDShopInv(YamlConfiguration yaml) {
        dShopInvState.dShopInvStateBuilder dShopInvStateBuilder = dShopInvState.builder()
                .withTitle(yaml.getString(shopPath + "title"))
                .withSize(yaml.getInt(shopPath + "size"));

        Map<UUID, dButtonState> items = new HashMap<>();

        yaml.getConfigurationSection(shopPath + "display_items").getKeys(false).forEach(id -> {
            String innerPath = shopPath + id;
            try {
                dButtonState buttonState = dButtonState.builder()
                        .withName(yaml.getString(innerPath + "name"))
                        .withMaterial(yaml.getString(innerPath + "material"))
                        .withLore(yaml.getStringList(innerPath + "lore"))
                        .withQuantity(yaml.getInt(innerPath + "quantity"))
                        .withSlot(yaml.getInt(innerPath + "slot"))
                        .withAction(yaml.getString(innerPath + "action"))
                        .withEnchantments(yaml.getStringList(innerPath + "enchantments"))
                        .withNbt(yaml.getStringList(yaml.getString(innerPath + "nbt")))
                        .build();

                items.put(UUID.fromString(id), buttonState);
            } catch (Exception e) {
                Log.info("There was a problem parsing the item with id: " + id);
                Log.info(e.getMessage());
            }
        });

        return dShopInvStateBuilder
                .withDisplay_items(items)
                .build();
    }

    private static Map<UUID, dItemState> getShopItems(YamlConfiguration yaml) {
        String innerPath = shopPath + "items";
        Map<UUID, dItemState> items = new LinkedHashMap<>();
        yaml.getConfigurationSection(innerPath).getKeys(false).forEach(id ->  {
            try {
                dItemState item = dItemState.builder()
                        .withName(yaml.getString(innerPath + "name"))
                        .withLore(yaml.getStringList(innerPath + "lore"))
                        .withMaterial(yaml.getString(innerPath + "material"))
                        .withQuantity(yaml.getInt(innerPath + "quantity"))
                        .withDailyShop_meta(getDailyItemMeta(yaml, id))
                        .withEnchantments(yaml.getConfigurationSection(innerPath + "enchantments").getValues(true))
                        .withNbt(yaml.getString(innerPath + "nbt"))
                        .build();

                items.put(UUID.fromString(id), item);
            } catch (Exception e) {
                Log.info("There was an error parsing the item of id " + id);
                Log.info(e.getMessage());
            }
        });

        return items;
    }

    private static dItemMetaState getDailyItemMeta(YamlConfiguration yaml, String id) {
        String innerPath = shopPath + "items" + id + "dailyShop_meta";
        return dItemMetaState.builder()
                .withBuyPrice(yaml.getString(innerPath + "buyPrice"))
                .withSellPrice(yaml.getString(innerPath + "sellPrice"))
                .withConfirm_gui(yaml.getBoolean(innerPath + "confirm_gui"))
                .withRarity(yaml.getString(innerPath + "rarity"))
                .withCommands(yaml.getStringList(innerPath + "commands"))
                .withBundleStr(yaml.getStringList(innerPath + "bundle"))
                .withSet(yaml.getInt(innerPath + "set"))
                .withStock(yaml.getString(innerPath + "stock"))
                .withEcon(yaml.getString(innerPath + "econ"))
                .withBuyPerms(yaml.getStringList(innerPath + "buyPerms"))
                .withSellPerms(yaml.getStringList(innerPath + "sellPerms"))
                .build();
    }

}

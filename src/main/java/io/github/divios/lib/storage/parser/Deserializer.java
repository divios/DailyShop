package io.github.divios.lib.storage.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.storage.parser.states.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Deserializer {

    private static final String shopPath = "shop.";

    protected static dShopState deserializeShop(File file) {
        return deserializeShop(YamlConfiguration.loadConfiguration(file));
    }

    protected static dShopState deserializeShop(YamlConfiguration yaml) {

        String shopID = yaml.getString("id");

        if (shopID == null || shopID.isEmpty()) {
            throw new RuntimeException("Shop id cannot be null or empty");
        }

        return getShopState(yaml, shopID);
    }

    @NotNull
    private static dShopState getShopState(YamlConfiguration yaml, String shopID) {
        return dShopState.builder()
                .withId(shopID)
                .withInvState(getdShopInv(yaml))
                .withItemsCollect(getShopItems(yaml))
                .build();
    }

    private static dShopInvState getdShopInv(YamlConfiguration yaml) {
        dShopInvState.dShopInvStateBuilder dShopInvStateBuilder = dShopInvState.builder()
                .withTitle(yaml.getString(shopPath + "title"))
                .withSize(yaml.getInt(shopPath + "size"));

        return dShopInvStateBuilder
                .withDisplay_items(getDButtons(yaml))
                .build();
    }

    @NotNull
    private static List<dButtonState> getDButtons(YamlConfiguration yaml) {
        List<dButtonState> items = new ArrayList<>();

        yaml.getConfigurationSection(shopPath + "display_items").getKeys(false).forEach(id -> {
            String innerPath = shopPath + "display_items." + id + ".";
            try {
                dButtonState buttonState = dButtonState.builder()
                        .withID(id)
                        .withName(yaml.getString(innerPath + "name"))
                        .withMaterial(yaml.getString(innerPath + "material"))
                        .withLore(yaml.getStringList(innerPath + "lore"))
                        .withQuantity(yaml.getInt(innerPath + "quantity"))
                        .withSlot(yaml.getInt(innerPath + "slot"))
                        .withAction(yaml.getString(innerPath + "action"))
                        .withEnchantments(yaml.getStringList(innerPath + "enchantments"))
                        //.withNbt((JsonObject) new JsonParser().parse(yaml.getString(innerPath + "nbt")))
                        .build();

                items.add(buttonState);
            } catch (Exception e) {
                Log.info("There was a problem parsing the display item with id: " + id);
                //Log.info(e.getMessage());
                e.printStackTrace();
            }
        });
        return items;
    }

    private static List<dItemState> getShopItems(YamlConfiguration yaml) {
        List<dItemState> items = new ArrayList<>();
        yaml.getConfigurationSection("items").getKeys(false).forEach(id ->  {
            String innerPath = "items." + id + ".";
            try {
                dItemState item = dItemState.builder()
                        .withID(id)
                        .withName(yaml.getString(innerPath + "name"))
                        .withLore(yaml.getStringList(innerPath + "lore"))
                        .withMaterial(yaml.getString(innerPath + "material"))
                        .withQuantity(yaml.getInt(innerPath + "quantity"))
                        .withDailyShop_meta(getDailyItemMeta(yaml, id))
                        //.withEnchantments((Map<String, Integer>) yaml.getMapList(innerPath + "enchantments"))
                        //.withNbt((JsonObject) new JsonParser().parse(yaml.getString(innerPath + "nbt")))
                        .build();

                items.add(item);
            } catch (Exception e) {
                Log.info("There was an error parsing the item of id " + id);
                Log.info(e.getMessage());
            }
        });

        return items;
    }

    private static dItemMetaState getDailyItemMeta(YamlConfiguration yaml, String id) {
        String innerPath = "items." + id + ".dailyShop_meta.";
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

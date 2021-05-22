package io.github.divios.dailyrandomshop;

import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class conf_msg {

    private static final DRShop main = DRShop.getInstance();

    public static String
            PREFIX, VAULT_CUSTOM_NAME,
            BUY_GUI_TITLE, BUY_GUI_PAINTING_NAME, BUY_GUI_ARROW_NAME, BUY_GUI_ITEMS_LORE_PRICE, BUY_GUI_ITEMS_LORE_CURRENCY, BUY_GUI_ITEMS_LORE_RARITY, BUY_GUI_ITEMS_LORE_BUY_FOR,
            CONFIRM_GUI_NAME, CONFIRM_GUI_ADD_PANE, CONFIRM_GUI_REMOVE_PANE, CONFIRM_GUI_CONFIRM_PANE, CONFIRM_GUI_RETURN_NAME,
            SELL_GUI_TITLE, SELL_PAINTING_NAME, SELL_ARROW_NAME, SELL_ITEM_NAME,
            CONFIRM_MENU_YES, CONFIRM_MENU_NO,
            SETTINGS_GUI_TITLE, SETTINGS_DAILY_ITEM, SETTINGS_SELL_ITEM,
            ADD_ITEMS_TITLE, ADD_ITEMS_FROM_ZERO, ADD_ITEMS_FROM_EXISTING, ADD_ITEMS_RETURN,
            DAILY_ITEMS_MENU_TITLE, DAILY_ITEMS_MENU_ADD, DAILY_ITEMS_MENU_NEXT, DAILY_ITEMS_MENU_PREVIOUS, DAILY_ITEMS_MENU_RETURN, DAILY_ITEMS_MENU_ANVIL_TITLE, DAILY_ITEMS_MENU_ANVIL_DEFAULT_TEXT,
            SELL_ITEMS_MENU_TITLE, SELL_ITEMS_MENU_NEXT, SELL_ITEMS_MENU_PREVIOUS, SELL_ITEMS_MENU_RETURN, SELL_ITEMS_MENU_ANVIL_TITLE, SELL_ITEMS_MENU_ANVIL_DEFAULT_TEXT,
            CUSTOMIZE_GUI_TITLE, CUSTOMIZE_CRAFT, CUSTOMIZE_RETURN, CUSTOMIZE_MATERIAL, CUSTOMIZE_RENAME, CUSTOMIZE_LORE, CUSTOMIZE_ENCHANTS, CUSTOMIZE_AMOUNT, CUSTOMIZE_ENABLE_COMMANDS, CUSTOMIZE_CHANGE_COMMANDS, CUSTOMIZE_TOGGLE_ENCHANTS,
            CUSTOMIZE_RENAME_ANVIL_TITLE, CUSTOMIZE_RENAME_ANVIL_DEFAULT_TEXT, CUSTOMIZE_CHANGE_LORE_TITLE, CUSTOMIZE_CHANGE_LORE_DEFAULT_TEXT, CUSTOMIZE_SEARCH, CUSTOMIZE_CANCEL_SEARCH, CUSTOMIZE_DURABILITY, CUSTOMIZE_BUNDLE,
            CUSTOMIZE_ADD_COMMANDS_TITLE, CUSTOMIZE_ADD_COMMANDS_DEFAULT_TEXT,
            CUSTOMIZE_TOGGLE_ATTRIBUTES, CUSTOMIZE_TOGGLE_EFFECTS, CUSTOMIZE_CHANGE_ECON, CUSTOMIZE_CHANGE_CONFIRM_GUI, CUSTOMIZE_PERMS, CUSTOMIZE_SET ,CUSTOMIZE_UNAVAILABLE;;

    public static List<String> BUY_GUI_PAINTING_LORE, BUY_GUI_ARROW_LORE,
            SELL_PAINTING_LORE, SELL_ARROW_LORE,
            SETTINGS_DAILY_ITEM_LORE, SETTINGS_SELL_ITEM_LORE,
            ADD_ITEMS_FROM_ZERO_LORE, ADD_ITEMS_FROM_EXISTING_LORE, ADD_ITEMS_RETURN_LORE,
            DAILY_ITEMS_MENU_ITEMS_LORE, DAILY_ITEMS_MENU_ADD_LORE,
            SELL_ITEMS_MENU_ITEMS_LORE, CONFIRM_GUI_RETURN_PANE_LORE, CUSTOMIZE_SEARCH_LORE, CUSTOMIZE_CANCEL_SEARCH_LORE,
            CUSTOMIZE_CRAFT_LORE, CUSTOMIZE_RETURN_LORE, CUSTOMIZE_RENAME_LORE, CUSTOMIZE_MATERIAL_LORE, CUSTOMIZE_LORE_LORE, CUSTOMIZE_ENCHANTS_LORE, CUSTOMIZE_AMOUNT_LORE, CUSTOMIZE_AMOUNT_ENABLE_LORE, CUSTOMIZE_ENABLE_COMMANDS_LORE,
            CUSTOMIZE_CHANGE_COMMANDS_LORE, CUSTOMIZE_TOGGLE_ENCHANTS_LORE, CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE, CUSTOMIZE_TOGGLE_EFFECTS_LORE, CUSTOMIZE_CHANGE_ECON_LORE ,CUSTOMIZE_DURABILITY_LORE, CUSTOMIZE_BUNDLE_LORE,
            CUSTOMIZE_CHANGE_RARITY_LORE, CUSTOMIZE_CHANGE_CONFIRM_GUI_LORE, CUSTOMIZE_ENABLE_PERMS_LORE, CUSTOMIZE_CHANGE_PERMS_LORE, CUSTOMIZE_ENABLE_SET_LORE, CUSTOMIZE_CHANGE_SET_LORE, RARITY_NAMES;

    public static String MSG_OPEN_SHOP, MSG_BUY_ITEM, MSG_SELL_ITEMS, MSG_NOT_ENOUGH_MONEY, MSG_INVENTORY_FULL,
            MSG_INVALID_ITEM, MSG_NOT_PERMS, MSG_NOT_PERMS_ITEM, MSG_ERROR_ITEM_HAND, MSG_ERROR_PRICE,
            MSG_ERROR_ADDING_ITEM, MSG_ITEM_ADDED, MSG_NEW_DAILY_ITEMS, MSG_SELL_ITEMS_GUI_EMPTY,
            MSG_ADDED_ITEM, MSG_REMOVED_ITEM, MSG_ITEM_ALREADY_ON_SALE, MSG_NOT_IN_STOCK, MSG_TIMER_EXPIRED,
            MSG_ADD_ITEM_TITLE, MSG_ADD_ITEM_SUBTITLE, MSG_NOT_INTEGER, MSG_RELOAD;

    public static int N_DAILY_ITEMS, TIMER;
    public static double DEFAULT_PRICE;

    public static boolean ENABLE_SELL_GUI, ENABLE_RARITY, DEBUG;

    public static void init() {
        main.reloadConfig();
        createLocales();

        File file = new File(main.getDataFolder() + File.separator + "locales" +
                File.separator + main.getConfig().getString("language", "en_US") + ".yml");

        if (!file.exists()) file = new File(main.getDataFolder() +
                File.separator + "locales" + File.separator + "en_US");

        FileConfiguration yamlFile = YamlConfiguration.loadConfiguration(file);

        PREFIX = utils.formatString(main.getConfig().getString("prefix", "&6&lDailyShop > "));
        VAULT_CUSTOM_NAME = utils.formatString(main.getConfig().getString("vault-currency-name", "&7Vault"));
        DEFAULT_PRICE = main.getConfig().getDouble("default-price", 500.0);
        TIMER = main.getConfig().getInt("timer-duration", 86400);
        ENABLE_SELL_GUI = main.getConfig().getBoolean("enable-sell-gui", true);
        ENABLE_RARITY = main.getConfig().getBoolean("enable-rarity", true);
        N_DAILY_ITEMS = main.getConfig().getInt("number-of-daily-items", 14);
        DEBUG = main.getConfig().getBoolean("debug", false);
        if (N_DAILY_ITEMS <= 0 || N_DAILY_ITEMS > 36) N_DAILY_ITEMS = 14;

        BUY_GUI_TITLE = utils.formatString(yamlFile.getString("daily-shop-gui-name", "&6&lDailyShop"));
        BUY_GUI_PAINTING_NAME = utils.formatString(yamlFile.getString("daily-shop-gui-painting-name", "&c&lWhat is this?"));
        BUY_GUI_PAINTING_LORE = yamlFile.getStringList("daily-shop-gui-painting-lore");
        BUY_GUI_ITEMS_LORE_PRICE = utils.formatString(yamlFile.getString("daily-items-lore-price", "&6Price: &7{price}"));
        BUY_GUI_ITEMS_LORE_CURRENCY = utils.formatString(yamlFile.getString("daily-items-lore-currency", "&6Currency: &7{currency}"));
        BUY_GUI_ITEMS_LORE_RARITY = utils.formatString(yamlFile.getString("daily-items-lore-rarity", "&6Rarity: &7{rarity}"));
        BUY_GUI_ITEMS_LORE_BUY_FOR = utils.formatString(yamlFile.getString("daily-items-lore-buy-for", "&6Buy for &7{price}"));
        BUY_GUI_ARROW_NAME = utils.formatString(yamlFile.getString("daily-shop-to-sell-name", "&c&lVisit Market"));
        BUY_GUI_ARROW_LORE = yamlFile.getStringList("daily-shop-to-sell-lore");

        CONFIRM_GUI_NAME = utils.formatString(yamlFile.getString("confirm-gui-name", "&2&lConfirm Purchase"));
        CONFIRM_GUI_ADD_PANE = utils.formatString(yamlFile.getString("confirm-gui-add-pane", "&aAdd"));
        CONFIRM_GUI_REMOVE_PANE = utils.formatString(yamlFile.getString("confirm-gui-remove-pane", "&cRemove"));
        CONFIRM_GUI_CONFIRM_PANE = utils.formatString(yamlFile.getString("confirm-gui-confirm-pane", "&aConfirm"));
        CONFIRM_GUI_RETURN_NAME = utils.formatString(yamlFile.getString("confirm-gui-return-name", "&cReturn"));
        CONFIRM_GUI_RETURN_PANE_LORE = yamlFile.getStringList("confirm-gui-return-pane-lore");

        SELL_GUI_TITLE = utils.formatString(yamlFile.getString("sell-gui-name", "&3&lMarket"));
        SELL_PAINTING_NAME = utils.formatString(yamlFile.getString("sell-gui-painting-name", "&3&lWhat is this?"));
        SELL_PAINTING_LORE = yamlFile.getStringList("sell-gui-painting-lore");
        SELL_ITEM_NAME = utils.formatString(yamlFile.getString("sell-item-name"));
        SELL_ARROW_NAME = utils.formatString(yamlFile.getString("sell-to-daily-shop-name", "&3&lReturn to daily shop"));
        SELL_ARROW_LORE = yamlFile.getStringList("sell-to-daily-shop-lore");

        CONFIRM_MENU_YES = utils.formatString(yamlFile.getString("confirm-menu-yes", "&a&lConfirm"));
        CONFIRM_MENU_NO = utils.formatString(yamlFile.getString("confirm-menu-no", "&c&lCancel"));

        SETTINGS_GUI_TITLE = utils.formatString(yamlFile.getString("settings-gui-title", "&6&lSettings"));
        SETTINGS_DAILY_ITEM = utils.formatString(yamlFile.getString("settings-daily-item", "&6&lDaily items"));
        SETTINGS_DAILY_ITEM_LORE = yamlFile.getStringList("settings-daily-items-lore");
        SETTINGS_SELL_ITEM = utils.formatString(yamlFile.getString("settings-sell-items", "&a&lSell items"));
        SETTINGS_SELL_ITEM_LORE = yamlFile.getStringList("settings-sell-items-lore");

        ADD_ITEMS_TITLE = utils.formatString(yamlFile.getString("add-items-title", "&3&lCreate Item"));
        ADD_ITEMS_FROM_ZERO = utils.formatString(yamlFile.getString("add-items-from-zero", "&c&lCreate item from zero"));
        ADD_ITEMS_FROM_ZERO_LORE = yamlFile.getStringList("add-items-from-zero-lore");
        ADD_ITEMS_FROM_EXISTING = utils.formatString(yamlFile.getString("add-items-from-existing", "&a&lCreate item from existing"));
        ADD_ITEMS_FROM_EXISTING_LORE = yamlFile.getStringList("add-items-from-existing-lore");
        ADD_ITEMS_RETURN = utils.formatString(yamlFile.getString("add-items-return", "&c&lReturn"));
        ADD_ITEMS_RETURN_LORE = yamlFile.getStringList("add-items-return-lore");

        DAILY_ITEMS_MENU_TITLE = utils.formatString(yamlFile.getString("daily-items-menu_title", "&6&lDaily items Manager"));
        DAILY_ITEMS_MENU_ADD = utils.formatString(yamlFile.getString("daily-items-menu-add", "&3&lAdd"));
        DAILY_ITEMS_MENU_ADD_LORE = yamlFile.getStringList("daily_items_menu_add_lore");
        DAILY_ITEMS_MENU_NEXT = utils.formatString(yamlFile.getString("daily-items-menu_next", "&6&lNext"));
        DAILY_ITEMS_MENU_PREVIOUS = utils.formatString(yamlFile.getString("daily-items-menu_previous", "&6&lPrevious"));
        DAILY_ITEMS_MENU_RETURN = utils.formatString(yamlFile.getString("daily-items-menu_return", "&c&lReturn"));
        DAILY_ITEMS_MENU_ITEMS_LORE = yamlFile.getStringList("daily-items-menu-items-lore");
        DAILY_ITEMS_MENU_ANVIL_TITLE = utils.formatString(yamlFile.getString("daily-items-anvil-title", "&6&lSet Price"));
        DAILY_ITEMS_MENU_ANVIL_DEFAULT_TEXT = utils.formatString(yamlFile.getString("daily-items-anvil-default-text", "Price"));

        SELL_ITEMS_MENU_TITLE = utils.formatString(yamlFile.getString("sell-items-menu_title", "&6&lsell items Manager"));
        SELL_ITEMS_MENU_NEXT = utils.formatString(yamlFile.getString("sell-items-menu_next", "&6&lNext"));
        SELL_ITEMS_MENU_PREVIOUS = utils.formatString(yamlFile.getString("sell-items-menu_previous", "&6&lPrevious"));
        SELL_ITEMS_MENU_RETURN = utils.formatString(yamlFile.getString("sell-items-menu_return", "&c&lReturn"));
        SELL_ITEMS_MENU_ITEMS_LORE = yamlFile.getStringList("sell-items-menu-items-lore");
        SELL_ITEMS_MENU_ANVIL_TITLE = utils.formatString(yamlFile.getString("sell-items-anvil-title", "&6&lSet Price"));
        SELL_ITEMS_MENU_ANVIL_DEFAULT_TEXT = utils.formatString(yamlFile.getString("sell-items-anvil-default-text", "Price"));

        CUSTOMIZE_GUI_TITLE = utils.formatString(yamlFile.getString("customize_menu_title", "&a&lCustomize Item"));
        CUSTOMIZE_CRAFT = utils.formatString(yamlFile.getString("customize_craft", "&a&lCraft item"));
        CUSTOMIZE_CRAFT_LORE = yamlFile.getStringList("customize_craft_lore");
        CUSTOMIZE_RETURN = utils.formatString(yamlFile.getString("customize_return", "&c&lReturn"));
        CUSTOMIZE_RETURN_LORE = yamlFile.getStringList("customize_return_lore");
        CUSTOMIZE_CHANGE_ECON = utils.formatString(yamlFile.getString("customize_change_econ", "&f&lChange currency"));
        CUSTOMIZE_CHANGE_ECON_LORE = yamlFile.getStringList("customize_change_econ_lore");
        CUSTOMIZE_CHANGE_CONFIRM_GUI = utils.formatString(yamlFile.getString("customize_change_confirm_gui", "&aEnable/disable confirm Gui"));
        CUSTOMIZE_CHANGE_CONFIRM_GUI_LORE = yamlFile.getStringList("customize_change_confirm_gui_lore");
        CUSTOMIZE_PERMS = utils.formatString(yamlFile.getString("customize_perms", "&f&lEdit permissions"));
        CUSTOMIZE_ENABLE_PERMS_LORE = yamlFile.getStringList("customize_enable_perms_lore");
        CUSTOMIZE_CHANGE_PERMS_LORE = yamlFile.getStringList("customize_change_perms_lore");
        CUSTOMIZE_SET = utils.formatString(yamlFile.getString("customize_set", "&f&lEdit set"));
        CUSTOMIZE_ENABLE_SET_LORE = yamlFile.getStringList("customize_enable_set_lore");
        CUSTOMIZE_CHANGE_SET_LORE = yamlFile.getStringList("customize_change_set_lore");
        CUSTOMIZE_CHANGE_RARITY_LORE = yamlFile.getStringList("customize_change_rarity_lore");
        RARITY_NAMES = yamlFile.getStringList("customize_rarity_names");
        CUSTOMIZE_RENAME = utils.formatString(yamlFile.getString("customize_rename", "&f&lRename"));
        CUSTOMIZE_RENAME_LORE = yamlFile.getStringList("customize_rename_lore");
        CUSTOMIZE_MATERIAL = utils.formatString(yamlFile.getString("customize_change_material", "&f&lChange Material"));
        CUSTOMIZE_MATERIAL_LORE = yamlFile.getStringList("customize_change_material_lore");
        CUSTOMIZE_SEARCH = utils.formatString(yamlFile.getString("customize_search", "&b&lSearch"));
        CUSTOMIZE_SEARCH_LORE = yamlFile.getStringList("customize_search_lore");
        CUSTOMIZE_CANCEL_SEARCH = utils.formatString(yamlFile.getString("customize_cancel_search", "&c&lCancel search"));
        CUSTOMIZE_CANCEL_SEARCH_LORE = yamlFile.getStringList("customize_cancel_search_lore");
        CUSTOMIZE_LORE = utils.formatString(yamlFile.getString("customize-change_lore", "&f&lChange Lore"));
        CUSTOMIZE_LORE_LORE = yamlFile.getStringList("customize_change_lore_lore");
        CUSTOMIZE_ENCHANTS = utils.formatString(yamlFile.getString("customize_change_enchants", "&f&lEdit enchantments"));
        CUSTOMIZE_ENCHANTS_LORE = yamlFile.getStringList("customize_change_enchants_lore");
        CUSTOMIZE_AMOUNT = utils.formatString(yamlFile.getString("customize_change_amount", "&f&lChange Amount"));
        CUSTOMIZE_AMOUNT_ENABLE_LORE = yamlFile.getStringList("customize_enable_amount_lore");
        CUSTOMIZE_BUNDLE = utils.formatString(yamlFile.getString("customize_bundle", "&f&lChange item Durability"));
        CUSTOMIZE_BUNDLE_LORE = yamlFile.getStringList("customize_bundle_lore");
        CUSTOMIZE_DURABILITY = utils.formatString(yamlFile.getString("customize_change-durability", "&f&lChange Bundle item"));
        CUSTOMIZE_DURABILITY_LORE = yamlFile.getStringList("customize_change-durability_lore");
        CUSTOMIZE_AMOUNT_LORE = yamlFile.getStringList("customize_change_amount_lore");
        CUSTOMIZE_ENABLE_COMMANDS = utils.formatString(yamlFile.getString("customize_enable_commands", "&f&lSet Item Reward as Commands"));
        CUSTOMIZE_ENABLE_COMMANDS_LORE = yamlFile.getStringList("customize_enable_commands_lore");
        CUSTOMIZE_CHANGE_COMMANDS = utils.formatString(yamlFile.getString("customize_change_commands", "&f&lSet commands to run"));
        CUSTOMIZE_CHANGE_COMMANDS_LORE = yamlFile.getStringList("customize_change_commands_lore");
        CUSTOMIZE_TOGGLE_ENCHANTS = utils.formatString(yamlFile.getString("customize_toggle_enchants", "&f&lMake enchant visible/invisible"));
        CUSTOMIZE_TOGGLE_ENCHANTS_LORE = yamlFile.getStringList("customize_toggle_enchants_lore");
        CUSTOMIZE_TOGGLE_ATTRIBUTES = utils.formatString(yamlFile.getString("customize_toggle_attributes", "&f&lMake attributes visible/invisible"));
        CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE = yamlFile.getStringList("customize_toggle_attributes_lore");
        CUSTOMIZE_TOGGLE_EFFECTS = utils.formatString(yamlFile.getString("customize_toggle_effects", "&f&lMake potion effects visible/invisible"));
        CUSTOMIZE_TOGGLE_EFFECTS_LORE = yamlFile.getStringList("customize_toggle_effects_lore");
        CUSTOMIZE_RENAME_ANVIL_TITLE = utils.formatString(yamlFile.getString("customize_rename_anvil_title", "&6&lInput the new lore"));
        CUSTOMIZE_RENAME_ANVIL_DEFAULT_TEXT = utils.formatString(yamlFile.getString("customize_rename_anvil_default_text", "Write the new name"));
        CUSTOMIZE_CHANGE_LORE_TITLE = utils.formatString(yamlFile.getString("customize_change_lore_anvil_title", "&6&lWrite lore"));
        CUSTOMIZE_CHANGE_LORE_DEFAULT_TEXT = utils.formatString(yamlFile.getString("customize_change_lore_default_text", "Write lore"));
        CUSTOMIZE_ADD_COMMANDS_TITLE = utils.formatString(yamlFile.getString("customize_add_commands_anvil_title", "&6&lWrite command to be added"));
        CUSTOMIZE_ADD_COMMANDS_DEFAULT_TEXT = utils.formatString(yamlFile.getString("customize_add_commands_default_text", "Write command to be added"));
        CUSTOMIZE_UNAVAILABLE = utils.formatString(yamlFile.getString("customize_unavailable_item", "&c&lUNAVAILABLE"));

        MSG_OPEN_SHOP = utils.formatString(yamlFile.getString("message-open-shop", "&7Opening daily shop..."));
        MSG_BUY_ITEM = utils.formatString(yamlFile.getString("message-buy-item", "&7You bought the item {item} for {price}"));
        MSG_SELL_ITEMS = utils.formatString(yamlFile.getString("message-sell-item", "&7You sold all the items for {price}"));
        MSG_NOT_ENOUGH_MONEY = utils.formatString(yamlFile.getString("message-not-enough-money", "&7Ey! You dont have enough money to buy this item"));
        MSG_INVENTORY_FULL = utils.formatString(yamlFile.getString("message-inventory-full", "&7Ey! Your inventory is full!"));
        MSG_INVALID_ITEM = utils.formatString(yamlFile.getString("message-invalid-item", "&7Ey, we don't accept that item on the market!"));
        MSG_NEW_DAILY_ITEMS = utils.formatString(yamlFile.getString("message-new-daily-items", "&7New items available on the Daily Shop!"));
        MSG_NOT_PERMS = utils.formatString(yamlFile.getString("message-not-perms", "&7Ey, you dont have permission to do this!"));
        MSG_NOT_PERMS_ITEM = utils.formatString(yamlFile.getString("message-not-perms-item", "&7Ey, you don't have the perms necessary to buy this item"));
        MSG_ERROR_ITEM_HAND = utils.formatString(yamlFile.getString("message-error_item_hand", "&7Ey, you need to have an item in your hand"));
        MSG_ERROR_PRICE = utils.formatString(yamlFile.getString("message-error_price", "&7Ey, you have to specify a price for the item"));
        MSG_ERROR_ADDING_ITEM = utils.formatString(yamlFile.getString("message-error_adding_item", "&7Something went wrong while adding the item"));
        MSG_ITEM_ADDED = utils.formatString(yamlFile.getString("message-added_item", "&7Item added successfully"));
        MSG_REMOVED_ITEM = utils.formatString(yamlFile.getString("message_removed_item", "&7Removed item successfully"));
        MSG_ITEM_ALREADY_ON_SALE = utils.formatString(yamlFile.getString("message_item_on_sale", "&7Ey! That item is already on sale"));
        MSG_NOT_IN_STOCK = utils.formatString(yamlFile.getString("message_not_in_stock", "&7That item is not in stock anymore, an admin must have take it away"));
        MSG_SELL_ITEMS_GUI_EMPTY = utils.formatString(yamlFile.getString("message-sell-items-gui-empty", "&7There are not items registered for sale, you can add one with /rdshop addSellItem"));
        MSG_ADDED_ITEM = utils.formatString(yamlFile.getString("message-add-daily-item-success", "&7Item added successfully"));
        MSG_ADD_ITEM_TITLE = utils.formatString(yamlFile.getString("message-add-item-title", "&a&lClick item"));
        MSG_ADD_ITEM_SUBTITLE = utils.formatString(yamlFile.getString("message-add-item-subtitle", "&7In hand to add it"));
        MSG_TIMER_EXPIRED = utils.formatString(yamlFile.getString("message-timer-expired", "&Ey! The time to select an item expired, try it again"));
        MSG_NOT_INTEGER = utils.formatString(yamlFile.getString("message-not-integer", "Not integer"));
        MSG_RELOAD = utils.formatString(yamlFile.getString("message-reload", "&7Reloaded all files"));
    }

    public static void createLocales() {
        File localeDirectory = new File(main.getDataFolder() + File.separator + "locales");

        if (!localeDirectory.exists() && !localeDirectory.isDirectory()) {
            localeDirectory.mkdir();
        }

        List<String> locales = new ArrayList<>(Arrays.asList(
                "en_US.yml",
                "es_ES.yml",
                "ru_RU.yml",
                "cn_CN.yml"));

        for (String s : locales) {
            File locale = new File(main.getDataFolder() + File.separator + "locales" + File.separator + s);
            if (locale.exists()) continue;
            try {
                locale.createNewFile();
                InputStream in = main.getResource("locales/" + s);
                OutputStream out = new FileOutputStream(locale);
                byte[] buffer = new byte[1024];
                int lenght = in.read(buffer);
                while (lenght != -1) {
                    out.write(buffer, 0, lenght);
                    lenght = in.read(buffer);
                }
                //ByteStreams.copy(in, out); BETA method, data losses ahead
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void reload() {
        init();
    }

}

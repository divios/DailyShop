package io.github.divios.dailyrandomshop;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class Config {

    public final String
        PREFIX,
        BUY_GUI_TITLE, BUY_GUI_PAINTING_NAME, BUY_GUI_ARROW_NAME, BUY_GUI_ITEMS_LORE_PRICE, BUY_GUI_ITEMS_LORE_RARITY,
        CONFIRM_GUI_NAME, CONFIRM_GUI_ADD_PANE, CONFIRM_GUI_REMOVE_PANE, CONFIRM_GUI_CONFIRM_PANE, CONFIRM_GUI_RETURN_NAME,
        SELL_GUI_TITLE, SELL_PAINTING_NAME, SELL_ARROW_NAME, SELL_ITEM_NAME,
        CONFIRM_MENU_YES, CONFIRM_MENU_NO,
        SETTINGS_GUI_TITLE, SETTINGS_DAILY_ITEM, SETTINGS_SELL_ITEM,
        ADD_ITEMS_TITLE, ADD_ITEMS_FROM_ZERO, ADD_ITEMS_FROM_EXISTING, ADD_ITEMS_RETURN,
        DAILY_ITEMS_MENU_TITLE, DAILY_ITEMS_MENU_ADD, DAILY_ITEMS_MENU_NEXT, DAILY_ITEMS_MENU_PREVIOUS, DAILY_ITEMS_MENU_RETURN, DAILY_ITEMS_MENU_ANVIL_TITLE, DAILY_ITEMS_MENU_ANVIL_DEFAULT_TEXT,
        SELL_ITEMS_MENU_TITLE, SELL_ITEMS_MENU_NEXT, SELL_ITEMS_MENU_PREVIOUS, SELL_ITEMS_MENU_RETURN, SELL_ITEMS_MENU_ANVIL_TITLE, SELL_ITEMS_MENU_ANVIL_DEFAULT_TEXT,
        CUSTOMIZE_GUI_TITLE, CUSTOMIZE_CRAFT, CUSTOMIZE_RETURN, CUSTOMIZE_MATERIAL, CUSTOMIZE_RENAME, CUSTOMIZE_LORE, CUSTOMIZE_ENCHANTS, CUSTOMIZE_AMOUNT, CUSTOMIZE_ENABLE_COMMANDS, CUSTOMIZE_CHANGE_COMMANDS, CUSTOMIZE_TOGGLE_ENCHANTS,
            CUSTOMIZE_RENAME_ANVIL_TITLE, CUSTOMIZE_RENAME_ANVIL_DEFAULT_TEXT, CUSTOMIZE_CHANGE_LORE_TITLE, CUSTOMIZE_CHANGE_LORE_DEFAULT_TEXT,
            CUSTOMIZE_ADD_COMMANDS_TITLE, CUSTOMIZE_ADD_COMMANDS_DEFAULT_TEXT,
            CUSTOMIZE_TOGGLE_ATTRIBUTES, CUSTOMIZE_TOGGLE_EFFECTS, CUSTOMIZE_TOGGLE_MMOITEM_SRATCH;

    public final List<String > BUY_GUI_PAINTING_LORE, BUY_GUI_ARROW_LORE,
            SELL_PAINTING_LORE, SELL_ARROW_LORE,
            SETTINGS_DAILY_ITEM_LORE, SETTINGS_SELL_ITEM_LORE,
            ADD_ITEMS_FROM_ZERO_LORE, ADD_ITEMS_FROM_EXISTING_LORE, ADD_ITEMS_RETURN_LORE,
            DAILY_ITEMS_MENU_ITEMS_LORE, DAILY_ITEMS_MENU_ADD_LORE,
            SELL_ITEMS_MENU_ITEMS_LORE,
            CUSTOMIZE_CRAFT_LORE, CUSTOMIZE_RETURN_LORE, CUSTOMIZE_RENAME_LORE, CUSTOMIZE_MATERIAL_LORE, CUSTOMIZE_LORE_LORE, CUSTOMIZE_ENCHANTS_LORE, CUSTOMIZE_AMOUNT_LORE, CUSTOMIZE_ENABLE_COMMANDS_LORE,
                CUSTOMIZE_CHANGE_COMMANDS_LORE, CUSTOMIZE_TOGGLE_ENCHANTS_LORE, CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE, CUSTOMIZE_TOGGLE_EFFECTS_LORE, CUSTOMIZE_TOGGLE_MMOITEM_SRATCH_LORE;

    public final String MSG_OPEN_SHOP, MSG_BUY_ITEM, MSG_SELL_ITEMS, MSG_NOT_ENOUGH_MONEY, MSG_INVENTORY_FULL,
            MSG_INVALID_ITEM , MSG_NOT_PERMS, MSG_ERROR_ITEM_HAND, MSG_ERROR_PRICE,
            MSG_ERROR_ADDING_ITEM, MSG_ITEM_ADDED, MSG_NEW_DAILY_ITEMS, MSG_SELL_ITEMS_GUI_EMPTY,
            MSG_ADDED_ITEM, MSG_REMOVED_ITEM, MSG_ITEM_ON_SALE, MSG_NOT_IN_STOCK, MSG_TIMER_EXPIRED,
            MSG_ADD_ITEM_TITLE, MSG_ADD_ITEM_SUBTITLE, MSG_RELOAD;

    public int N_DAILY_ITEMS;

    public Config(DailyRandomShop main) {


        File file = new File(main.getDataFolder() + File.separator + "locales" + File.separator + main.getConfig().getString("language", "en_US") + ".yml");

        if(file == null) file = new File(main.getDataFolder() + File.separator + "locales" + File.separator + "en_US");

        FileConfiguration yamlFile = YamlConfiguration.loadConfiguration(file);

        PREFIX = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("prefix", "&6&lDailyShop > "));
        main.getConfig().addDefault("timer-duration", 86400);
        main.getConfig().addDefault("buy-price-multiplier", 1);
        main.getConfig().addDefault("sell-price-multiplier", 1);
        main.getConfig().addDefault("sell-price-multiplier", 1);
        main.getConfig().addDefault("enable-sell-gui", true);
        main.getConfig().addDefault("enable-confirm-gui", true);
        main.getConfig().addDefault("enable-rarity", true);
        N_DAILY_ITEMS = main.getConfig().getInt("number-of-daily-items", 14);
        if(N_DAILY_ITEMS < 0 || N_DAILY_ITEMS > 36) N_DAILY_ITEMS = 14;

        BUY_GUI_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-shop-gui-name","&6&lDailyShop"));
        BUY_GUI_PAINTING_NAME = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-shop-gui-painting-name","&c&lWhat is this?"));
        BUY_GUI_PAINTING_LORE =  yamlFile.getStringList("daily-shop-gui-painting-lore");
        BUY_GUI_ITEMS_LORE_PRICE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-items-lore-price", "&6Price: &7{price}"));
        BUY_GUI_ITEMS_LORE_RARITY = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-items-lore-rarity", "&6Rarity: &7{rarity}"));
        BUY_GUI_ARROW_NAME = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-shop-to-sell-name","&c&lVisit Market"));
        BUY_GUI_ARROW_LORE = yamlFile.getStringList("daily-shop-to-sell-lore");

        CONFIRM_GUI_NAME = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("confirm-gui-name","&2&lConfirm Purchase"));
        CONFIRM_GUI_ADD_PANE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("confirm-gui-add-pane","&aAdd"));
        CONFIRM_GUI_REMOVE_PANE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("confirm-gui-remove-pane","&cRemove"));
        CONFIRM_GUI_CONFIRM_PANE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("confirm-gui-confirm-pane","&aConfirm"));
        CONFIRM_GUI_RETURN_NAME = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("confirm-gui-return-name","&cReturn"));

        SELL_GUI_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-gui-name", "&3&lMarket"));
        SELL_PAINTING_NAME = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-gui-painting-name","&3&lWhat is this?"));
        SELL_PAINTING_LORE =  yamlFile.getStringList("sell-gui-painting-lore");
        SELL_ITEM_NAME = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-item-name"));
        SELL_ARROW_NAME = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-to-daily-shop-name","&3&lReturn to daily shop"));
        SELL_ARROW_LORE = yamlFile.getStringList("sell-to-daily-shop-lore");

        CONFIRM_MENU_YES = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("confirm-menu-yes", "&a&lConfirm"));
        CONFIRM_MENU_NO = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("confirm-menu-no", "&c&lCancel"));

        SETTINGS_GUI_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("settings-gui-title", "&6&lSettings"));
        SETTINGS_DAILY_ITEM = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("settings-daily-item", "&6&lDaily items"));
        SETTINGS_DAILY_ITEM_LORE = yamlFile.getStringList("settings-daily-items-lore");
        SETTINGS_SELL_ITEM = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("settings-sell-items", "&a&lSell items"));
        SETTINGS_SELL_ITEM_LORE = yamlFile.getStringList("settings-sell-items-lore");

        ADD_ITEMS_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("add-items-title", "&3&lCreate Item"));
        ADD_ITEMS_FROM_ZERO = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("add-items-from-zero", "&c&lCreate item from zero"));
        ADD_ITEMS_FROM_ZERO_LORE = yamlFile.getStringList("add-items-from-zero-lore");
        ADD_ITEMS_FROM_EXISTING = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("add-items-from-existing", "&a&lCreate item from existing"));
        ADD_ITEMS_FROM_EXISTING_LORE = yamlFile.getStringList("add-items-from-existing-lore");
        ADD_ITEMS_RETURN = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("add-items-return", "&c&lReturn"));
        ADD_ITEMS_RETURN_LORE = yamlFile.getStringList("add-items-return-lore");

        DAILY_ITEMS_MENU_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-items-menu_title", "&6&lDaily items Manager"));
        DAILY_ITEMS_MENU_ADD = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-items-menu-add", "&3&lAdd"));
        DAILY_ITEMS_MENU_ADD_LORE = yamlFile.getStringList("daily-items-menu-add-lore");
        DAILY_ITEMS_MENU_NEXT = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-items-menu_next", "&6&lNext"));
        DAILY_ITEMS_MENU_PREVIOUS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-items-menu_previous", "&6&lPrevious"));
        DAILY_ITEMS_MENU_RETURN = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-items-menu_return", "&c&lReturn"));
        DAILY_ITEMS_MENU_ITEMS_LORE = yamlFile.getStringList("daily-items-menu-items-lore");
        DAILY_ITEMS_MENU_ANVIL_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-items-anvil-title", "&6&lSet Price"));
        DAILY_ITEMS_MENU_ANVIL_DEFAULT_TEXT = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("daily-items-anvil-default-text", "Price"));

        SELL_ITEMS_MENU_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-items-menu_title", "&6&lsell items Manager"));
        SELL_ITEMS_MENU_NEXT = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-items-menu_next", "&6&lNext"));
        SELL_ITEMS_MENU_PREVIOUS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-items-menu_previous", "&6&lPrevious"));
        SELL_ITEMS_MENU_RETURN = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-items-menu_return", "&c&lReturn"));
        SELL_ITEMS_MENU_ITEMS_LORE = yamlFile.getStringList("sell-items-menu-items-lore");
        SELL_ITEMS_MENU_ANVIL_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-items-anvil-title", "&6&lSet Price"));
        SELL_ITEMS_MENU_ANVIL_DEFAULT_TEXT = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("sell-items-anvil-default-text", "Price"));

        CUSTOMIZE_GUI_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_menu_title", "&a&lCustomize Item"));
        CUSTOMIZE_CRAFT = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_craft", "&a&lCraft item"));
        CUSTOMIZE_CRAFT_LORE = yamlFile.getStringList("customize_craft_lore");
        CUSTOMIZE_RETURN = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_return", "&c&lReturn"));
        CUSTOMIZE_RETURN_LORE = yamlFile.getStringList("customize_return_lore");
        CUSTOMIZE_RENAME = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_rename", "&f&lRename"));
        CUSTOMIZE_RENAME_LORE = yamlFile.getStringList("customize_rename_lore");
        CUSTOMIZE_MATERIAL = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize-change-material", "&f&lChange Material"));
        CUSTOMIZE_MATERIAL_LORE = yamlFile.getStringList("customize_change_material_lore");
        CUSTOMIZE_LORE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize-change_lore", "&f&lChange Lore"));
        CUSTOMIZE_LORE_LORE = yamlFile.getStringList("customize_change_lore_lore");
        CUSTOMIZE_ENCHANTS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize-change_enchants", "&f&lEdit enchantments"));
        CUSTOMIZE_ENCHANTS_LORE = yamlFile.getStringList("customize_change_enchants_lore");
        CUSTOMIZE_AMOUNT = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_change_amount", "&f&lChange Amount"));
        CUSTOMIZE_AMOUNT_LORE = yamlFile.getStringList("customize_change_amount_lore");
        CUSTOMIZE_ENABLE_COMMANDS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_enable_commands", "&f&lSet Item Reward as Commands"));
        CUSTOMIZE_ENABLE_COMMANDS_LORE = yamlFile.getStringList("customize_enable_commands_lore");
        CUSTOMIZE_CHANGE_COMMANDS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_change_commands", "&f&lSet commands to run"));
        CUSTOMIZE_CHANGE_COMMANDS_LORE = yamlFile.getStringList("customize_change_commands_lore");
        CUSTOMIZE_TOGGLE_ENCHANTS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_toggle_enchants", "&f&lMake enchant visible/invisible"));
        CUSTOMIZE_TOGGLE_ENCHANTS_LORE = yamlFile.getStringList("customize_toggle_enchants_lore");
        CUSTOMIZE_TOGGLE_ATTRIBUTES = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_toggle_attributes", "&f&lMake attributes visible/invisible"));
        CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE = yamlFile.getStringList("customize_toggle_attributes_lore");
        CUSTOMIZE_TOGGLE_EFFECTS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_toggle_effects", "&f&lMake potion effects visible/invisible"));
        CUSTOMIZE_TOGGLE_EFFECTS_LORE = yamlFile.getStringList("customize_toggle_effects_lore");
        CUSTOMIZE_TOGGLE_MMOITEM_SRATCH = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_toggle_mmoitem_scratch", "&f&lGenerate MMOItem from Scratch"));
        CUSTOMIZE_TOGGLE_MMOITEM_SRATCH_LORE = yamlFile.getStringList("customize_toggle_mmoitem_scratch_lore");
        CUSTOMIZE_RENAME_ANVIL_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_rename_anvil_title", "&6&lWrite the new name"));
        CUSTOMIZE_RENAME_ANVIL_DEFAULT_TEXT = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_rename_anvil_default_text", "Write the new name"));
        CUSTOMIZE_CHANGE_LORE_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_change_lore_anvil_title", "&6&lWrite lore"));
        CUSTOMIZE_CHANGE_LORE_DEFAULT_TEXT = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_change_lore_default_text", "Write lore"));
        CUSTOMIZE_ADD_COMMANDS_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_add_commands_anvil_title", "&6&lWrite command to be added"));
        CUSTOMIZE_ADD_COMMANDS_DEFAULT_TEXT = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("customize_add_commands_default_text", "Write command to be added"));

        MSG_OPEN_SHOP = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-open-shop", "&7Opening daily shop..."));
        MSG_BUY_ITEM = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-buy-item", "&7You bought the item {item} for {price}"));
        MSG_SELL_ITEMS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-sell-item", "&7You sold all the items for {price}"));
        MSG_NOT_ENOUGH_MONEY = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-not-enough-money", "&7Ey! You dont have enough money to buy this item"));
        MSG_INVENTORY_FULL= ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-inventory-full", "&7Ey! Your inventory is full!"));
        MSG_INVALID_ITEM = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-invalid-item", "&7Ey, we don't accept that item on the market!"));
        MSG_NEW_DAILY_ITEMS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-new-daily-items", "&7New items available on the Daily Shop!"));
        MSG_NOT_PERMS = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-not-perms", "&7Ey, you dont have permission to do this!"));
        MSG_ERROR_ITEM_HAND =  ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-error_item_hand", "&7Ey, you need to have an item in your hand"));
        MSG_ERROR_PRICE =  ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-error_price", "&7Ey, you have to specify a price for the item"));
        MSG_ERROR_ADDING_ITEM = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-error_adding_item", "&7Something went wrong while adding the item"));
        MSG_ITEM_ADDED = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-added_item", "&7Item added successfully"));
        MSG_REMOVED_ITEM = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message_removed_item", "&7Removed item successfully"));
        MSG_ITEM_ON_SALE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message_item_on_sale", "&7Ey! That item is already on sale"));
        MSG_NOT_IN_STOCK = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message_not_in_stock", "&7That item is not in stock anymore, an admin must have take it away"));
        MSG_SELL_ITEMS_GUI_EMPTY = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-sell-items-gui-empty", "&7There are not items registered for sale, you can add one with /rdshop addSellItem"));
        MSG_ADDED_ITEM = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-add-daily-item-success", "&7Item added successfully"));
        MSG_ADD_ITEM_TITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-add-item-title", "&a&lClick item"));
        MSG_ADD_ITEM_SUBTITLE = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-add-item-subtitle", "&7In hand to add it"));
        MSG_TIMER_EXPIRED = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-timer-expired", "&Ey! The time to select an item expired, try it again"));
        MSG_RELOAD = ChatColor.translateAlternateColorCodes('&', yamlFile.getString("message-reload", "&7Reloaded all files"));
    }

}

package io.github.divios.dailyrandomshop;

import org.bukkit.ChatColor;

import java.util.List;

public class Config {

    public final String PREFIX, BUY_GUI_TITLE, BUY_GUI_PAINTING_NAME, BUY_GUI_ARROW_NAME, BUY_GUI_ITEMS_LORE,
        CONFIRM_GUI_NAME, CONFIRM_GUI_ADD_PANE, CONFIRM_GUI_REMOVE_PANE, CONFIRM_GUI_CONFIRM_PANE, CONFIRM_GUI_RETURN_NAME,
        SELL_GUI_TITLE, SELL_PAINTING_NAME, SELL_ARROW_NAME, SELL_ITEM_NAME;
    public final List<String > BUY_GUI_PAINTING_LORE, BUY_GUI_ARROW_LORE, SELL_PAINTING_LORE, SELL_ARROW_LORE;
    public final String MSG_OPEN_SHOP, MSG_BUY_ITEM, MSG_SELL_ITEMS, MSG_NOT_ENOUGHT_MONEY, MSG_INVENTORY_FULL,
            MSG_NEW_DAILY_ITEMS, MSG_RELOAD;

    Config(DailyRandomShop main) {

        PREFIX = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("prefix", "&aDailyShop > "));
        main.getConfig().addDefault("timer-duration", 86400);
        main.getConfig().addDefault("buy-price-multiplier", 1);
        main.getConfig().addDefault("sell-price-multiplier", 1);
        main.getConfig().addDefault("sell-price-multiplier", 1);
        main.getConfig().addDefault("enable-sell-gui", true);

        BUY_GUI_TITLE = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("daily-shop-gui-name","&aDailyShop"));
        BUY_GUI_PAINTING_NAME = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("daily-shop-gui-painting-name","&aWhat is this?"));
        BUY_GUI_PAINTING_LORE =  main.getConfig().getStringList("daily-shop-gui-painting-lore");
        BUY_GUI_ITEMS_LORE = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("daily-items-lore"));
        BUY_GUI_ARROW_NAME = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("daily-shop-to-sell-name","&6Visit Market"));
        BUY_GUI_ARROW_LORE = main.getConfig().getStringList("daily-shop-to-sell-lore");

        CONFIRM_GUI_NAME = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("confirm-gui-name","&aConfirm Purchase"));
        CONFIRM_GUI_ADD_PANE = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("confirm-gui-add-pane","&aAdd"));
        CONFIRM_GUI_REMOVE_PANE = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("confirm-gui-remove-pane","&cRemove"));
        CONFIRM_GUI_CONFIRM_PANE = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("confirm-gui-confirm-pane","&aConfirm"));
        CONFIRM_GUI_RETURN_NAME = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("confirm-gui-return-name","&cReturn"));

        SELL_GUI_TITLE = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("sell-gui-name", "&6Market"));
        SELL_PAINTING_NAME = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("sell-gui-painting-name","&aWhat is this?"));
        SELL_PAINTING_LORE =  main.getConfig().getStringList("sell-gui-painting-lore");
        SELL_ITEM_NAME = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("sell-item-name"));
        SELL_ARROW_NAME = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("sell-to-daily-shop-name","&6Return to daily shop"));
        SELL_ARROW_LORE = main.getConfig().getStringList("sell-to-daily-shop-lore");

        MSG_OPEN_SHOP = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-open-shop", "&7Opening daily shop..."));
        MSG_BUY_ITEM = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-buy-item", "&7You bought the item {item} for {price}"));
        MSG_SELL_ITEMS = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-sell-item", "&7You sold all the items for {price}"));
        MSG_NOT_ENOUGHT_MONEY = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-not-enough-money", "&7Ey! You dont have enough money to buy this item"));
        MSG_INVENTORY_FULL= ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-inventory-full", "&7Ey! Your inventory is full!"));
        MSG_NEW_DAILY_ITEMS = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-new-daily-items", "&7New items available on the Daily Shop!"));
        MSG_RELOAD = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-reload", "&7Reloaded all files"));
    }

}

package io.github.divios.dailyrandomshop;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.sql.BatchUpdateException;
import java.util.List;

public class Config {

    public final String PREFIX, BUY_GUI_TITLE, BUY_GUI_PAINTING_NAME, BUY_GUI_ARROW_NAME, BUY_GUI_ITEMS_LORE,
        CONFIRM_GUI_NAME, CONFIRM_GUI_ADD_PANE, CONFIRM_GUI_REMOVE_PANE, CONFIRM_GUI_CONFIRM_PANE, CONFIRM_GUI_RETURN_NAME,
        SELL_GUI_TITLE, SELL_PAINTING_NAME, SELL_ARROW_NAME, SELL_ITEM_NAME;
    public final List<String > BUY_GUI_PAINTING_LORE, BUY_GUI_ARROW_LORE, SELL_PAINTING_LORE, SELL_ARROW_LORE;
    public final String MSG_OPEN_SHOP, MSG_BUY_ITEM, MSG_SELL_ITEMS, MSG_NOT_ENOUGH_MONEY, MSG_INVENTORY_FULL,
            MSG_INVALID_ITEM , MSG_NOT_PERMS, MSG_ADD_DAILY_ITEM_ERROR_ITEM, MSG_ADD_DAILY_ITEM_ERROR_PRICE,
            MSG_ADD_DAILY_ITEM_ERROR, MSG_ADD_DAILY_ITEM_SUCCESS, MSG_NEW_DAILY_ITEMS, MSG_RELOAD;
    public double N_DAILY_ITEMS;
    public String BUY_GUI_PANE1, BUY_GUI_PANE2, SELL_GUI_PANE;

    public Config(DailyRandomShop main) {

        PREFIX = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("prefix", "&aDailyShop > "));
        main.getConfig().addDefault("timer-duration", 86400);
        main.getConfig().addDefault("buy-price-multiplier", 1);
        main.getConfig().addDefault("sell-price-multiplier", 1);
        main.getConfig().addDefault("sell-price-multiplier", 1);
        main.getConfig().addDefault("enable-sell-gui", true);
        main.getConfig().addDefault("enable-confirm-gui", true);
        N_DAILY_ITEMS = main.getConfig().getDouble("number-of-daily-items", 7);
        if(N_DAILY_ITEMS < 0 || N_DAILY_ITEMS > 28) N_DAILY_ITEMS = 7.0;
        BUY_GUI_PANE1 = main.getConfig().getString("daily-shop-pane1", "GREEN_STAINED_GLASS_PANE").toUpperCase();
        try{
            XMaterial.valueOf(BUY_GUI_PANE1);
        } catch (IllegalArgumentException e) {
            main.getLogger().warning("daily-shop-pane1 is either an incompatible material or does not exist, setting it to default");
            BUY_GUI_PANE1 = "GREEN_STAINED_GLASS_PANE";
        }

        BUY_GUI_PANE2 = main.getConfig().getString("daily-shop-pane2", "LIME_STAINED_GLASS_PANE").toUpperCase();
        try{
            XMaterial.valueOf(BUY_GUI_PANE2);
        } catch (IllegalArgumentException e) {
            main.getLogger().warning("daily-shop-pane2 is either an incompatible material or does not exist, setting it to default");
            BUY_GUI_PANE2 = "LIME_STAINED_GLASS_PANE";
        }

        SELL_GUI_PANE = main.getConfig().getString("sell-gui-pane", "GRAY_STAINED_GLASS_PANE").toUpperCase();
        try{
            XMaterial.valueOf(SELL_GUI_PANE);
        } catch (IllegalArgumentException e) {
            main.getLogger().warning("sell-gui-pane is either an incompatible material or does not exist, setting it to default");
            SELL_GUI_PANE = "GRAY_STAINED_GLASS_PANE";
        }

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
        MSG_NOT_ENOUGH_MONEY = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-not-enough-money", "&7Ey! You dont have enough money to buy this item"));
        MSG_INVENTORY_FULL= ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-inventory-full", "&7Ey! Your inventory is full!"));
        MSG_INVALID_ITEM = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-invalid-item", "&7Ey, we don't accept that item on the market!"));
        MSG_NEW_DAILY_ITEMS = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-new-daily-items", "&7New items available on the Daily Shop!"));
        MSG_NOT_PERMS = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-not-perms", "&7Ey, you dont have permission to do this!"));
        MSG_ADD_DAILY_ITEM_ERROR_ITEM =  ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-add-daily-item-error-item", "&7Ey, you need to have an item in your hand"));
        MSG_ADD_DAILY_ITEM_ERROR_PRICE =  ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-add-daily-item-error-price", "&7Ey, you have to specify a price for the item"));
        MSG_ADD_DAILY_ITEM_ERROR = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-add-daily-item-error", "&7Something went wrong while adding the item"));
        MSG_ADD_DAILY_ITEM_SUCCESS = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-add-daily-item-success", "&7Item added successfully"));
        MSG_RELOAD = ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("message-reload", "&7Reloaded all files"));
    }

}

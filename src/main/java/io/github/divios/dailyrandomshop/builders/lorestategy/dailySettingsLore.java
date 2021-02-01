package io.github.divios.dailyrandomshop.builders.lorestategy;

import io.github.divios.dailyrandomshop.builders.itemsFactory;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class dailySettingsLore implements loreStategy {

    private static final dataManager dbManager = dataManager.getInstance();

    @Override
    public void setLore(ItemStack item) {
        String price = String.format("%,.2f",new itemsFactory.Builder(item)
                .getPrice(dbManager.listDailyItems));
        List<String> lore = new ArrayList<>();
        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_PRICE);
        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY);
        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_RARITY);
        lore.add("");
        lore.addAll(conf_msg.DAILY_ITEMS_MENU_ITEMS_LORE);
        lore.replaceAll(s -> s.replaceAll("\\{price}", price));
        utils.setLore(item, lore);
    }

    @Override
    public void removeLore(ItemStack item) {
        utils.removeLore(item,
                conf_msg.DAILY_ITEMS_MENU_ITEMS_LORE.size() + 4);
    }
}

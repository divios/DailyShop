package io.github.divios.dailyrandomshop.builders.lorestategy;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class currentItemsLore implements loreStrategy {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();

    @Override
    public void setLore(ItemStack item) {

        String price = String.format("%,.2f", dailyItem.getPrice(item));

        List<String> lore = new ArrayList<>();

        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_PRICE);
        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY);
        //if(main.getConfig().getBoolean("enable-rarity"))
        lore.replaceAll(s -> s.replaceAll("\\{price}", price)
                        .replaceAll("\\{currency}", "")); //add rarity
        utils.setLore(item, lore);
    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {
        removeLore(item);
        setLore(item);
    }
}

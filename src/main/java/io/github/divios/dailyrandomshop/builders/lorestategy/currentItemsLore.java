package io.github.divios.dailyrandomshop.builders.lorestategy;

import io.github.divios.dailyrandomshop.builders.itemsFactory;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class currentItemsLore implements loreStategy {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();

    @Override
    public void setLore(ItemStack item) {

        String price = String.format("%,.2f", new itemsFactory.Builder(item)
                .getPrice(dbManager.listDailyItems));

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
}

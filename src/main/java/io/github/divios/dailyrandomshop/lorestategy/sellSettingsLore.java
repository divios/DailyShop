package io.github.divios.dailyrandomshop.lorestategy;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class sellSettingsLore implements loreStrategy {

    @Override
    public void setLore(ItemStack item) {
        List<String> lore = new ArrayList<>();
        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_PRICE
                .replaceAll("\\{price}", "" + utils.getPrice(item)));
        lore.add("");
        lore.addAll(conf_msg.SELL_ITEMS_MENU_ITEMS_LORE);
        utils.setLore(item,
                lore);
    }

    @Override
    public void removeLore(ItemStack item) {
        utils.removeLore(item, 2 +
                conf_msg.SELL_ITEMS_MENU_ITEMS_LORE.size());
    }

    @Override
    public void update(ItemStack item) {
        removeLore(item);
        setLore(item);
    }
}

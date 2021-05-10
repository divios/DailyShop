package io.github.divios.dailyrandomshop.lorestategy;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class currentItemsLore implements loreStrategy {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();

    @Override
    public void setLore(ItemStack item) {

        String price = String.format("%,.2f", new dailyItem(item).getPrice());
        String currency;
        try {
            currency = ((AbstractMap.SimpleEntry<String, String>) new dailyItem(item).getMetadata(dailyItem.dailyMetadataType.rds_econ)).getValue();
        } catch (NullPointerException e) {currency = conf_msg.VAULT_CUSTOM_NAME;}
        String finalCurrency = currency; /* Because of lambdas... */

        List<String> lore = new ArrayList<>();

        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_PRICE);
        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY);
        if(conf_msg.ENABLE_RARITY)
            lore.add(conf_msg.BUY_GUI_ITEMS_LORE_RARITY);
        lore.replaceAll(s -> s.replaceAll("\\{price}", price)
                        .replaceAll("\\{currency}", finalCurrency)
                        .replaceAll("\\{rarity}", dailyItem.getRarityLore(item))); //add rarity
        utils.setLore(item, lore);

        if (new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_bundle))
            utils.setLore(item, Arrays.asList("",
                        "&6Right Click > &7to see the list", "&7of items in the bundle"));
    }

    @Override
    public void removeLore(ItemStack item) {
        int n = 2;
        if(conf_msg.ENABLE_RARITY) n = 3;
        utils.removeLore(item,
                conf_msg.DAILY_ITEMS_MENU_ITEMS_LORE.size() + n);
    }

    @Override
    public void update(ItemStack item) {
        removeLore(item);
        setLore(item);
    }
}

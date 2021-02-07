package io.github.divios.dailyrandomshop.lorestategy;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class dailySettingsLore implements loreStrategy {

    private static final dataManager dbManager = dataManager.getInstance();

    @Override
    public void setLore(ItemStack item) {
        String price = String.format("%,.2f", dailyItem.getPrice(item));
        String currency = conf_msg.VAULT_CUSTOM_NAME;
        try {
            currency = ((AbstractMap.SimpleEntry<String, String>)
                    new dailyItem(item).getMetadata(dailyItem.dailyMetadataType.rds_econ)).getValue();
        } catch (NullPointerException ignored) {}
        String finalCurrency = currency; /* Because of lambdas... */

        List<String> lore = new ArrayList<>();
        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_PRICE);
        lore.add(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY);
        if(conf_msg.ENABLE_RARITY)
            lore.add(conf_msg.BUY_GUI_ITEMS_LORE_RARITY);
        lore.add("");
        lore.addAll(conf_msg.DAILY_ITEMS_MENU_ITEMS_LORE);
        if (dailyItem.isMMOitem(item)) {
            lore.add(utils.formatString("&6> Click + Q: &7Reload MMOItem"));
        }

        lore.replaceAll(s -> s.replaceAll("\\{price}", price)
                .replaceAll("\\{rarity}", dailyItem.getRarityLore(item))
                .replaceAll("\\{currency}", finalCurrency));

        utils.setLore(item, lore);

        if(new dailyItem(item).hasMetadata(dailyItem.dailyMetadataType.rds_amount))
            item.setAmount((Integer) new dailyItem(item).
                    getMetadata(dailyItem.dailyMetadataType.rds_amount));
    }

    @Override
    public void removeLore(ItemStack item) {
        int n = 3;
        if(conf_msg.ENABLE_RARITY) n = 4;
        utils.removeLore(item,
                conf_msg.DAILY_ITEMS_MENU_ITEMS_LORE.size() + n);
    }

    @Override
    public void update(ItemStack item) {
        removeLore(item);
        setLore(item);
    }
}

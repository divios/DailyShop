package io.github.divios.dailyrandomshop.builders.lorestategy;

import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class confirmItemsLore implements loreStrategy {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();

    @Override
    public void setLore(ItemStack item) {
        String price = String.format("%,.2f", dailyItem.getPrice(item) * item.getAmount());
        utils.setLore(item, Arrays.asList("&6Buy for &7" + price));
    }

    @Override
    public void removeLore(ItemStack item) {
        utils.removeLore(item, 1);
    }

    @Override
    public void update(ItemStack item) {
        removeLore(item);
        setLore(item);
    }

    public void update(ItemStack item, ItemStack itemPrice) {
        removeLore(item);
        setLore(item, itemPrice);
    }

    public void setLore(ItemStack item, ItemStack itemPrice) {
        String price = String.format("%,.2f", dailyItem.getPrice(itemPrice) * itemPrice.getAmount());
        utils.setLore(item, Arrays.asList("&6Buy for &7" + price));
    }

}

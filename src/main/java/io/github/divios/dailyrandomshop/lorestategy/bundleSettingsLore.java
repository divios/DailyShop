package io.github.divios.dailyrandomshop.lorestategy;

import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class bundleSettingsLore implements loreStrategy{


    @Override
    public void setLore(ItemStack item) {
        List<String> lore = new ArrayList<>();
        lore.add("&6Click > &7Add/Remove from bundle");

        utils.setLore(item, lore);

    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {

    }
}

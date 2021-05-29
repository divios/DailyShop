package io.github.divios.dailyrandomshop.lorestategy;

import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class shopsManagerLore implements loreStrategy{


    @Override
    public void setLore(ItemStack item) {
        utils.setLore(item, Collections.singletonList(""));
        utils.setLore(item, Collections.singletonList("&6Left click: &7Manage items"));
        utils.setLore(item, Collections.singletonList("&6Right click: &7Delete shop"));
        utils.setLore(item, Collections.singletonList("&6Middle click: &7Change shop name"));
        utils.setLore(item, Collections.singletonList(""));
        utils.setLore(item, Collections.singletonList("&6Shift Left Click: &7Customize shop appearance"));
    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {

    }
}

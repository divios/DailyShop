package io.github.divios.dailyrandomshop.builders.lorestategy;

import io.github.divios.dailyrandomshop.builders.itemsFactory;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class dailySettingsLore implements loreStategy {
    @Override
    public void setLore(ItemStack item) {
        utils.setLore(item, Arrays.asList(new itemsFactory.Builder(item, false).getUUID()));
    }
}

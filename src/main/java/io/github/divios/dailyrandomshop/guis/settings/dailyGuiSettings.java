package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.builders.itemsFactory;
import io.github.divios.dailyrandomshop.builders.lorestategy.dailySettingsLore;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class dailyGuiSettings {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();
    private static dailyGuiSettings instance = null;

    private dailyGuiSettings () {};

    public static void openInventory(Player p) {
        if(instance == null) {
            instance = new dailyGuiSettings();
        }

        new dynamicGui.Builder()
                .contents(instance::Contents)
                .back(settingsGuiIH::getInstance)
                .addItems((inventory, integer) -> instance.setItems(inventory))
                .nonContentAction(instance::nonContentAction)
                .setSearch(false)
                .open(p);
    }

    public List<ItemStack> Contents() {
        List<ItemStack> contents = new ArrayList<>();

        for(Map.Entry<ItemStack, Double> entry: dbManager.listDailyItems.entrySet()) {
            contents.add(new itemsFactory.Builder(entry.getKey(), true)
                    .setLoreStategy(new dailySettingsLore())
                    .getItem());
        }
        return contents;
    }

    public void setItems(Inventory inv) {
        ItemStack addItems = XMaterial.ANVIL.parseItem();
        utils.setDisplayName(addItems, "&b&lAdd");
        utils.setLore(addItems, Arrays.asList("&7Click to add item"));
        inv.setItem(52, addItems);
    }

    public dynamicGui.Response nonContentAction(int slot, Player p) {
        if (slot == 52) {
            addDailyItemGuiIH.openInventory(p);
        }
        return dynamicGui.Response.nu();
    }


}

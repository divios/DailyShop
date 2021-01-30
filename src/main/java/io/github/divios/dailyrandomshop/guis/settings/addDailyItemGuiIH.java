package io.github.divios.dailyrandomshop.guis.settings;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.customizerguis.customizerMainGuiIH;
import io.github.divios.dailyrandomshop.listeners.dynamicItemListener;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class addDailyItemGuiIH implements InventoryHolder, Listener {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static addDailyItemGuiIH instance = null;
    private static Inventory returnInventory;

    private addDailyItemGuiIH() { };

    public static void openInventory(Player p) {
        if (instance == null) {
            instance = new addDailyItemGuiIH();
            Bukkit.getPluginManager().registerEvents(instance, main);
            instance.init();
        }
        p.openInventory(instance.getInventory());
    }
    
    public void init() {
        returnInventory = Bukkit.createInventory(this, 27, conf_msg.ADD_ITEMS_TITLE);

        ItemStack fromZero = XMaterial.REDSTONE_TORCH.parseItem();
        utils.setDisplayName(fromZero, conf_msg.ADD_ITEMS_FROM_ZERO);
        utils.setLore(fromZero, conf_msg.ADD_ITEMS_FROM_ZERO_LORE);

        ItemStack fromItem = XMaterial.HOPPER.parseItem();
        utils.setDisplayName(fromItem, conf_msg.ADD_ITEMS_FROM_EXISTING);
        utils.setLore(fromItem, conf_msg.ADD_ITEMS_FROM_EXISTING_LORE);

        ItemStack returnItem = XMaterial.OAK_SIGN.parseItem();
        utils.setDisplayName(returnItem, conf_msg.ADD_ITEMS_RETURN);
        utils.setLore(returnItem, conf_msg.ADD_ITEMS_RETURN_LORE);


        returnInventory.setItem(11, fromZero);
        returnInventory.setItem(15, fromItem);
        returnInventory.setItem(22, returnItem);

        for (int i = 0; i < returnInventory.getSize(); i++) {
            ItemStack item = returnInventory.getItem(i);
            if (utils.isEmpty(item)) {
                returnInventory.setItem(i, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());
            }
        }
    }

    @Override
    public Inventory getInventory() {
        return returnInventory;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() != e.getRawSlot()) return;

        if (e.getSlot() == 22) {
            dailyGuiSettings.openInventory(p);
        }

        if (e.getSlot() == 11) {
            customizerMainGuiIH.openInventory(p, XMaterial.GRASS.parseItem(), null);
        }

        else if (e.getSlot() == 15) {
            new dynamicItemListener(p);
            p.closeInventory();
        }
    }
}
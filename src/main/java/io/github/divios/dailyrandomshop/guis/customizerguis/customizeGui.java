package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class customizeGui implements Listener, InventoryHolder {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final dShop shop;
    private boolean preventClose = true;

    private customizeGui(Player p, dShop shop) {
        this.p = p;
        this.shop = shop;
    }

    public static void open(Player p, dShop shop) {
        if (!shop.getGui().getAvailable()) {
            p.sendMessage(conf_msg.PREFIX + utils.formatString("&7Someone is already editing this gui"));
            return;
        }
        shop.getGui().setAvailable(false);
        shop.getGui().closeAll();               //Close all viewers

        p.openInventory(new customizeGui(p, shop).getInventory());
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() != this) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() != this) return;

        if (!preventClose) {
            shop.getGui().setAvailable(true);
            return;
        }
        utils.runTaskLater(() -> e.getPlayer().openInventory(getInventory()), 1L);
    }
}

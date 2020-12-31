package io.github.divios.dailyrandomshop.Listeners;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class sellGuiListener implements Listener {

    private ArrayList<Integer> dailyItemsSlots = new ArrayList<>();
    private final DailyRandomShop main;
    private final String name;

    public sellGuiListener(DailyRandomShop main, ArrayList<Integer> slots) {
        this.main = main;
        dailyItemsSlots = slots;
        name = main.config.SELL_GUI_TITLE;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(name + ChatColor.RED)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == e.getRawSlot() && !dailyItemsSlots.contains(e.getSlot())) { //si damos al inventario de arriba
            e.setCancelled(true);
            if (e.getSlot() == 27) { //Boton atras
                if (!p.hasPermission("DailyRandomShop.open")) {
                    try {
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
                    } catch (Exception ignored) {}
                    return;
                }
                for(int i: dailyItemsSlots) { //recover items
                    ItemStack item = e.getView().getTopInventory().getItem(i);
                    if (item != null) {
                        p.getInventory().addItem(item);
                        e.getView().getTopInventory().setItem(i, null); //clean inventory to prevent inventoryclose dup
                    }
                }
                try {
                    p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
                } catch (NoSuchFieldError ignored){}
                p.openInventory(main.BuyGui.getGui());
            }
            if (e.getSlot() == 31) { //Boton de confirmar
                double price = main.SellGui.calculatePrice(e.getView().getTopInventory());
                if (price != 0) {
                    main.econ.depositPlayer(p, price);

                    for(int i: dailyItemsSlots) { //remove all items
                        ItemStack item = e.getView().getTopInventory().getItem(i);
                        e.getView().getTopInventory().remove(item);
                    }
                    try {
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    } catch (NoSuchFieldError ignored){}
                    p.closeInventory();
                    p.sendMessage(main.config.PREFIX + main.config.MSG_SELL_ITEMS.replace("{price}", "" + price));
                } else {
                    try {
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
                    }catch (NoSuchFieldError ignored) { }
                }
            }
            return;
        }

        //A partir de aqui es si estamos añadiendo un item
        if (e.getCurrentItem() != null && e.getSlot() != e.getRawSlot() && e.getCurrentItem().getType() != XMaterial.AIR.parseMaterial()
                && (main.utils.getItemPrice(main.listSellItems, e.getCurrentItem(), false) == 0.0) ) {

            e.setCancelled(true);
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            } catch (NoSuchFieldError ignored){}
            p.sendMessage(main.config.PREFIX + main.config.MSG_INVALID_ITEM);
            return;
        }

        Bukkit.getScheduler().runTaskLater(main, () -> {

            main.SellGui.calculatePrice(e.getView().getTopInventory());
            if (e.getSlot() == e.getRawSlot() && dailyItemsSlots.contains(e.getSlot())) {
                p.updateInventory();
            }
        }, 1L);

    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (!e.getView().getTitle().equals(name)) {
            return;
        }
        Player p = (Player) e.getPlayer();

        for(int i: dailyItemsSlots) { //recover items
            ItemStack item = e.getView().getTopInventory().getItem(i);
            if (item != null) p.getInventory().addItem(item);
        }

    }

}

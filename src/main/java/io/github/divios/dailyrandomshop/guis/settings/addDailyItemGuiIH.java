package io.github.divios.dailyrandomshop.guis.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.guis.customizerItem.customizerMainGuiIH;
import io.github.divios.dailyrandomshop.listeners.UtilAddItemListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class addDailyItemGuiIH implements InventoryHolder, Listener {

    private final DailyRandomShop main;
    private final Player p;
    private final Inventory returnInventory;

    public addDailyItemGuiIH(DailyRandomShop main, Player p, Inventory returnInventory) {
        Bukkit.getPluginManager().registerEvents(this, main);

        this.main = main;
        this.p = p;
        this.returnInventory = returnInventory;
        p.openInventory(getInventory());

    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, main.config.ADD_ITEMS_TITLE);

        ItemStack fromZero = XMaterial.REDSTONE_TORCH.parseItem();
        ItemMeta meta = fromZero.getItemMeta();
        meta.setDisplayName(main.config.ADD_ITEMS_FROM_ZERO);
        List<String> lore = new ArrayList<>();
        for(String s: main.config.ADD_ITEMS_FROM_ZERO_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        fromZero.setItemMeta(meta);


        ItemStack fromItem = XMaterial.HOPPER.parseItem();
        meta = fromItem.getItemMeta();
        meta.setDisplayName(main.config.ADD_ITEMS_FROM_EXISTING);
        lore = new ArrayList<>();
        for(String s: main.config.ADD_ITEMS_FROM_EXISTING_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        fromItem.setItemMeta(meta);

        ItemStack returnItem = XMaterial.OAK_SIGN.parseItem();
        meta = returnItem.getItemMeta();
        meta.setDisplayName(main.config.ADD_ITEMS_RETURN);
        lore = new ArrayList<>();
        for(String s: main.config.ADD_ITEMS_RETURN_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        returnItem.setItemMeta(meta);

        inv.setItem(11, fromZero);
        inv.setItem(15, fromItem);
        inv.setItem(22, returnItem);

        for (int i=0; i<inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                inv.setItem(i, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());
            }
        }

        return inv;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        if(e.getView().getTopInventory().getHolder() != this) return;

        e.setCancelled(true);

        if(e.getSlot() != e.getRawSlot()) return;

        if(e.getSlot() == 22) {
            if(returnInventory == null) p.closeInventory();
            else p.openInventory(returnInventory);
        }

        if(e.getSlot() == 11) {
            new customizerMainGuiIH(main, p, XMaterial.GRASS.parseItem(), null);
        }

        if(e.getSlot() == 15) {
            /*if (Bukkit.getServer().getClass().getPackage().getName().contains("1_8") || Bukkit.getServer().getClass().getPackage().getName().contains("1_9") ||
                    Bukkit.getServer().getClass().getPackage().getName().contains("1_10") || Bukkit.getServer().getClass().getPackage().getName().contains("1_11")) {
               if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) p.sendMessage(main.config.PREFIX + main.config.MSG_ERROR_ITEM_HAND);
               else new customizerMainGuiIH(main, p, p.getItemInHand().clone(), null);
            }

            else {*/
                new UtilAddItemListener(main, p);
                p.closeInventory();
            //}
        }

    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getView().getTopInventory().getHolder() == this) {

            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);

        }
    }



}

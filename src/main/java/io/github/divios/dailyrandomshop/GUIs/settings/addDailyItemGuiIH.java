package io.github.divios.dailyrandomshop.GUIs.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.customizerItem.customizerMainGuiIH;
import io.github.divios.dailyrandomshop.Listeners.customAddItemListener;
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
        Inventory inv = Bukkit.createInventory(this, 27, ChatColor.DARK_AQUA + "" +
                ChatColor.BOLD + "Create item");

        ItemStack fromZero = XMaterial.REDSTONE_TORCH.parseItem();
        ItemMeta meta = fromZero.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "" +
                "Create item from zero");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Create an item from scratch, customize");
        lore.add(ChatColor.GRAY + "every aspect of it, its your creativity");
        lore.add(ChatColor.GRAY + "and you");
        meta.setLore(lore);
        fromZero.setItemMeta(meta);


        ItemStack fromItem = XMaterial.HOPPER.parseItem();
        meta = fromItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "" +
                "Create item from existing");
        lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Create an item from an existing item,");
        lore.add(ChatColor.GRAY + "useful when you need a custom item to be");
        lore.add(ChatColor.GRAY + "added (textures, nbt api, MMOItems...),");
        lore.add(ChatColor.GRAY + "you 'll still be able to customizer it");
        meta.setLore(lore);
        fromItem.setItemMeta(meta);

        ItemStack returnItem = XMaterial.OAK_SIGN.parseItem();
        meta = returnItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "" +
                "Return");
        lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Return to manage menu");

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

            new customAddItemListener(main, p);
            p.closeInventory();
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

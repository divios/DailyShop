package io.github.divios.dailyrandomshop.guis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.dailyrandomshop.DailyRandomShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class sellGuiIH implements Listener, InventoryHolder {

    private final DailyRandomShop main;
    private final Player p;
    private final ArrayList<Integer> dailyItemsSlots = new ArrayList<>();


    public sellGuiIH(DailyRandomShop main, Player p) {
        Bukkit.getPluginManager().registerEvents(this, main);

        this.p = p;
        this.main = main;

        for (int i = 18; i < 36; i++) {
            dailyItemsSlots.add(i);
        }

        p.openInventory(getInventory());
    }

    @Override
    public Inventory getInventory() {
        Inventory sellGui = Bukkit.createInventory(this, 45, main.config.SELL_GUI_TITLE);

        ItemStack painting = XMaterial.PAINTING.parseItem();
        ItemMeta meta = painting.getItemMeta();
        meta.setDisplayName(main.config.SELL_PAINTING_NAME);
        List<String> lore = new ArrayList<>();
        for (String s : main.config.SELL_PAINTING_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        painting.setItemMeta(meta);

        ItemStack fench = XMaterial.OAK_FENCE_GATE.parseItem();
        meta = fench.getItemMeta();
        meta.setDisplayName(main.config.SELL_ARROW_NAME);
        lore = new ArrayList<>();
        for (String s : main.config.SELL_ARROW_LORE) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lore);
        fench.setItemMeta(meta);

        ItemStack sellConfirm = XMaterial.OAK_SIGN.parseItem();
        meta = sellConfirm.getItemMeta();
        meta.setDisplayName(main.config.SELL_ITEM_NAME.replaceAll("\\{price}", "NaN"));
        sellConfirm.setItemMeta(meta);

        for (int i = 9; i < 18; i++) {
            ItemStack fillItem = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
            meta = fillItem.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "");
            fillItem.setItemMeta(meta);
            sellGui.setItem(i, fillItem);
        }


        sellGui.setItem(4, painting);
        sellGui.setItem(40, sellConfirm);
        if (p.hasPermission("DailyRandomShop.open")) sellGui.setItem(0, fench);

        return sellGui;
    }

    public double calculatePrice(Inventory inv) {
        double price = 0;

        for (int i : dailyItemsSlots) {
            if (inv.getItem(i) == null) continue;
            price += main.utils.getItemPrice(main.listSellItems, inv.getItem(i), false) * inv.getItem(i).getAmount();
        }
        price = (double) Math.round(price * 100.0) / 100;
        if (price == 0) {
            ItemStack item = inv.getItem(40);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(main.config.SELL_ITEM_NAME.replaceAll("\\{price}", "NaN"));
            item.setItemMeta(meta);
            inv.setItem(40, item);
        } else {
            ItemStack item = inv.getItem(40);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(main.config.SELL_ITEM_NAME.replaceAll("\\{price}", String.format("%,.2f", price)));
            item.setItemMeta(meta);
            inv.setItem(40, item);
        }
        return price;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) return;

        if (e.getSlot() == e.getRawSlot() && !dailyItemsSlots.contains(e.getSlot())) e.setCancelled(true);

        if (e.isShiftClick() && e.getSlot() != e.getRawSlot()) e.setCancelled(true);

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 0) {

            p.openInventory(main.BuyGui.getInventory());
            try {
                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.5F, 1);
            } catch (NoSuchFieldError Ignored) {
            } finally {
                return;
            }

        }

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 40) {
            Double price = calculatePrice(e.getView().getTopInventory());

            if (price == 0) {
                try {
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
                } catch (NoSuchFieldError Ignored) {
                } finally {
                    return;
                }
            }

            for (int i : dailyItemsSlots) { //remove all items
                ItemStack item = e.getView().getTopInventory().getItem(i);
                e.getView().getTopInventory().remove(item);
            }
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            } catch (NoSuchFieldError ignored) {
            }

            p.closeInventory();
            p.sendMessage(main.config.PREFIX + main.config.MSG_SELL_ITEMS.replaceAll("\\{price}", String.format("%,.2f", price)));
            main.econ.depositPlayer(p, price);

        }

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR &&
                (main.utils.getItemPrice(main.listSellItems, e.getCurrentItem(), false) == 0.0)
                && e.getSlot() != e.getRawSlot()) {

            e.setCancelled(true);
            p.sendMessage(main.config.PREFIX + main.config.MSG_INVALID_ITEM);
            try {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1);
            } catch (NoSuchFieldError Ignored) {
            } finally {
                return;
            }
        }

        Bukkit.getScheduler().runTaskLater(main, () -> {

            calculatePrice(e.getView().getTopInventory());
            if (e.getSlot() == e.getRawSlot() && dailyItemsSlots.contains(e.getSlot())) {
                p.updateInventory();
            }
        }, 1L);

    }


    @EventHandler
    private void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() == this) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {

        if (e.getView().getTopInventory().getHolder() != this) return;

        for (int i : dailyItemsSlots) { //recover items
            ItemStack item = e.getView().getTopInventory().getItem(i);
            if (item != null) p.getInventory().addItem(item);
        }

        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);


    }
}

package io.github.divios.dailyrandomshop.guis;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.hooks.hooksManager;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class sellGui implements Listener, InventoryHolder {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private final dataManager dbManager = dataManager.getInstance();
    private static sellGui instance = null;

    private sellGui() {
    }

    public static void openInventory(Player p) {
        if (instance == null) {
            instance = new sellGui();
            Bukkit.getPluginManager().registerEvents(instance, main);
        }
        p.openInventory(instance.getInventory());
    }

    @Override
    public Inventory getInventory() {
        Inventory sellGui = Bukkit.createInventory(instance, 45, conf_msg.SELL_GUI_TITLE);

        ItemStack painting = XMaterial.PAINTING.parseItem();
        utils.setDisplayName(painting, conf_msg.SELL_PAINTING_NAME);
        utils.setLore(painting, conf_msg.SELL_PAINTING_LORE);

        ItemStack sellConfirm = XMaterial.OAK_SIGN.parseItem();
        utils.setDisplayName(sellConfirm,
                conf_msg.SELL_ITEM_NAME.replaceAll("\\{price}", "NaN"));

        for (int i = 9; i < 18; i++) {
            ItemStack fillItem = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
            utils.setDisplayName(fillItem, "&6");
            sellGui.setItem(i, fillItem);
        }

        if (main.getConfig().getBoolean("enable-connected-shops", false)) {
            ItemStack fence = XMaterial.OAK_FENCE_GATE.parseItem();
            utils.setDisplayName(fence, conf_msg.BUY_GUI_ARROW_NAME);
            utils.setLore(fence, conf_msg.BUY_GUI_ARROW_LORE);
            sellGui.setItem(0, fence);
        }

        sellGui.setItem(4, painting);
        sellGui.setItem(40, sellConfirm);

        return sellGui;
    }

    public double calculatePrice(Inventory inv, Player p) {
        double price = 0;

        for (int i = 18; i < inv.getSize() - 9; i++) {
            ItemStack item = inv.getItem(i);
            if (utils.isEmpty(item)) continue;
            price += utils.getPrice(item) * inv.getItem(i).getAmount();
        }
        price = (double) Math.round(price * 100.0) / 100 * utils.getPriceModifier(p);
        ItemStack item = inv.getItem(40);
        if (price <= 0) {
            utils.setDisplayName(item, conf_msg.SELL_ITEM_NAME.
                    replaceAll("\\{price}", "NaN"));
        } else {
            utils.setDisplayName(item, conf_msg.SELL_ITEM_NAME.
                    replaceAll("\\{price}", String.format("%,.2f", price)));
        }
        return price;
    }

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        if (e.getView().getTopInventory().getHolder() != instance) return;

        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getView().getTopInventory();

        if (e.getSlot() == e.getRawSlot() && e.getSlot() == 0) {
            if (!p.hasPermission("dailyRandomShop.open")) {
                utils.noPerms(p);
                utils.sendSound(p, Sound.ENTITY_VILLAGER_NO);
                return;
            }
            buyGui.getInstance().openInventory(p);
        }

        if (e.getSlot() == e.getRawSlot() && (e.getSlot() < 18
                || e.getSlot() > 36)) {
            e.setCancelled(true);

            if (e.getSlot() == e.getRawSlot() && e.getSlot() == 40) {
                double price = calculatePrice(e.getView().getTopInventory(), p);

                if (price <= 0) {
                    utils.sendSound(p, Sound.ENTITY_VILLAGER_NO);
                    return;
                }

                for (int i = 18; i < inv.getSize(); i++) { //remove all items
                    ItemStack item = e.getView().getTopInventory().getItem(i);
                    e.getView().getTopInventory().remove(item);
                }
                utils.sendSound(p, Sound.ENTITY_PLAYER_LEVELUP);

                p.closeInventory();
                p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_SELL_ITEMS.
                        replaceAll("\\{price}", String.format("%,.2f", price)));
                hooksManager.getInstance().getVault().depositPlayer(p, price);
                return;
            }
            return;
        }

        if (e.getSlot() != e.getRawSlot() && !utils.isEmpty(e.getCurrentItem()) &&
                (utils.getPrice(e.getCurrentItem()) <= 0.0)
        ) {

            e.setCancelled(true);
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_INVALID_ITEM);
            utils.sendSound(p, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        if (e.isShiftClick() && e.getSlot() != e.getRawSlot() &&
                !utils.isEmpty(e.getCurrentItem())) {

            e.setCancelled(true);

            Inventory auxInv = Bukkit.createInventory(null, 18, "");

            IntStream.range(0, 18).forEach(i ->
                    auxInv.setItem(i, inv.getItem(i + 18)));

            List<Object> items;
            items = Arrays.asList(auxInv.addItem(e.getCurrentItem().clone()).values().toArray());

            e.getCurrentItem().setAmount(0);

            IntStream.range(0, 18).forEach(i ->
                    inv.setItem(i + 18, auxInv.getItem(i)));

            items.forEach(i -> p.getInventory().addItem((ItemStack) i));

        }

        utils.async(() -> calculatePrice(inv, p));
    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;

        Player p = (Player) e.getPlayer();
        Inventory inv = e.getView().getTopInventory();

        for (int i = 18; i < inv.getSize() - 9; i++) { //recover items
            ItemStack item = e.getView().getTopInventory().getItem(i);
            if (item != null) p.getInventory().addItem(item);
        }
    }

}

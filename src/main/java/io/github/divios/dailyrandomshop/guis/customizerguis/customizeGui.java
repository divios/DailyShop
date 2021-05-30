package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.settings.shopsManagerGui;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import io.github.divios.lib.itemHolder.dGui;
import io.github.divios.lib.itemHolder.dShop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class customizeGui implements Listener, InventoryHolder {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final dShop shop;
    private Inventory inv;
    private String title;
    private boolean preventClose = true;
    private boolean resfreshFlag = false;
    private final Map<Integer, ItemStack> pItems;

    private customizeGui(Player p, dShop shop) {
        this.p = p;
        this.shop = shop;
        this.title = shop.getGui().getTitle();

        utils.runTaskLater(() ->
                Bukkit.getPluginManager().registerEvents(this, plugin), 1L);

        pItems = withdrawPlayerItems();

        inv = getInventory();
        addCustomizeItems();
        refresh();

    }

    public static void open(Player p, dShop shop) {
        if (!shop.getGui().getAvailable()) {
            p.sendMessage(conf_msg.PREFIX + utils.formatString("&7Someone is already editing this gui"));
            return;
        }
        shop.getGui().setAvailable(false);
        shop.getGui().closeAll();               //Close all viewers

        new customizeGui(p, shop);
    }

    public void addCustomizeItems() {
        ItemStack back = XMaterial.SPRUCE_SIGN.parseItem();
        utils.setDisplayName(back, "&b&lGo back");
        utils.setLore(back, Collections.singletonList("&7Click to go back"));

        ItemStack complete = XMaterial.ANVIL.parseItem();
        utils.setDisplayName(complete, "&b&lApply changes");
        utils.setLore(complete, Collections.singletonList("&7Click to complete changes"));

        ItemStack rename = XMaterial.NAME_TAG.parseItem();
        utils.setDisplayName(rename, "&b&lChange title");
        utils.setLore(rename, Collections.singletonList("&7Click to change the gui title"));

        ItemStack addRow = XMaterial.PLAYER_HEAD.parseItem();
        utils.applyTexture(addRow, "3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716");
        utils.setDisplayName(addRow, "&b&lAdd row");
        utils.setLore(addRow, Collections.singletonList("&7Adds a row"));

        ItemStack deleteRow = XMaterial.PLAYER_HEAD.parseItem();
        utils.applyTexture(deleteRow, "bd8a99db2c37ec71d7199cd52639981a7513ce9cca9626a3936f965b131193");
        utils.setDisplayName(deleteRow, "&b&lRemove row");
        utils.setLore(deleteRow, Collections.singletonList("&7Deletes a row"));


        p.getInventory().setItem(3, back);
        p.getInventory().setItem(5, complete);
        p.getInventory().setItem(19, rename);
        p.getInventory().setItem(23, deleteRow);
        p.getInventory().setItem(25, addRow);
    }

    /**
     * Iterates throughout the player's inventory saving the items and their position.
     * Also clears the player inventory except the armor
     *
     * @return the map with the items and it's position
     */
    private Map<Integer, ItemStack> withdrawPlayerItems() {
        Map<Integer, ItemStack> items = new LinkedHashMap<>();
        Inventory pInv = p.getInventory();

        IntStream.range(0, 36).forEach(i -> {
            if (utils.isEmpty(pInv.getItem(i)))
                return;

            items.put(i, pInv.getItem(i));
            pInv.clear(i);
        });

        return items;
    }

    /**
     * Should clear the customizer items and give back the
     * previous player withdraw items
     */
    private void depositPlayerItems() {
        IntStream.range(0, 36).forEach(i -> p.getInventory().clear(i));

        pItems.forEach((i, item) ->
                p.getInventory().setItem(i, item));
    }

    /**
     * Opens again the inv for the player
     */
    public void refresh() {
        utils.runTaskLater(() -> {
            resfreshFlag = true;
            addCustomizeItems();
            p.openInventory(inv);
            resfreshFlag = false;
        }, 1L);
    }


    @Override
    public Inventory getInventory() {
        dGui gui = shop.getGui();
        Inventory inventory = Bukkit.createInventory(this,
                gui.getInventory().getSize(), title);

        inventory.setContents(gui.getInventory().getContents());
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getWhoClicked().getUniqueId().equals(p.getUniqueId()))
            return;

        e.setCancelled(true);

        if (e.getSlot() == -999) return;            //avoid errors

        if (e.getRawSlot() > (inv.getSize() - 1)) {        // En el player inventory

            if (utils.isEmpty(e.getCurrentItem()))
                return;

            if (e.getSlot() == 3) {
                preventClose = false;
                p.closeInventory();
            }

            else if (e.getSlot() == 5) {   //apply changes
                shop.updateGui(inv);
                preventClose = false;
                p.closeInventory();
            }

            else if (e.getSlot() == 19) {
                resfreshFlag = true;
                new AnvilGUI.Builder()
                        .onClose(player -> utils.runTaskLater(this::refresh, 1L))
                        .onComplete((player, s) -> {
                            title = utils.formatString(s);
                            Inventory aux = Bukkit.createInventory(this, inv.getSize(), title);
                            utils.translateContents(inv, aux);
                            inv = aux;
                            refresh();
                            return AnvilGUI.Response.close();
                        })
                        .text("Input new title")
                        .itemLeft(XMaterial.NAME_TAG.parseItem())
                        .plugin(plugin)
                        .open(p);
            }

            else if (e.getSlot() == 23) {
                if (inv.getSize() == 9) return;

                Inventory aux = Bukkit.createInventory(this, inv.getSize() - 9, title);
                utils.translateContents(inv, aux);
                inv = aux;
                refresh();
            }

            else if (e.getSlot() == 25) {
                if (inv.getSize() == 54) return;

                Inventory aux = Bukkit.createInventory(this, inv.getSize() + 9, title);
                utils.translateContents(inv, aux);
                inv = aux;
                refresh();
            }
        }

        else {          //si le da arriba
            resfreshFlag = true;
            new miniCustomizeGui(p, utils.isEmpty(e.getCurrentItem()) ?
                    XMaterial.GRASS_BLOCK.parseItem() : e.getCurrentItem(),
                    item -> {
                        inv.setItem(e.getSlot(), item);
                        refresh();
            });
        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (!e.getWhoClicked().getUniqueId().equals(p.getUniqueId()))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!e.getPlayer().getUniqueId().equals(p.getUniqueId()))
            return;

        if (resfreshFlag)
            return;

        if (preventClose) {
            refresh();
            return;
        }

        shop.getGui().setAvailable(true);
        depositPlayerItems();

        unregisterAll();
        utils.runTaskLater(() -> shopsManagerGui.open(p), 1L);

    }

    @EventHandler
    public void onPickUpItemEvent(PlayerPickupItemEvent e) {
        if (e.getPlayer().getUniqueId().equals(p.getUniqueId()))
            e.setCancelled(true);
    }

    /**
     * Unregisters all the events of this instance
     */
    private void unregisterAll() {
        InventoryCloseEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);
        PlayerPickupItemEvent.getHandlerList().unregister(this);
    }




}

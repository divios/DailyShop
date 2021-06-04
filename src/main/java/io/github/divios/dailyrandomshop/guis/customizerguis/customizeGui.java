package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.settings.shopsManagerGui;
import io.github.divios.dailyrandomshop.utils.utils;
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

import java.util.LinkedHashMap;
import java.util.Map;
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

        Task.syncDelayed(plugin, () ->
                Bukkit.getPluginManager().registerEvents(this, plugin), 1L);

        pItems = withdrawPlayerItems();

        inv = getInventory();
        addCustomizeItems();
        refresh();

    }

    public static void open(Player p, dShop shop) {
        if (!shop.getGui().getAvailable()) {
            p.sendMessage(conf_msg.PREFIX + FormatUtils.color("&7Someone is already editing this gui"));
            return;
        }
        shop.getGui().setAvailable(false);
        shop.getGui().closeAll();               //Close all viewers

        new customizeGui(p, shop);
    }

    public void addCustomizeItems() {
        ItemStack back = new ItemBuilder(XMaterial.SPRUCE_SIGN)
                .setName("&b&lGo back").setLore("&7Click to go back");

        ItemStack complete = new ItemBuilder(XMaterial.ANVIL)
                .setName("&b&lApply changes").setLore("&7Click to complete changes");

        ItemStack rename = new ItemBuilder(XMaterial.NAME_TAG)
                .setName("&b&lChange title").setLore("&7Click to change the gui title");

        ItemStack addRow = new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setName("&b&lAdd row").setLore("&7Adds a row")
                .applyTexture("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716");

        ItemStack deleteRow = new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setName("&7Deletes a row").setLore("&b&lRemove row")
                .applyTexture("bd8a99db2c37ec71d7199cd52639981a7513ce9cca9626a3936f965b131193");

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
        Task.syncDelayed(plugin, () -> {
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
        if (e.getInventory().getHolder() != this)
            return;

        e.setCancelled(true);

        if (e.getSlot() == -999) return;            //avoid errors

        if (e.getRawSlot() > (inv.getSize() - 1)) {        // En el player inventory

            if (utils.isEmpty(e.getCurrentItem()))
                return;

            if (e.getSlot() == 3) {  //back
                preventClose = false;
                p.closeInventory();
            }

            else if (e.getSlot() == 5) {   //apply changes
                shop.updateGui(title, inv);
                preventClose = false;
                p.closeInventory();
            }

            else if (e.getSlot() == 19) {           //change Name
                resfreshFlag = true;
                new AnvilGUI.Builder()
                        .onClose(player -> Task.syncDelayed(plugin, this::refresh, 1L))
                        .onComplete((player, s) -> {
                            title = FormatUtils.color(s);
                            Inventory aux = Bukkit.createInventory(this, inv.getSize(), title);
                            inventoryUtils.translateContents(inv, aux);
                            inv = aux;
                            refresh();
                            return AnvilGUI.Response.close();
                        })
                        .text("Input new title")
                        .itemLeft(XMaterial.NAME_TAG.parseItem())
                        .plugin(plugin)
                        .open(p);
            }

            else if (e.getSlot() == 23) {           //quitar row
                if (inv.getSize() == 9) return;

                Inventory aux = Bukkit.createInventory(this, inv.getSize() - 9, title);
                inventoryUtils.translateContents(inv, aux);
                inv = aux;
                refresh();
            }

            else if (e.getSlot() == 25) {           //ampliar row
                if (inv.getSize() == 54) return;

                Inventory aux = Bukkit.createInventory(this, inv.getSize() + 9, title);
                inventoryUtils.translateContents(inv, aux);
                inv = aux;
                refresh();
            }
        }

        else {          //si le da arriba

            if (e.isRightClick()) {
                inv.clear(e.getSlot());
                return;
            }

            resfreshFlag = true;
            new miniCustomizeGui(p,
                    utils.isEmpty(e.getCurrentItem()) ?
                         XMaterial.GRASS_BLOCK.parseItem() : e.getCurrentItem().clone(),
                    item -> {
                        inv.setItem(e.getSlot(), item);
                        refresh();
            });
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() != this)
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() != this)
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
        Task.syncDelayed(plugin, () -> shopsManagerGui.open(p), 1L);

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

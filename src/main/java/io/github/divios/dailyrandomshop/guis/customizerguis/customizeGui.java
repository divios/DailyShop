package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.confirmIH;
import io.github.divios.dailyrandomshop.guis.settings.shopsManagerGui;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.lib.itemHolder.dGui;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class customizeGui implements Listener, InventoryHolder {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final dShop shop;
    private final dGui _gui;
    private Inventory inv;

    private boolean preventClose = true;
    private boolean refreshFlag = false;

    private final Map<Integer, ItemStack> pItems = new LinkedHashMap<>();

    private customizeGui(Player p, dShop shop) {
        this.p = p;
        this.shop = shop;
        this._gui = shop.getGui().clone();
        this.inv = shop.getGui().getInventory();

        Task.syncDelayed(plugin, () ->
                Bukkit.getPluginManager().registerEvents(this, plugin), 1L);

        withdrawPlayerItems();

        addCustomizeItems();
        refresh();          // opens the inventory for the player

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
                .setName("&b&lRemove row").setLore("&7Deletes a row")
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
    private void withdrawPlayerItems() {

        Inventory pInv = p.getInventory();

        IntStream.range(0, 36).forEach(i -> {
            if (utils.isEmpty(pInv.getItem(i)))
                return;

            pItems.put(i, pInv.getItem(i));
            pInv.clear(i);
        });

    }

    /**
     * Should clear the customizer items and give back the
     * previous player withdraw items
     */
    private void depositPlayerItems() {
        IntStream.range(0, 36).forEach(i -> p.getInventory().clear(i));

        pItems.forEach((i, item) ->
                p.getInventory().setItem(i, item));
        pItems.clear();
    }

    /**
     * Opens again the inv for the player
     */
    public void refresh() {
        inv = _gui.getInventory();
        Task.syncDelayed(plugin, () -> {
            refreshFlag = true;
            addCustomizeItems();
            p.openInventory(inv);
            refreshFlag = false;
        }, 1L);
    }


    @Override
    public @NotNull Inventory getInventory() {
        return shop.getGui().getInventory();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory() != inv)
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
                shop.updateGui(_gui);
                preventClose = false;
                p.closeInventory();
            }

            else if (e.getSlot() == 19) {           //change Name
                refreshFlag = true;
                new AnvilGUI.Builder()
                        .onClose(player -> Task.syncDelayed(plugin, this::refresh, 1L))
                        .onComplete((player, s) -> {
                            _gui.setTitle(FormatUtils.color(s));
                            refresh();
                            return AnvilGUI.Response.close();
                        })
                        .text("Input new title")
                        .itemLeft(XMaterial.NAME_TAG.parseItem())
                        .plugin(plugin)
                        .open(p);
            }

            else if (e.getSlot() == 23) {           //quitar row
                if (_gui.removeRow())
                    refresh();
            }

            else if (e.getSlot() == 25) {           //ampliar row
                if (_gui.addRow())
                    refresh();
            }
        }

        else {          //si le da arriba

            if (utils.isEmpty(e.getCurrentItem())
                    && e.getClick().equals(ClickType.MIDDLE)) {  //add empty slot
                _gui.addButton(dItem.empty(), e.getSlot());
                refresh();
                return;
            }

            if (e.isRightClick() && !utils.isEmpty(e.getCurrentItem())) {  // delete item
                refreshFlag = true;
                new confirmIH(p, (player, aBoolean) -> {
                    if (aBoolean) _gui.removeButton(e.getSlot());
                    refresh();
                }, e.getCurrentItem(), "&a&lConfirm Action",
                        "", "");
                refreshFlag = false;
                return;
            }

            refreshFlag = true;
            depositPlayerItems();
            new miniCustomizeGui(p,         // customize item
                    utils.isEmpty(e.getCurrentItem()) ?
                         XMaterial.GRASS_BLOCK.parseItem() : e.getCurrentItem().clone(),
                    item -> {
                        _gui.addButton(new dItem(item), e.getSlot());
                        withdrawPlayerItems();
                        refresh();
            });
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory() != inv)
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory() != inv)
            return;

        if (refreshFlag)
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

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (e.getPlayer().getUniqueId().equals(p.getUniqueId()))
            shop.getGui().setAvailable(true);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        if (e.getPlayer().getUniqueId().equals(p.getUniqueId()))
            shop.getGui().setAvailable(true);
    }

    /**
     * Unregisters all the events of this instance
     */
    private void unregisterAll() {
        InventoryCloseEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);
        PlayerPickupItemEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerKickEvent.getHandlerList().unregister(this);
    }

}

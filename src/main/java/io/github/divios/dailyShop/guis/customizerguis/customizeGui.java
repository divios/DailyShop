package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.core_lib.misc.confirmIH;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateShopEvent;
import io.github.divios.dailyShop.guis.settings.shopsManagerGui;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
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

    private static final DailyShop plugin = DailyShop.getInstance();

    private final Player p;
    private final dShop shop;
    private final dInventory _gui;
    private Inventory inv;

    private boolean preventClose = true;
    private boolean refreshFlag = false;

    private dItem toClone = null;

    private final Map<Integer, ItemStack> pItems = new LinkedHashMap<>();

    private customizeGui(Player p, dShop shop, dInventory inv) {
        this.p = p;
        this.shop = shop;
        this._gui = inv.skeleton();
        this.inv = inv.clone().getInventory();

        Task.syncDelayed(plugin, () ->
                Bukkit.getPluginManager().registerEvents(this, plugin), 1L);

        withdrawPlayerItems();

        addCustomizeItems();
        refresh();          // opens the inventory for the player

    }

    public static void open(Player p, dShop shop, dInventory inv) {
        new customizeGui(p, shop, inv);
    }

    public void addCustomizeItems() {


        IntStream.range(0, 36).forEach(i->
                p.getInventory().setItem(i,
                        ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE).setName("&c")));

        ItemStack back = ItemBuilder.of(XMaterial.PLAYER_HEAD)
                .setName("&b&lGo back").setLore("&7Click to go back")
                .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf");

        ItemStack complete = ItemBuilder.of(XMaterial.PLAYER_HEAD)
                .setName("&b&lApply changes").setLore("&7Click to complete changes")
                .applyTexture("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f");

        ItemStack rename = ItemBuilder.of(XMaterial.NAME_TAG)
                .setName("&b&lChange title").setLore("&7Click to change the gui title");

        ItemStack addRow = ItemBuilder.of(XMaterial.PLAYER_HEAD)
                .setName("&b&lAdd row").setLore("&7Adds a row")
                .applyTexture("3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716");

        ItemStack deleteRow = ItemBuilder.of(XMaterial.PLAYER_HEAD)
                .setName("&b&lRemove row").setLore("&7Deletes a row")
                .applyTexture("bd8a99db2c37ec71d7199cd52639981a7513ce9cca9626a3936f965b131193");

        ItemStack info = ItemBuilder.of(XMaterial.PAPER)
                .setName("&8> &6Info")
                .addLore("&7The main idea is to customize",
                        "&7the shop as you want", "&7an leave empty the slots", "&7where the daily items will appear",
                        "",
                        "&8- &6Left click empty slot", "   &7Adds a new item",
                        "&8- &6Shift Click empty slot", "   &7Sets slot as AIR,", "   &7where no daily items",
                            "   &7'll appear and is displayed", "   &7as an empty slot",
                        "&8- &6Middle click item", "   &7Copies the item to the clipboard,",
                        "   &7middle click again on", "   &7empty slot to paste");

        p.getInventory().setItem(3, back);
        p.getInventory().setItem(5, complete);
        p.getInventory().setItem(19, rename);
        p.getInventory().setItem(23, deleteRow);
        p.getInventory().setItem(25, addRow);
        p.getInventory().setItem(8, info);
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
        return inv;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true);

        if (e.getSlot() == -999) return;            //avoid errors

        if (e.getRawSlot() > (inv.getSize() - 1)) {        // En el player inventory

            if (utils.isEmpty(e.getCurrentItem()))
                return;

            if (e.getSlot() == 3) {  //back
                Bukkit.getPluginManager().callEvent(new updateShopEvent(shop, _gui, false));
                preventClose = false;
                p.closeInventory();
            }

            else if (e.getSlot() == 5) {   //apply changes

                Bukkit.getPluginManager().callEvent(new updateShopEvent(shop, _gui, true));
                preventClose = false;
                p.closeInventory();
            }

            else if (e.getSlot() == 19) {           //change Name
                refreshFlag = true;
                ChatPrompt.prompt(plugin, p , (s) -> {
                    _gui.setTitle(FormatUtils.color(s));
                    Task.syncDelayed(plugin, this::refresh);
                }, cause -> Task.syncDelayed(plugin, this::refresh),
                        "&5&lInput New Title", "");
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

            if (toClone != null && utils.isEmpty(e.getCurrentItem())
                && e.getClick().equals(ClickType.MIDDLE)) {     // paste clipboard
                _gui.addButton(toClone.clone2(), e.getSlot());
                refresh();
                return;
            }

            if (!utils.isEmpty(e.getCurrentItem()) &&
                    e.getClick().equals(ClickType.MIDDLE)) {        // copy to clipboard
                toClone = dItem.of(e.getCurrentItem());
                return;
            }

            if (utils.isEmpty(e.getCurrentItem())
                    && e.isShiftClick()) {  //add empty slot
                _gui.addButton(dItem.AIR(), e.getSlot());
                refresh();
                return;
            }

            if (e.isRightClick() && !utils.isEmpty(e.getCurrentItem())) {  // delete item
                refreshFlag = true;

                confirmIH.builder()
                        .withPlayer(p)
                        .withAction(aBoolean -> {
                            if (aBoolean) _gui.removeButton(e.getSlot());
                            refresh();
                        })
                        .withItem(e.getCurrentItem())
                        .withTitle("&a&lConfirm Action")
                        .withConfirmLore(plugin.configM.getLangYml().CONFIRM_GUI_YES, plugin.configM.getLangYml().CONFIRM_GUI_YES_LORE)
                        .withCancelLore(plugin.configM.getLangYml().CONFIRM_GUI_NO, plugin.configM.getLangYml().CONFIRM_GUI_NO_LORE)
                        .prompt();

                refreshFlag = false;
                return;
            }

            refreshFlag = true;
            depositPlayerItems();
            new miniCustomizeGui(p, shop,        // customize item
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
        if (!e.getInventory().equals(inv))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!e.getInventory().equals(inv))
            return;

        if (refreshFlag)
            return;

        if (preventClose) {
            refresh();
            return;
        }


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
        if (e.getPlayer().getUniqueId().equals(p.getUniqueId())) {

        }

    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        if (e.getPlayer().getUniqueId().equals(p.getUniqueId())) {

        }

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

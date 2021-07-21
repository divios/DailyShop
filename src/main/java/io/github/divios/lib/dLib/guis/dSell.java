package io.github.divios.lib.dLib.guis;

import io.github.divios.core_lib.inventory.inventoryUtils;
import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dGui;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class dSell extends dGui {

    private final HashMap<UUID, Inventory> inventories = new HashMap<>();
    private EventListener<InventoryCloseEvent> closeEvent;

    private boolean closeFlag = false;

    public dSell(String title, Inventory inv, dShop shop) {
        super(title, inv, shop);
    }

    public dSell(String title, int size, dShop shop) {
        super(title, size, shop);
    }

    public dSell(String base64, dShop shop) {
        super(base64, shop);
    }

    public dSell(dShop shop) {
        super(shop);
    }

    @Override
    public void closeAll() {
        inv.getViewers().forEach(humanEntity -> {

            if (Bukkit.getPlayer(humanEntity.getUniqueId()) == null) return;

            Optional.ofNullable(inventories.get(humanEntity.getUniqueId()))
                    .ifPresent(inventory ->
                            openSlots.forEach(slot -> {
                                if (utils.isEmpty(inventory.getItem(slot))) return;
                                humanEntity.getInventory().addItem(inventory.getItem(slot));
                            }));

            try {
                humanEntity.closeInventory();
            } catch (Exception ignored) { }
        });
    }

    @Override
    protected void _renovate(dItem newItem, int slot) {
        newItem.setSlot(slot);
        buttons.add(newItem);
        openSlots.remove(slot);
    }

    @Override
    protected void updateItem(dItem item, updateItemEvent.updatetype type) {
        // shouldn't be anything else to do
    }

    @Override
    protected void initListeners() {

        this.clickEvent = new EventListener<>(InventoryClickEvent.class,
                e -> {

                    if (e.getInventory() != inventories.get(e.getWhoClicked().getUniqueId())) return;

                    if (e.getClickedInventory() == inventories.get(e.getWhoClicked().getUniqueId())
                            && !openSlots.contains(e.getSlot())) {                          // if not open slot, cancel
                        e.setCancelled(true);                                               // and run action

                        buttons.stream()
                                .filter(dItem -> dItem.getSlot() == e.getSlot())
                                .findFirst().ifPresent(dItem -> dItem.getAction().stream((dAction, s) ->
                                dAction.run((Player) e.getWhoClicked(), s)));

                    } else if (e.getInventory() != e.getClickedInventory()) {   // click al inventory del player

                        if (buttons.stream()
                                .noneMatch(dItem -> dItem.getRawItem().isSimilar(e.getCurrentItem()))) {   // not for sale

                            if (utils.isEmpty(e.getCurrentItem())) return;

                            e.setCancelled(true);
                            Msg.sendMsg((Player) e.getWhoClicked(), "&7This shop doesnt admit that item");
                        }

                        else if (e.isShiftClick()) {          // Si hace shift click
                            e.setCancelled(true);
                            autoInsertItems(e.getCurrentItem(), inventories.get(e.getWhoClicked().getUniqueId()));
                        }
                    }

                });

        closeEvent = new EventListener<>(InventoryCloseEvent.class,
                e -> {
                    if (e.getInventory() != inventories.get(e.getPlayer().getUniqueId())) return;

                    if (closeFlag) return;

                    Optional.ofNullable(inventories.get(e.getPlayer().getUniqueId()))
                            .ifPresent(inventory ->
                                    openSlots.forEach(slot -> {
                                            if (utils.isEmpty(inventory.getItem(slot))) return;
                                            e.getPlayer().getInventory().addItem(inventory.getItem(slot));
                                    }));
                });


        this.dragEvent = new EventListener<>(InventoryDragEvent.class,
                e -> {
                    if (e.getInventory() != inventories.get(e.getWhoClicked().getUniqueId())) return;

                    if (e.getNewItems().keySet().stream().anyMatch(i -> !openSlots.contains(i)))
                        e.setCancelled(true);

                });

        this.openEvent = new EventListener<>(InventoryOpenEvent.class,
                EventPriority.HIGHEST, e -> {
            if (e.getInventory() != inv) return;

            e.setCancelled(true);

            if (!available) {
                e.getPlayer().sendMessage("The shop is closed... come again in some minutes");
            }

            Inventory cloned = inventoryUtils.cloneInventory(inv, title);
            e.getPlayer().openInventory(cloned);

            inventories.put(e.getPlayer().getUniqueId(), cloned);

        });
    }

    @Override
    protected void destroy() {
        clickEvent.unregister();
        dragEvent.unregister();
        openEvent.unregister();
        closeEvent.unregister();
    }

    private void autoInsertItems(ItemStack item, Inventory inventory) {

        if (utils.isEmpty(item) || item.getAmount() == 0) return;

        boolean firstFase = false;
       for (int i : openSlots) {

           if (utils.isEmpty(inventory.getItem(i))) continue;

           ItemStack invItem = inventory.getItem(i);

           if (invItem.isSimilar(item)) {
               firstFase = true;
               if (64 - invItem.getAmount() >= item.getAmount()) {
                   invItem.setAmount(invItem.getAmount() + item.getAmount());
                   item.setAmount(0);

               } else {
                   item.setAmount(item.getAmount() - (64 - invItem.getAmount()));
                   invItem.setAmount(64);
               }
               inventory.setItem(i, invItem);
               break;
           }
       }

       if (firstFase) return;

        openSlots.stream()         // Checks for empty slots on openSlots
                .filter(slot -> utils.isEmpty(inv.getItem(slot)))
                .findFirst()
                .ifPresent(slot -> inv.setItem(slot, item));


    }


}

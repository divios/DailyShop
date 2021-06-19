package io.github.divios.lib.itemHolder.guis;

import io.github.divios.core_lib.misc.EventListener;
import io.github.divios.dailyrandomshop.events.updateItemEvent;
import io.github.divios.dailyrandomshop.lorestategy.shopItemsLore;
import io.github.divios.dailyrandomshop.transaction.transaction;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.lib.itemHolder.dGui;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.stream.IntStream;

public class dBuy extends dGui {

    public dBuy(String title, Inventory inv, dShop shop) {
        super(title, inv, shop);
    }

    public dBuy(String title, int size, dShop shop) {
        super(title, size, shop);
    }

    public dBuy(String base64, dShop shop) {
        super(base64, shop);
    }

    public dBuy(dShop shop) { super(shop); }

    @Override
    public void closeAll() {
        inv.getViewers().forEach(humanEntity -> {
            try {
                humanEntity.closeInventory();
            } catch (Exception ignored) {}
        });
    }

    @Override
    protected void _renovate(dItem newItem, int slot) {
        newItem.setSlot(slot);
        buttons.add(newItem);
        inv.setItem(slot, newItem.getItem());
    }

    @Override
    protected void updateItem(dItem item, updateItemEvent.updatetype type) {
        if (super.buttons.stream().noneMatch(dItem -> dItem.getUid().equals(item.getUid()))) return;

        buttons.stream().filter(dItem -> dItem.getUid().equals(item.getUid()))
                .findFirst()
                .ifPresent(dItem -> {

                    if (type.equals(updateItemEvent.updatetype.UPDATE_ITEM)) {

                        item.setSlot(dItem.getSlot());
                        buttons.remove(dItem);
                        buttons.add(item);
                        ItemStack itemWithLore = item.getItem().clone();
                        strategy.setLore(itemWithLore);
                        inv.setItem(dItem.getSlot(), itemWithLore);

                    } else if (type.equals(updateItemEvent.updatetype.NEXT_AMOUNT)) {

                        dItem.setStock(dItem.getStock().get() - 1);

                        if (dItem.getStock().get() <= 0) {
                            buttons.remove(dItem);
                            inv.setItem(dItem.getSlot(), utils.getRedPane());
                        } else
                            inv.getItem(dItem.getSlot()).setAmount(dItem.getStock().get());

                    } else if (type.equals(updateItemEvent.updatetype.DELETE_ITEM)) {

                        buttons.stream()
                                .filter(dItem1 -> dItem1.getUid().equals(item.getUid()))
                                .findFirst()
                                .ifPresent(dItem1 -> {
                                    buttons.remove(dItem1);
                                    inv.setItem(dItem1.getSlot(), utils.getRedPane());
                                });
                    }
                });
    }

    @Override
    protected void initListeners() {
        super.clickEvent = new EventListener<>(plugin, InventoryClickEvent.class,
                EventPriority.HIGHEST, e -> {

            if (e.getInventory() != inv) return;

            e.setCancelled(true);

            if (utils.isEmpty(e.getCurrentItem())) return;

            if (openSlots.contains(e.getSlot()) && e.isLeftClick())
                buttons.stream()
                        .filter(ditem -> ditem.getUid().equals(dItem.of(e.getCurrentItem()).getUid()))
                        .findFirst()
                        .ifPresent(dItem -> transaction.init(
                                (Player) e.getWhoClicked(), dItem, shop));

            else if (openSlots.contains(e.getSlot()) && e.isRightClick()) {
                //TODO: sell things
            }

            else {

                buttons.stream().filter(dItem -> dItem.getSlot() == e.getSlot())
                        .findFirst().ifPresent(dItem -> dItem.getAction()
                        .stream((dAction, s) -> dAction.run((Player) e.getWhoClicked(), s)));
            }

        });

        super.dragEvent = new EventListener<>(plugin, InventoryDragEvent.class,
                e -> {
                    if (e.getInventory() != inv) return;

                    e.setCancelled(true);

                });

        super.openEvent = new EventListener<>(plugin, InventoryOpenEvent.class,
                EventPriority.HIGHEST, e -> {
            if (e.getInventory() != inv) return;

            if (!available) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("The shop is closed... come again in some minutes");
            }
        });
    }

    @Override
    protected void destroy() {
        clickEvent.unregister();
        dragEvent.unregister();
        openEvent.unregister();
    }


}

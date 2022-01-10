package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class dragAndSellGui {

    private final Player p;
    private final Inventory gui;
    private final Set<Subscription> listeners;

    public static void promptGui(Player p) {
        new dragAndSellGui(p);
    }

    private dragAndSellGui(Player p) {
        this.p = p;
        this.gui = Bukkit.createInventory(null, 36, "");
        this.listeners = new HashSet<>();

        fillGuiWithPanels();
        updateDoneButton(0);

        createListeners();
        p.openInventory(gui);
    }

    private void fillGuiWithPanels() {
        ItemStack fillItem = ItemUtils.setName(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), "&7");
        IntStream.range(27, 36).forEach(index -> gui.setItem(index, fillItem));
    }

    private void updateDoneButton(double price) {
        ItemStack doneButton = ItemUtils.setName(XMaterial.GREEN_STAINED_GLASS_PANE.parseItem(),
                "Total Price: " + price);
        gui.setItem(31, doneButton);
    }

    private void createListeners() {
        listeners.add(
                Events.subscribe(InventoryClickEvent.class)
                        .filter(e -> e.getInventory().equals(gui))
                        .handler(e -> {
                            if (e.getSlot() != e.getRawSlot())
                                buttonGuiAction(e);
                            else
                                upperGuiAction(e);
                        })
        );

        listeners.add(
                Events.subscribe(InventoryCloseEvent.class)
                        .filter(e -> e.getInventory().equals(gui))
                        .handler(e -> {
                            Iterator<Subscription> iterator = listeners.iterator();
                            while (iterator.hasNext()) {
                                iterator.next().unregister();
                                iterator.remove();
                            }
                            givePlayerItemsBack();
                        })
        );
    }

    private void buttonGuiAction(InventoryClickEvent e) {
        if (!ItemUtils.isEmpty(e.getCurrentItem())
                && getItemPrice(e.getCurrentItem()) <= 0) {
            e.setCancelled(true);
            Messages.MSG_INVALID_SELL.send(p);
        }
        if (e.isLeftClick() && e.isShiftClick())
            Schedulers.sync().run(() -> updateDoneButton(recalculatePrice()));
    }

    private void upperGuiAction(InventoryClickEvent e) {
        if (e.getSlot() >= 27 && e.getSlot() < 36) {
            e.setCancelled(true);
            if (e.getSlot() == 31) {
                startTransaction();
            }
        }

        if (e.getSlot() >= 0 && e.getSlot() < 27) {
            Schedulers.sync().run(() -> updateDoneButton(recalculatePrice()));
        }
    }

    private double recalculatePrice() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        double finalPrice = IntStream.range(0, 27)
                .mapToObj(gui::getItem)
                .filter(Objects::nonNull)
                .mapToDouble(this::getItemPrice)
                .sum();

        DebugLog.info("Time elapsed to recalculate price: " + (System.currentTimeMillis() - timestamp.getTime()) + " ms");
        return finalPrice;
    }

    /**
     * Searches the sell price of the item. If <=0, means it cannot be sold
     */
    private double getItemPrice(ItemStack itemToLook) {
        double itemPrice = 0;
        for (dShop shop : DailyShop.get().getShopsManager().getShops()) {
            boolean found = false;
            for (dItem item : shop.getCurrentItems()) {
                if (item.getItem().isSimilar(itemToLook)) {
                    found = true;
                    itemPrice = item.getPlayerSellPrice(p, shop);
                    break;
                }
            }
            if (found) break;
        }
        return itemPrice;
    }


    private void startTransaction() {
        IntStream.range(0, 27).forEach(index -> {
            ItemStack itemToSell = gui.getItem(index);
            if (ItemUtils.isEmpty(itemToSell)) return;
            for (dShop shop : DailyShop.get().getShopsManager().getShops()) {
                boolean done = false;
                for (dItem currentItem : shop.getCurrentItems()) {
                    if (currentItem.getItem().isSimilar(itemToSell)) {
                        done = true;

                        break;
                    }
                }
                if (done) break;
            }
        });
    }

    private void givePlayerItemsBack() {
        IntStream.range(0, 27)
                .mapToObj(gui::getItem)
                .filter(Objects::nonNull)
                .forEach(itemStack -> ItemUtils.give(p, itemStack));
    }


}

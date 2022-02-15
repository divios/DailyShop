package io.github.divios.lib.dLib.shop;

import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.dailyShop.utils.NMSUtils.SetSlotPacket;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.util.DailyItemsMap;
import io.github.divios.lib.dLib.shop.util.NMSContainerID;
import io.github.divios.lib.dLib.shop.factory.DailyItemFactory;
import io.github.divios.lib.dLib.shop.buttons.PaneButton;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class ShopGui_ {

    private final DailyItemFactory itemFactory;

    private final ConcurrentHashMap<UUID, Player> viewers;

    private final ConcurrentHashMap<Integer, dItem> buttons;
    private final DailyItemsMap dailyItemsMap;

    private UpdateTask updateTask;

    private ButtonGui gui;

    ShopGui_(String title, Inventory inv, DailyItemFactory itemFactory) {
        this.itemFactory = itemFactory;

        this.viewers = new ConcurrentHashMap<>();

        this.buttons = new ConcurrentHashMap<>();
        this.dailyItemsMap = new DailyItemsMap();

        gui = new ButtonGui(title, inv);
        gui.setOnClose(e -> viewers.remove(e.getPlayer().getUniqueId()));
    }

    public void open(Player p) {
        viewers.put(p.getUniqueId(), p);
        gui.open(p);

        if (updateTask == null) updateTask = new UpdateTask();
        updateTask.run();
    }

    public void close(Player p) {
        Player closedPlayer;
        if ((closedPlayer = viewers.remove(p.getUniqueId())) == null) return;

        closedPlayer.closeInventory();
    }

    public void setPaneItem(int slot, dItem item) {
        gui.setButton(slot, new PaneButton(item));
        buttons.put(slot, item);
    }

    public void setDailyItems(Queue<dItem> dailyItems) {
        clearDailyItems();

        for (int index = 0; index < gui.getSize(); index++) {
            if (buttons.containsKey(index)) continue;

            dItem item;
            if ((item = dailyItems.poll()) == null) break;     // dailyItems is empty

            setDailyItem(index, item);
        }
        if (updateTask != null) updateTask.run();
    }

    private void setDailyItem(int slot, dItem item) {
        Validate.isTrue(!buttons.containsKey(slot), "Cannot set a dailyItem in a button slot");

        dItem clone = item.clone();
        clone.generateNewBuyPrice();
        clone.generateNewSellPrice();
        clone.setSlot(slot);

        dailyItemsMap.put(clone);
        gui.setButton(slot, itemFactory.createButton(item));
    }

    private void updateDailyItem(dItem updatedItem) {
        dItem oldItem;
        if ((oldItem = dailyItemsMap.get(updatedItem.getID())) == null) return;

        int slot = oldItem.getSlot();
        setDailyItem(slot, updatedItem);
    }

    private void removeDailyItem(String id) {
        dItem item;
        if ((item = dailyItemsMap.remove(id)) == null) return;

        gui.removeButton(item.getSlot());
    }

    private void clearDailyItems() {
        for (Iterator<dItem> iterator = dailyItemsMap.iterator(); iterator.hasNext(); ) {
            gui.clear(iterator.next().getSlot());
            iterator.remove();
        }
    }

    private void destroy() {
        if (updateTask != null) updateTask.stop();
        gui.destroy();
    }

    @Override
    public String toString() {
        return gui.toString();
    }

    private static final ExecutorService asyncPool = Executors.newCachedThreadPool();

    private class UpdateTask {

        private final Task task;

        public UpdateTask() {
            task = Schedulers.async().runRepeating(
                    this::run,
                    0, TimeUnit.SECONDS,
                    200, TimeUnit.MILLISECONDS
            );
        }

        private void run() {
            if (viewers.isEmpty()) return;
            viewers.forEach((uuid, player) -> asyncPool.execute(() -> sendPackets(player)));
        }

        private void sendPackets(Player player) {
            buttons.forEach((integer, dItem) -> {
                if (dItem.isAir()) return;

                ItemStack aux = dItem.getItem();
                ItemStack toSend = aux.clone();

                String newName = Utils.JTEXT_PARSER.parse(ItemUtils.getName(aux), player);
                List<String> newLore = Utils.JTEXT_PARSER.parse(ItemUtils.getLore(aux), player);

                toSend = ItemUtils.setName(toSend, newName);
                toSend = ItemUtils.setLore(toSend, newLore);

                try {
                    SetSlotPacket.send(player, toSend, dItem.getSlot(), NMSContainerID.getPlayerInventoryID(player));
                } catch (Exception ignored) {
                }        // WindowId can throw null pointer due to no synchronization
            });

            dailyItemsMap.forEach(dItem -> {
                ItemStack toSend = itemFactory.createButton(dItem, player).getItem();
                try {
                    SetSlotPacket.send(player, toSend, dItem.getSlot(), NMSContainerID.getPlayerInventoryID(player));
                } catch (Exception ignored) {
                }        // WindowId can throw null pointer due to no synchronization
            });
        }

        public void stop() {
            task.stop();
        }

    }

    public class dailyItems implements Map<String, dItem> {

        @Override
        public int size() {
            return dailyItemsMap.size();
        }

        @Override
        public boolean isEmpty() {
            return dailyItemsMap.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return dailyItemsMap.contains((String) key);
        }

        @Override
        public boolean containsValue(Object value) {
            boolean contains = false;
            for (Iterator<dItem> iterator = dailyItemsMap.iterator(); iterator.hasNext(); ) {
                if (iterator.next().equals(value)) {
                    contains = true;
                    break;
                }
            }

            return contains;
        }

        @Override
        public dItem get(Object key) {
            return dailyItemsMap.get((String) key);
        }

        @Nullable
        @Override
        public dItem put(String key, dItem value) {
            updateDailyItem(value.setID(key));
            return null;
        }

        @Override
        public dItem remove(Object key) {
            removeDailyItem((String) key);
            return null;
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ? extends dItem> m) {
            m.forEach(this::put);
        }

        @Override
        public void clear() {
            clearDailyItems();
        }

        @NotNull
        @Override
        public Set<String> keySet() {
            return null;
        }

        @NotNull
        @Override
        public Collection<dItem> values() {
            return dailyItemsMap.values();
        }

        @NotNull
        @Override
        public Set<Entry<String, dItem>> entrySet() {
            return null;
        }
    }

}

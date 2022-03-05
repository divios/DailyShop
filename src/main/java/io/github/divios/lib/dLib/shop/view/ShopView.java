package io.github.divios.lib.dLib.shop.view;

import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.view.buttons.DailyItemFactory;
import io.github.divios.lib.dLib.shop.view.buttons.PaneButton;
import io.github.divios.lib.dLib.shop.view.gui.ButtonGui;
import io.github.divios.lib.dLib.shop.view.gui.GuiButtonFactory;
import io.github.divios.lib.dLib.shop.view.util.DailyItemsMap;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class ShopView {

    private DailyItemFactory itemFactory;

    protected final ConcurrentHashMap<Integer, dItem> buttons;
    protected final DailyItemsMap dailyItemsMap;

    UpdateTask updateTask;

    ButtonGui gui;

    public ShopView(String title, Inventory inv, DailyItemFactory itemFactory) {
        this.itemFactory = itemFactory;

        this.buttons = new ConcurrentHashMap<>();
        this.dailyItemsMap = new DailyItemsMap();

        gui = GuiButtonFactory.createMultiGui(title, inv.getSize());
    }

    public void open(@NotNull Player p) {
        gui.open(p);
        if (updateTask == null) updateTask = new UpdateTask();
    }

    public void setTitle(String title) {
        List<HumanEntity> viewers = gui.getViewers();
        ButtonGui newGui = GuiButtonFactory.createMultiGui(title, getSize());

        gui.destroy();
        gui = newGui;

        update();
        viewers.forEach(player -> newGui.open((Player) player));
    }

    public void setSize(int size) {
        int comparator = Integer.compare(getSize(), size);

        if (comparator > 0) {
            decrementRows((getSize() - size) / 9);
        } else if (comparator < 0)
            incrementRows((size - getSize()) / 9);
    }

    public void setItemFactory(DailyItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    public void setPaneItem(int slot, dItem item) {
        dItem dailyItem;
        if ((dailyItem = dailyItemsMap.get(slot)) != null)      // Removed dailyItem if crash
            removeDailyItem(dailyItem.getID());

        item.setSlot(slot);
        gui.setButton(slot, new PaneButton(item));
        buttons.put(slot, item);
    }

    public void clear(int slot) {
        if (buttons.remove(slot) != null) {
            gui.clear(slot);
        } else
            removeDailyItem(slot);
    }

    public void setDailyItems(Queue<dItem> dailyItems) {
        clearDailyItems();

        removeStaticItems(dailyItems).forEach(dItem -> setDailyItem(dItem.getSlot(), dItem));

        for (int index = 0; index < gui.getSize(); index++) {
            if (buttons.containsKey(index) || dailyItemsMap.contains(index)) continue;

            dItem item;
            if ((item = dailyItems.poll()) == null) break;     // dailyItems is empty

            setDailyItem(index, item);
        }
    }

    private Collection<dItem> removeStaticItems(Collection<dItem> items) {
        Set<dItem> staticItems = new HashSet<>();
        for (Iterator<dItem> iterator = items.iterator(); iterator.hasNext(); ) {
            dItem item = iterator.next();
            if (!item.isStaticSlot()) continue;

            iterator.remove();
            staticItems.add(item);
        }

        return staticItems;
    }

    void setDailyItem(int slot, dItem item) {
        Validate.isTrue(!buttons.containsKey(slot), "Cannot set a dailyItem in a button slot");

        dItem clone = item.clone();
        clone.setSlot(slot);

        dailyItemsMap.put(clone);
        gui.setButton(slot, itemFactory.createButton(clone));
    }

    private void updateDailyItem(dItem updatedItem) {
        dItem oldItem;
        if ((oldItem = dailyItemsMap.get(updatedItem.getID())) == null) return;

        int slot = oldItem.getSlot();
        setDailyItem(slot, updatedItem);
        gui.setButton(slot, itemFactory.createButton(updatedItem));
    }

    private void removeDailyItem(int slot) {
        if (dailyItemsMap.remove(slot) != null)
            gui.clear(slot);
    }

    private void removeDailyItem(String id) {
        dItem item;
        if ((item = dailyItemsMap.remove(id)) == null) return;

        gui.clear(item.getSlot());
    }

    private void clearDailyItems() {
        for (Iterator<dItem> iterator = dailyItemsMap.iterator(); iterator.hasNext(); ) {
            gui.clear(iterator.next().getSlot());
            iterator.remove();
        }
    }

    private void setGui(ButtonGui newGui) {
        List<HumanEntity> viewers = gui.getViewers();

        gui.destroy();
        gui = newGui;

        update();
        viewers.forEach(player -> newGui.open((Player) player));
    }

    public void incrementRows(int rows) {
        Validate.isTrue(rows >= 0, "N times cannot be less than 0. Got " + rows);
        if (getSize() == 54) return;

        int newSize = Math.min(54, getSize() + (rows * 9));
        setGui(GuiButtonFactory.createMultiGui(getTitle(), newSize));
    }


    public void decrementRows(int rows) {
        Validate.isTrue(rows >= 0, "N times cannot be less than 0. Got " + rows);
        if (getSize() == 9) return;

        int newSize = Math.max(9, getSize() - (rows * 9));
        ButtonGui newGui = GuiButtonFactory.createMultiGui(getTitle(), newSize);

        // Removed buttons/dailyItems out of newInv bounds
        buttons.entrySet().removeIf(entry -> entry.getKey() >= newSize);
        dailyItemsMap.removeIf(dItem -> dItem.getSlot() >= newSize);

        setGui(newGui);
    }

    public void setState(ShopViewState state) {
        ButtonGui newGui = GuiButtonFactory.createMultiGui(state.getTitle(), state.getSize());

        dailyItemsMap.removeIf(dItem -> dItem.getSlot() >= getSize());
        buttons.clear();

        setGui(newGui);
        state.getButtons().forEach(this::setPaneItem);
    }

    private void update() {
        gui.clear();
        buttons.forEach((integer, dItem) -> gui.setButton(integer, new PaneButton(dItem)));
        dailyItemsMap.forEach(dItem -> gui.setButton(dItem.getSlot(), itemFactory.createButton(dItem)));
    }

    public void destroy() {
        if (updateTask != null) updateTask.stop();
        gui.destroy();
    }

    private dailyItems values = null;
    public dailyItems getDailyItems() {
        if (values == null) values = new dailyItems();
        return values;
    }

    public String getTitle() { return gui.getTitle(); }

    public int getSize() { return gui.getSize(); }

    public Map<Integer, dItem> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }

    public List<HumanEntity> getViewers() {
        return gui.getViewers();
    }

    public ShopViewState toState() {
        return new ShopViewState(getTitle(), getSize(), buttons);
    }

    @Override
    public String toString() {
        return gui.toString();
    }


    private static final ExecutorService asyncPool = Executors.newCachedThreadPool();
    private class UpdateTask {

        private final Task task;

        public UpdateTask() {
            task = Schedulers.async().runRepeating(() -> gui.update(),
                    0, TimeUnit.SECONDS,
                    500, TimeUnit.MILLISECONDS
            );
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

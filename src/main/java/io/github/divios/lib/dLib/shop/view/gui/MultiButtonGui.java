package io.github.divios.lib.dLib.shop.view.gui;

import io.github.divios.lib.dLib.shop.view.buttons.Button;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MultiButtonGui extends ButtonGui {

    private final ButtonGui gui;
    private final ConcurrentHashMap<UUID, ButtonGui> guis;

    public MultiButtonGui(ButtonGui gui) {
        super(gui.getTitle(), gui.getInv());

        this.guis = new ConcurrentHashMap<>();
        this.gui = gui;

        onClose = e -> removeInventory(e.getPlayer().getUniqueId());
    }

    @Override
    public void open(@NotNull Player p) {
        guis.put(p.getUniqueId(), new PlayerButtonGui(p, gui));
    }

    @Override
    public void setOnClose(Consumer<InventoryCloseEvent> onClose) {
        this.onClose = this.onClose.andThen(onClose);
    }

    private void removeInventory(UUID uuid) {
        ButtonGui removed;
        if ((removed = guis.remove(uuid)) == null) return;

        removed.destroy();
    }

    @Override
    public void setButton(int slot, Button button) {
        gui.setButton(slot, button);
        guis.values().forEach(gui1 -> gui1.setButton(slot, button));
    }

    @Override
    public void clear(int slot) {
        gui.clear(slot);
        guis.values().forEach(gui1 -> gui1.clear(slot));
    }

    @Override
    public void clear() {
        gui.clear();
        guis.values().forEach(ButtonGui::clear);
    }

    @Override
    public void update() {
        gui.update();
        guis.values().forEach(ButtonGui::update);
    }

    @Override
    public void destroy() {
        gui.destroy();
        guis.values().forEach(ButtonGui::destroy);
    }

    @Override
    public String getTitle() {
        return gui.getTitle();
    }

    @Override
    public Inventory getInv() {
        return gui.getInv();
    }

    @Override
    public int getSize() {
        return gui.getSize();
    }

    @Override
    public Map<Integer, Button> getButtons() {
        return gui.getButtons();
    }

    @Override
    public List<HumanEntity> getViewers() {
        List<HumanEntity> viewers = new ArrayList<>();
        guis.values().forEach(gui1 -> viewers.addAll(gui1.getViewers()));

        return viewers;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

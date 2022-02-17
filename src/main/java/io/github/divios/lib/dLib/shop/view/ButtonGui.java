package io.github.divios.lib.dLib.shop.view;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.lib.dLib.shop.view.buttons.Button;
import io.github.divios.lib.dLib.shop.view.buttons.EmptyButton;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class ButtonGui {

    private final String title;
    private final Inventory inv;
    private final HashMap<Integer, Button> buttons;

    private Consumer<InventoryCloseEvent> onClose = e -> {
    };

    private final Subscription clickListener;
    private final Subscription closeListener;

    public ButtonGui(String title, Inventory inv) {
        this.title = title;
        this.inv = inv;
        buttons = new HashMap<>();

        clickListener = createClickListener();
        closeListener = createCloseListener();

        populateButtons();
    }

    private void populateButtons() {
        for (int i = 0; i < inv.getSize(); i++)
            buttons.put(i, new EmptyButton());

        buttons.put(-999, new EmptyButton());       // Out of gui
    }

    private Subscription createClickListener() {
        return Events.subscribe(InventoryClickEvent.class)
                .filter(event -> event.getInventory().equals(inv))
                .handler(event -> {
                    event.setCancelled(true);
                    buttons.get(event.getSlot()).execute(event);
                });
    }

    private Subscription createCloseListener() {
        return Events.subscribe(InventoryCloseEvent.class)
                .filter(event -> event.getInventory().equals(inv))
                .handler(event -> onClose.accept(event));
    }

    public void open(Player p) {
        p.openInventory(inv);
    }

    public void setOnClose(Consumer<InventoryCloseEvent> onClose) {
        this.onClose = onClose;
    }

    public void setButton(int slot, Button button) {
        validateSlot(slot);

        buttons.put(slot, button);
        inv.setItem(slot, button.getItem());
    }

    public void removeButton(int slot) {
        validateSlot(slot);

        buttons.remove(slot);
        inv.clear(slot);
    }

    public void clear(int slot) {
        validateSlot(slot);

        inv.clear(slot);
        buttons.put(slot, new EmptyButton());
    }

    public void clear() {
        inv.clear();
        populateButtons();  // Override buttons with empty
    }

    public String getTitle() {
        return title;
    }

    public Inventory getInv() {
        return inv;
    }

    public int getSize() {
        return inv.getSize();
    }

    public Map<Integer, Button> getButtons() {
        return Collections.unmodifiableMap(buttons);
    }

    public List<HumanEntity> getViewers() {
        return inv.getViewers();
    }

    private void validateSlot(int slot) {
        Validate.isTrue(slot >= 0, "Slot cannot be less than 0. Got: " + slot);
        Validate.isTrue(slot < inv.getSize(),
                "Slot cannot be greater than inventory size. Got: " + slot + ":" + inv.getSize());
    }

    public void destroy() {
        clickListener.unregister();
        closeListener.unregister();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < inv.getSize() / 9; i++) {
            for (int j = 0; j < 9; j++) {
                Button button = buttons.get((i * 9) + j);
                buffer.append(" | ")
                        .append(button.getClass().getSimpleName())
                        .append(":")
                        .append(button.getItem().getType().name());
            }
            buffer.append("\n");
        }

        return buffer.toString();
    }

}

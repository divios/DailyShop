package io.github.divios.lib.serialize.wrappers;

import io.github.divios.lib.dLib.newDItem;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class WrappedDButton {

    public static WrappedDButton of(newDItem item) {
        return new WrappedDButton(item);
    }

    private final newDItem item;
    private final LinkedList<Integer> slots = new LinkedList<>();

    public WrappedDButton(newDItem item) {
        this.item = item;
        slots.addFirst(item.getSlot());
    }

    public boolean isMultipleSlots() {
        return !slots.isEmpty();
    }

    public void addMultipleSlot(int slot) {
        slots.add(slot);
    }

    public void addMultipleSlots(Collection<Integer> slots) {
        this.slots.addAll(slots);
    }

    public List<Integer> getMultipleSlots() {
        return Collections.unmodifiableList(slots);
    }

    public newDItem getDItem() {
        return item;
    }
}

package io.github.divios.lib.serialize.wrappers;

import io.github.divios.lib.dLib.dItem;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class WrappedDButton {

    public static WrappedDButton of(dItem item) {
        return new WrappedDButton(item);
    }

    private final dItem item;
    private final LinkedList<Integer> slots = new LinkedList<>();

    public WrappedDButton(dItem item) {
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

    public dItem getDItem() {
        return item;
    }
}

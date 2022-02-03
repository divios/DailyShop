package io.github.divios.lib.dLib.priceModifiers;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class priceModifierManager {

    private final Set<priceModifier> modifiers = ConcurrentHashMap.newKeySet();

    public priceModifierManager() {
    }

    public boolean hasModifiers(Player p) {
        return modifiers.stream().anyMatch(modifier -> p.hasPermission(modifier.getPermission()));
    }

    public double getModifier(Player p, String shop, String itemID, priceModifier.type type) {
        if (isEmpty() || p == null) return 0.0;

        modifierContext context = new modifierContext(p, shop, itemID, type);
        return modifiers.stream()
                .filter(priceModifier -> p.hasPermission(priceModifier.getPermission()))
                .filter(priceModifier -> priceModifier.appliesToContext(context))
                .min(Comparator.comparingInt(priceModifier::getPriority))
                .map(priceModifier -> priceModifier.getValue(context))
                .orElse(0.0);
    }

    public Set<priceModifier> getAsSet() {
        return Collections.unmodifiableSet(modifiers);
    }

    public int size() {
        return modifiers.size();
    }

    public boolean isEmpty() {
        return modifiers.isEmpty();
    }

    public void addModifier(priceModifier modifier) {
        modifiers.add(modifier);
    }

    public void removeModifier(priceModifier modifier) {
        modifiers.remove(modifier);
    }

    public void addAll(Collection<priceModifier> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    public void removeAll(Collection<priceModifier> modifiers) {
        this.modifiers.removeAll(modifiers);
    }

    public void retainAll(Collection<priceModifier> modifiers) {
        this.modifiers.retainAll(modifiers);
    }

    public void clearAll() {
        modifiers.clear();
    }

}

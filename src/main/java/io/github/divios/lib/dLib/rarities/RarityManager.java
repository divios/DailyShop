package io.github.divios.lib.dLib.rarities;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;

import java.util.*;

@SuppressWarnings("unused")
public class RarityManager {

    private final HashMap<String, Rarity> rarities;

    public RarityManager() {
        this.rarities = new HashMap<>();
        rarities.put("unavailable", Rarity.UNAVAILABLE);
    }

    public Map<String, Rarity> getRarities() {
        return Collections.unmodifiableMap(rarities);
    }

    public Optional<Rarity> get(String id) {
        return Optional.ofNullable(rarities.get(id.toLowerCase()));
    }

    public void add(Rarity rarity) {
        rarities.put(rarity.getId(), rarity);
    }

    public void addAll(Collection<Rarity> rarities) {
        rarities.forEach(this::add);
    }

    public boolean remove(Rarity rarity) {
        return remove(rarity.getId());
    }

    public boolean remove(String id) {
        return rarities.remove(id.toLowerCase()) != null;
    }

    public void set(Collection<Rarity> rarities) {
        this.rarities.clear();
        addAll(rarities);
    }

    public Rarity getFirst() {
        return rarities.values().stream()
                .sorted(Comparator.comparingDouble(Rarity::getWeight).reversed())
                .iterator().next();
    }

    public Rarity getNext(Rarity search) {
        Rarity next = null;

        Iterator<Rarity> iterator = rarities.values().stream()
                .sorted(Comparator.comparingDouble(Rarity::getWeight).reversed())
                .iterator();

        while (iterator.hasNext()) {
            if (iterator.next().equals(search)) {
                next = iterator.hasNext() ? iterator.next() : getFirst();
                break;
            }
        }

        return next;
    }

    @Override
    public String toString() {
        return "RarityManager{" +
                "rarities=" + rarities.values() +
                '}';
    }
}

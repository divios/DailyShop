package io.github.divios.lib.dLib.registry;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.registry.util.Pair;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class RecordBook {

    private final ConcurrentHashMap<String, HashSet<RecordBookEntry>> recordBook;

    public RecordBook() {
        this.recordBook = new ConcurrentHashMap<>();
        populate();
    }

    private void populate() {
        DailyShop.get().getDatabaseManager().getLogEntriesAsync()
                .thenAcceptAsync(entries -> {
                    entries.stream()
                            .filter(entry -> {
                                dShop shop = DailyShop.get().getShopsManager().getShop(entry.getShopID()).orElse(null);
                                return shop != null && entry.getTimestamp().compareTo(shop.getTimestamp()) > 0;
                            })
                            .forEach(this::addEntry);
                });
    }

    public static void registerEntry(RecordBookEntry entry) {
        DailyShop.get().getDatabaseManager().addLogEntryAsync(entry);
        DailyShop.get().getRecordBook().addEntry(entry);
    }

    private void addEntry(RecordBookEntry entry) {
        recordBook.compute(entry.getShopID(), (s, recordBookEntries) -> {
            if (recordBookEntries == null) recordBookEntries = new HashSet<>();
            recordBookEntries.add(entry);
            return recordBookEntries;
        });
    }

    public void flushCache(dShop shop) {
        recordBook.remove(shop.getName());
    }

    public int getAmountMatching(dShop shop, Player p, Transactions.Type type, dItem item) {
        Set<RecordBookEntry> entries;
        if ((entries = recordBook.get(shop.getName())) == null) return 0;

        return entries.stream()
                .filter(entry -> entry.getShopID().equals(shop.getName()))
                .filter(entry -> entry.getItemID().equals(item.getID()))
                .filter(entry -> entry.getPlayer().equals(p.getName()))
                .filter(entry -> entry.getType() == type)
                .mapToInt(RecordBookEntry::getQuantity)
                .sum();
    }

    public int getAmountMatching(dShop shop, Player p, Transactions.Type type) {
        Set<RecordBookEntry> entries;
        if ((entries = recordBook.get(shop.getName())) == null) return 0;

        return entries.stream()
                .filter(entry -> entry.getShopID().equals(shop.getName()))
                .filter(entry -> entry.getPlayer().equals(p.getName()))
                .filter(entry -> entry.getType() == type)
                .mapToInt(RecordBookEntry::getQuantity)
                .sum();
    }

    public Pair<Integer, Integer> getTupleAmount(dShop shop, Player p, Transactions.Type type, dItem item) {
        Set<RecordBookEntry> entries;
        if ((entries = recordBook.get(shop.getName())) == null) return Pair.of(0, 0);

        int shopAmount = 0;
        int itemAmount = 0;
        for (RecordBookEntry entry : entries) {
            if (entry.getShopID().equals(shop.getName())
                    && entry.getPlayer().equals(p.getName())
                    && entry.getType() == type) {
                shopAmount += entry.getQuantity();
                if (entry.getItemID().equals(item.getID()))
                    itemAmount += entry.getQuantity();
            }
        }

        return Pair.of(shopAmount, itemAmount);
    }

}

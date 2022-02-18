package io.github.divios.lib.dLib.shop;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.registry.util.Pair;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class LogCache {

    private final HashMap<UUID, LogEntry> map = new HashMap<>();

    public void register(UUID uuid, String id, int amount, Transactions.Type type) {
        map.compute(uuid, (uuid1, entry) -> {
            if (entry == null)
                entry = new LogEntry();
            entry.put(id, amount, type);

            return entry;
        });
    }

    public int getTotalAmount(Player p, Transactions.Type type) {
        LogEntry entry;
        return (entry = map.get(p.getUniqueId())) == null
                ? 0
                : entry.getTotalAmount(type);
    }

    public int getAmountForItem(Player p, String id, Transactions.Type type) {
        LogEntry entry;
        return (entry = map.get(p.getUniqueId())) == null
                ? 0
                : entry.getAmount(id, type);
    }

    public Pair<Integer, Integer> getAmountTuple(UUID uuid, dItem item, Transactions.Type type) {
        int totalAmount;
        int itemAmount;

        LogEntry entry;
        if ((entry = map.get(uuid)) == null)
            return Pair.of(0, 0);

        totalAmount = entry.getTotalAmount(type);
        itemAmount = entry.getAmount(item.getID(), type);

        return Pair.of(totalAmount, itemAmount);
    }

    public void clear() {
        map.clear();
    }


    private static final class LogEntry {

        private final TotalAmountEntry amounts = new TotalAmountEntry();
        private final ItemsMap itemsMap = new ItemsMap();

        public void put(String id, int amount, Transactions.Type type) {
            if (type == Transactions.Type.BUY)
                amounts.buyTotalAmount += amount;
            else
                amounts.sellTotalAmount += amount;

            itemsMap.put(id, amount, type);
        }

        public int getAmount(String id, Transactions.Type type) {
            return itemsMap.getAmount(id, type);
        }

        public int getTotalAmount(Transactions.Type type) {
            if (type == Transactions.Type.BUY)
                return amounts.buyTotalAmount;
            else
                return amounts.sellTotalAmount;
        }

    }

    private static final class TotalAmountEntry {
        private int buyTotalAmount = 0;
        private int sellTotalAmount = 0;
    }

    private static final class ItemsMap {

        private final HashMap<String, ItemsMapEntry> itemsMapLimit = new HashMap<>();

        public void put(String id, int amount, Transactions.Type type) {
            itemsMapLimit.compute(id, (s, storageMapEntry) -> {
                if (storageMapEntry == null)
                    storageMapEntry = new ItemsMapEntry();
                storageMapEntry.put(amount, type);

                return storageMapEntry;
            });
        }

        public int getAmount(String id, Transactions.Type type) {
            ItemsMapEntry entry;
            return (entry = itemsMapLimit.get(id)) == null
                    ? 0
                    : entry.get(type);
        }

    }

    private static final class ItemsMapEntry {

        private int buyLimit;
        private int sellLimit;

        public int getBuyLimit() {
            return buyLimit;
        }

        public int getSellLimit() {
            return sellLimit;
        }

        public int get(Transactions.Type type) {
            if (type == Transactions.Type.BUY)
                return getBuyLimit();
            else if (type == Transactions.Type.SELL)
                return getSellLimit();

            return 0;
        }

        public void put(int amount, Transactions.Type type) {
            if (type == Transactions.Type.BUY)
                incrementBuy(amount);
            else if (type == Transactions.Type.SELL)
                incrementSell(amount);
        }

        public void incrementBuy(int amount) {
            buyLimit += amount;
        }

        public void incrementSell(int amount) {
            sellLimit += amount;
        }

    }

}

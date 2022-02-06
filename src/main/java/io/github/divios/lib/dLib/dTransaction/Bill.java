package io.github.divios.lib.dLib.dTransaction;

import com.google.common.base.Preconditions;
import io.github.divios.dailyShop.economies.Economy;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("unused")
public class Bill {

    public static BillBuilder start(Player p,
                                    Transactions.Type type,
                                    Economy economy
    ) {
        return new BillBuilder(p, type, economy);
    }

    private final Player player;
    private final Transactions.Type type;
    private final HashMap<String, Map.Entry<Double, Integer>> billTable;
    private final Economy economy;
    private double totalPrice = 0;

    public Bill(Player player,
                Transactions.Type type,
                Map<String, Map.Entry<Double, Integer>> items,
                Economy economy) {
        this.player = Objects.requireNonNull(player);
        this.type = Objects.requireNonNull(type);
        this.billTable = new HashMap<>();
        this.economy = Objects.requireNonNull(economy);

        items.forEach((s, entry) -> {
            billTable.put(s, entry);
            totalPrice += entry.getKey();
        });
    }

    public Player getPlayer() {
        return player;
    }

    public Transactions.Type getType() {
        return type;
    }

    public Economy getEconomy() {
        return economy;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Map<String, Map.Entry<Double, Integer>> getBillTable() {
        return Collections.unmodifiableMap(billTable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Double.compare(bill.totalPrice, totalPrice) == 0
                && Objects.equals(player.getUniqueId(), bill.player.getUniqueId())
                && Objects.equals(billTable, bill.billTable)
                && Objects.equals(economy, bill.economy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player.getUniqueId(),
                billTable,
                economy,
                totalPrice);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "player=" + player +
                ", billTable=" + billTable +
                ", economy=" + economy +
                ", totalPrice=" + totalPrice +
                '}';
    }

    public static final class BillBuilder {
        private final Player player;
        private final Transactions.Type type;
        private final Economy economy;
        private final HashMap<String, Map.Entry<Double, Integer>> billTable;

        BillBuilder(Player player, Transactions.Type type, Economy economy) {
            this.player = player;
            this.type = type;
            this.economy = economy;
            this.billTable = new HashMap<>();
        }

        public BillBuilder withItem(String id, double price, int amount) {
            billTable.put(id, new AbstractMap.SimpleEntry<>(price, amount));
            return this;
        }

        public Bill printBill() {
            Preconditions.checkNotNull(player);
            Preconditions.checkNotNull(type);
            Preconditions.checkNotNull(economy);

            return new Bill(player, type, billTable, economy);
        }
    }

}

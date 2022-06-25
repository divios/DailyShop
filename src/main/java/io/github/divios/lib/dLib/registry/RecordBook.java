package io.github.divios.lib.dLib.registry;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@SuppressWarnings("unused")
public class RecordBook {

    public static void initiate() {
        DailyShop.get().getDatabaseManager().getLogEntriesAsync(Integer.MAX_VALUE)
                .thenAcceptAsync(entries -> {

                    for (RecordBookEntry entry : entries) {
                        dShop shop = DailyShop.get().getShopsManager().getShop(entry.getShopID()).orElse(null);
                        if (shop == null) continue; // TODO

                        OfflinePlayer p = Bukkit.getOfflinePlayer(entry.getPlayer());
                        shop.getShopCache().register(
                                p.getUniqueId(),
                                entry.getItemID(),
                                entry.getQuantity(),
                                entry.getType()
                        );
                    }

                }).complete(null);
    }

    public static void registerEntry(RecordBookEntry entry) {
        DailyShop.get().getDatabaseManager().addLogEntryAsync(entry);
    }

}

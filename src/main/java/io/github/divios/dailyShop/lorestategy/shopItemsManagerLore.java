package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class shopItemsManagerLore {

    private shopItemsManagerLore() {
        throw new RuntimeException("This class cannot be instantiated");
    }

    public static ItemStack applyLore(@NotNull dItem item) {
        ItemStack toReturn = item.getItemWithId();

        toReturn = new ItemBuilder(toReturn)                            // Prices lore
                .addLore("")
                .addLore(Lang.DAILY_ITEMS_BUY_PRICE.getAsString(
                                Template.of("buyPrice", item.getDBuyPrice() == null
                                        ? "&c" + XSymbols.TIMES_3.parseSymbol()
                                        : item.getDBuyPrice().getGenerator().toString()
                                )
                        )
                )
                .addLore(Lang.DAILY_ITEMS_SELL_PRICE.getAsString(
                                Template.of("sellPrice", item.getDSellPrice() == null
                                        ? "&c" + XSymbols.TIMES_3.parseSymbol()
                                        : item.getDSellPrice().getGenerator().toString()
                                )
                        )
                )
                .addLore("");

        if (item.hasStock())                                   // Stock lore
            toReturn = ItemUtils
                    .addLore(toReturn,
                            Lang.DAILY_ITEMS_STOCK.getAsString() +
                                    item.getDStock().getDefault() + " (" + item.getDStock().getName() + ")"
                    );

        return new ItemBuilder(toReturn)                                // Currency, rarity lore
                .addLore(Lang.DAILY_ITEMS_CURRENCY.getAsString(
                                Template.of("currency", item.getEcon().getName())
                        )
                )
                .addLore(Lang.DAILY_ITEMS_RARITY.getAsString(
                                Template.of("rarity", item.getRarity().getName())
                        )
                )
                .addLore("")
                .addLore(Lang.DAILY_ITEMS_MANAGER_LORE.getAsListString());      // Clicks info lore

    }

}

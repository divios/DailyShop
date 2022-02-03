package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.jcommands.util.Primitives;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class shopItemsLore {

    private shopItemsLore() {
        throw new RuntimeException("This class cannot be instantiated");
    }

    public static ItemStack applyLore(@NotNull dItem item, @Nullable Player p, @Nullable dShop shop) {
        ItemStack toReturn = item.getItemWithId();

        dStock stock;
        if (p != null && (stock = item.getDStock()) != null)
            toReturn = ItemBuilder.of(toReturn)
                    .addLore("")
                    .addLore(Lang.DAILY_ITEMS_STOCK.getAsString(p) + getStockForPlayer(stock, p));

        return new ItemBuilder(toReturn)
                .addLore(Lang.SHOPS_ITEMS_LORE.getAsListString(p,
                                Template.of("buyPrice", getItemBuyPrice(item, p, shop)),
                                Template.of("sellPrice", getItemSellPrice(item, p, shop)),
                                Template.of("currency", item.getEcon().getName()),
                                Template.of("rarity", item.getRarity().toString())
                        )
                );
    }

    private static String getStockForPlayer(dStock stock, Player p) {
        if (stock.get(p) > 0)
            return Primitives.getAsString(stock.get(p));
        else
            return getRedCross();

    }

    private static String getItemBuyPrice(dItem item, Player p, dShop shop) {
        double price;
        if ((price = item.getPlayerBuyPrice(p, shop)) >= 0)
            return PrettyPrice.pretty(price);
        else
            return getRedCross();
    }

    private static String getItemSellPrice(dItem item, Player p, dShop shop) {
        double price;
        if ((price = item.getPlayerSellPrice(p, shop)) > 0)
            return PrettyPrice.pretty(price);
        else
            return getRedCross();
    }

    private static String getRedCross() {
        return FormatUtils.color("&c" + XSymbols.TIMES_3.parseSymbol());
    }

}

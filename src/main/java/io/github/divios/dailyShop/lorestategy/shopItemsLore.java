package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class shopItemsLore implements loreStrategy {
    private static final DailyShop plugin = DailyShop.get();

    private ItemStack itemToApplyLore;
    private Player player;
    private dShop shop;

    public shopItemsLore() {}

    @Override
    public ItemStack applyLore(ItemStack item, Object... data) {
        this.itemToApplyLore = item;
        player = (data.length) >= 1 ? (Player) data[0] : null;
        shop = (data.length >= 2) ? (dShop) data[1] : null;
        return addLoreToItem();
    }

    private ItemStack addLoreToItem() {
        if (itemHasStock())
            itemToApplyLore = applyStockLore();
        return applyDefaultLore();
    }

    private boolean itemHasStock() {
        return dItem.of(itemToApplyLore).hasStock()
                && player != null
                && !plugin.configM.getLangYml().DAILY_ITEMS_STOCK.isEmpty();
    }

    private ItemStack applyStockLore() {
        return ItemBuilder.of(itemToApplyLore)
                .addLore("")
                .addLore(plugin.configM.getLangYml().DAILY_ITEMS_STOCK + getStockForPlayer());
    }

    private ItemStack applyDefaultLore() {
        return ItemBuilder.of(itemToApplyLore)
                .addLore(getDefaultLore());
    }

    private String getStockForPlayer() {
        if (playerHasStock())
            return String.valueOf(dItem.of(itemToApplyLore).getStock().get(player));
        else
            return getRedCross();

    }

    private List<String> getDefaultLore() {
        return Msg.msgList(plugin.configM.getLangYml().SHOPS_ITEMS_LORE)
                .add("\\{buyPrice}", getItemBuyPrice())
                .add("\\{sellPrice}", getItemSellPrice())
                .add("\\{currency}", getItemEconomyName())
                .add("\\{rarity}", getItemRarity())
                .build();
    }

    private boolean playerHasStock() {
        return dItem.of(itemToApplyLore).getStock().get(player) != -1;
    }

    private String getRedCross() {
        return FormatUtils.color("&c" + XSymbols.TIMES_3.parseSymbol());
    }

    private String getItemBuyPrice() {
        if (itemHasValidBuyPrice())
            return getItemBuyPriceDoubleFormatted();
        else
            return getRedCross();
    }

    private String getItemSellPrice() {
        if (itemHasValidSellPrice())
            return getItemSellPriceDoubleFormatted();
        else
            return getRedCross();
    }

    private String getItemEconomyName() {
        return dItem.of(itemToApplyLore).getEconomy().getName();
    }

    private String getItemRarity() {
        return dItem.of(itemToApplyLore).getRarity().toString();
    }

    private boolean itemHasValidBuyPrice() {
        return dItem.of(itemToApplyLore).getBuyPrice().isPresent()
                && dItem.of(itemToApplyLore).getBuyPrice().get().getPrice() != -1;
    }

    private boolean itemHasValidSellPrice() {
        return dItem.of(itemToApplyLore).getSellPrice().isPresent() &&
                dItem.of(itemToApplyLore).getSellPrice().get().getPrice() != -1;
    }

    private String getItemBuyPriceDoubleFormatted() {
        return PriceWrapper.format(getItemBuyPriceDouble() * dItem.of(itemToApplyLore).getSetItems().orElse(1));
    }

    private String getItemSellPriceDoubleFormatted() {
        return PriceWrapper.format(getItemSellPriceDouble() * dItem.of(itemToApplyLore).getSetItems().orElse(1));
    }

    private double getItemBuyPriceDouble() {
        return dItem.of(itemToApplyLore).getBuyPrice().orElse(new dPrice(-1)).getPriceForPlayer(player, shop, dItem.getId(itemToApplyLore), priceModifier.type.BUY);
    }

    private double getItemSellPriceDouble() {
        return dItem.of(itemToApplyLore).getSellPrice().orElse(new dPrice(-1)).getPriceForPlayer(player, shop, dItem.getId(itemToApplyLore), priceModifier.type.SELL);
    }


}

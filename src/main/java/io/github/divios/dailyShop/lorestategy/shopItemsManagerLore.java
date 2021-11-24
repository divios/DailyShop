package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.inventory.ItemStack;

public class shopItemsManagerLore implements loreStrategy {


    private static final DailyShop plugin = DailyShop.getInstance();

    public shopItemsManagerLore() {
    }

    public ItemStack applyLore(ItemStack item, Object... data) {
        item = applyPricesLore(item);
        if (itemHasStockEnabled(item))
            item = applyStockLore(item);
        item = applyCurrencyNameLore(item);
        item = applyRarityLore(item);
        item = applyClicksInfoLore(item);
        return item;
    }

    private ItemStack applyPricesLore(ItemStack item) {
        return ItemBuilder.of(item)
                .addLore("")
                .addLore(getBuyPriceFormatted(item))
                .addLore(getSellPriceFormatted(item))
                .addLore("");
    }

    private boolean itemHasStockEnabled(ItemStack item) {
        return dItem.of(item).hasStock();
    }

    private ItemStack applyStockLore(ItemStack item) {
        return ItemBuilder.of(item).
                addLore(plugin.configM.getLangYml().DAILY_ITEMS_STOCK +
                        dItem.of(item).getStock().getDefault() + " (" + dItem.of(item).getStock().getName() + ")");
    }

    private ItemStack applyCurrencyNameLore(ItemStack item) {
        return ItemBuilder.of(item)
                .addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_CURRENCY)
                .add("\\{currency}", "" + dItem.of(item).getEconomy().getName()).build());
    }

    private ItemStack applyRarityLore(ItemStack item) {
        return ItemBuilder.of(item)
                .addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_RARITY)
                .add("\\{rarity}", dItem.of(item).getRarity().toString()).build());
    }

    private ItemStack applyClicksInfoLore(ItemStack item) {
        return ItemBuilder.of(item)
                .addLore("")
                .addLore(plugin.configM.getLangYml().DAILY_ITEMS_MANAGER_LORE);
    }

    private String getBuyPriceFormatted(ItemStack item) {
        return Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_BUY_PRICE)
                .add("\\{buyPrice}", getBuyVisualPrice(item))
                .build();
    }

    private String getSellPriceFormatted(ItemStack item) {
        return Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_SELL_PRICE)
                .add("\\{sellPrice}", getSellVisualPrice(item))
                .build();
    }

    private String getBuyVisualPrice(ItemStack item) {
        return dItem.of(item).getBuyPrice().orElse(new dPrice(-1)).getVisualPrice();
    }

    private String getSellVisualPrice(ItemStack item) {
        return dItem.of(item).getSellPrice().orElse(new dPrice(-1)).getVisualPrice();
    }


}

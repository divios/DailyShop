package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import org.bukkit.inventory.ItemStack;

public class shopItemsManagerLore implements loreStrategy {

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
                addLore(Lang.DAILY_ITEMS_STOCK.getAsString() +
                        dItem.of(item).getStock().getDefault() + " (" + dItem.of(item).getStock().getName() + ")");
    }

    private ItemStack applyCurrencyNameLore(ItemStack item) {
        return ItemBuilder.of(item)
                .addLore(Lang.DAILY_ITEMS_CURRENCY.getAsString(
                        Template.of("currency", dItem.of(item).getEconomy().getName())
                ));
    }

    private ItemStack applyRarityLore(ItemStack item) {
        return ItemBuilder.of(item)
                .addLore(Lang.DAILY_ITEMS_RARITY.getAsString(
                        Template.of("rarity", dItem.of(item).getRarity().toString())
                ));
    }

    private ItemStack applyClicksInfoLore(ItemStack item) {
        return ItemBuilder.of(item)
                .addLore("")
                .addLore(Lang.DAILY_ITEMS_MANAGER_LORE.getAsListString());
    }

    private String getBuyPriceFormatted(ItemStack item) {
        return Lang.DAILY_ITEMS_BUY_PRICE.getAsString(
                Template.of("buyPrice", getBuyVisualPrice(item))
        );
    }

    private String getSellPriceFormatted(ItemStack item) {
        return Lang.DAILY_ITEMS_SELL_PRICE.getAsString(
                Template.of("sellPrice", getSellVisualPrice(item))
        );
    }

    private String getBuyVisualPrice(ItemStack item) {
        return dItem.of(item).getBuyPrice().orElse(new dPrice(-1)).getVisualPrice();
    }

    private String getSellVisualPrice(ItemStack item) {
        return dItem.of(item).getSellPrice().orElse(new dPrice(-1)).getVisualPrice();
    }


}

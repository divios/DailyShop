package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.PriceFormatter;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class shopItemsLore implements loreStrategy {
    private static final DailyShop plugin = DailyShop.getInstance();

    public shopItemsLore() {}

    @Override
    public void setLore(ItemStack item) {
        ItemUtils.translateAllItemData(applyLore(item), item);
    }

    @Override
    public ItemStack applyLore(ItemStack item, Object... data) {

        Player p = (Player) data[0];
        dItem aux = dItem.of(item);

        ItemBuilder newItem = ItemBuilder.of(item)
                .addLore("")
                .addLore(Msg.singletonMsg(
                        plugin.configM.getLangYml().DAILY_ITEMS_BUY_PRICE)
                        .add("\\{buyPrice}",
                                (aux.getBuyPrice().isPresent()
                                        && aux.getBuyPrice().get().getPrice() != -1) ?
                                        PriceFormatter.format(aux.getBuyPrice().orElse(new dPrice(-1)).getPrice())
                                        : FormatUtils.color("&c&l" + XSymbols.TIMES_3.parseSymbol())).build())

                .addLore(Msg.singletonMsg(
                        plugin.configM.getLangYml().DAILY_ITEMS_SELL_PRICE)
                        .add("\\{sellPrice}", (aux.getSellPrice().isPresent()
                                && aux.getSellPrice().get().getPrice() != -1) ?
                                PriceFormatter.format(aux.getSellPrice().orElse(new dPrice(-1)).getPrice())
                                : FormatUtils.color("&c&l" + XSymbols.TIMES_3.parseSymbol())).build())

                .addLore("");

        if (aux.hasStock() && p != null) {
            newItem = newItem.addLore(plugin.configM.getLangYml().DAILY_ITEMS_STOCK +
                    (aux.getStock().get(p) == -1 ?
                            FormatUtils.color("&c" + XSymbols.TIMES_3.parseSymbol()):
                            aux.getStock().get(p)));
        }

        newItem = newItem.addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_CURRENCY)
                .add("\\{currency}", aux.getEconomy().getName()).build());

        newItem = newItem.addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_RARITY)
                .add("\\{rarity}", aux.getRarity().toString()).build());


        return newItem.addLore(plugin.configM.getLangYml().SHOPS_ITEMS_LORE);
    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {

    }
}

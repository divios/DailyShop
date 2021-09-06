package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class shopItemsLore implements loreStrategy {
    private static final DailyShop plugin = DailyShop.getInstance();

    public shopItemsLore() {
    }

    @Override
    public void setLore(ItemStack item) {
        ItemUtils.translateAllItemData(applyLore(item), item);
    }

    @Override
    public ItemStack applyLore(ItemStack item, Object... data) {

        Player p = (Player) data[0];
        dItem aux = dItem.of(item);

        ItemBuilder newItem = ItemBuilder.of(item);

        if (aux.hasStock() && p != null && !plugin.configM.getLangYml().DAILY_ITEMS_STOCK.isEmpty()) {
            newItem = newItem.addLore("").addLore(plugin.configM.getLangYml().DAILY_ITEMS_STOCK +
                    (aux.getStock().get(p) == -1 ?
                            FormatUtils.color("&c" + XSymbols.TIMES_3.parseSymbol()) :
                            aux.getStock().get(p)));
        }


        return newItem.addLore(
                Msg.msgList(plugin.configM.getLangYml().SHOPS_ITEMS_LORE)

                        .add("\\{buyPrice}",
                                (aux.getBuyPrice().isPresent()
                                        && aux.getBuyPrice().get().getPrice() != -1) ?
                                        PriceWrapper.format(aux.getBuyPrice().orElse(new dPrice(-1)).getPrice())
                                        : FormatUtils.color("&c&l" + XSymbols.TIMES_3.parseSymbol()))

                        .add("\\{sellPrice}", (aux.getSellPrice().isPresent()
                                && aux.getSellPrice().get().getPrice() != -1) ?
                                PriceWrapper.format(aux.getSellPrice().orElse(new dPrice(-1)).getPrice())
                                : FormatUtils.color("&c&l" + XSymbols.TIMES_3.parseSymbol()))

                        .add("\\{currency}", aux.getEconomy().getName())

                        .add("\\{rarity}", aux.getRarity().toString())

                        .build());

    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {

    }
}

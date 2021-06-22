package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dPrice;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.inventory.ItemStack;

import java.text.Format;

public class shopItemsLore implements loreStrategy {
    private final dShop.dShopT type;

    public shopItemsLore(dShop.dShopT type) {
        this.type = type;
    }

    @Override
    public void setLore(ItemStack item) {

        dItem aux = dItem.of(item);

        ItemBuilder newItem = new ItemBuilder(item)
                        .addLore(Msg.singletonMsg(
                                conf_msg.BUY_GUI_ITEMS_LORE_BUY_PRICE)
                                .add("\\{buyPrice}",
                                        (aux.getBuyPrice().isPresent()
                                        && aux.getBuyPrice().get().getPrice() != -1) ? "" +
                                        aux.getBuyPrice().orElse(new dPrice(-1)).getVisualPrice()
                                        : FormatUtils.color("&c&l" + XSymbols.TIMES_3.parseSymbol())).build())

                        .addLore(Msg.singletonMsg(
                                        conf_msg.BUY_GUI_ITEMS_LORE_SELL_PRICE)
                                .add("\\{sellPrice}", (aux.getSellPrice().isPresent()
                                        && aux.getSellPrice().get().getPrice() != -1) ? "" +
                                        aux.getSellPrice().orElse(new dPrice(-1)).getVisualPrice()
                                        : FormatUtils.color("&c&l" + XSymbols.TIMES_3.parseSymbol())).build())

                        .addLore("");

                        if (aux.getStock().isPresent()) {
                            newItem = newItem.addLore(FormatUtils.color("&6Stock: &7" +
                                    (aux.getStock().get() == -1 ?
                                    FormatUtils.color("&c" + XSymbols.TIMES_3.parseSymbol()):
                                    aux.getStock().get())));
                        }

                        newItem = newItem.addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY)
                                .add("\\{currency}", aux.getEconomy().getName()).build());

                        if (DRShop.getInstance().getConfig().getBoolean("enable-rarity", true))
                            newItem = newItem.addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_RARITY)
                                .add("\\{rarity}", aux.getRarity().toString()).build());

        ItemUtils.translateAllItemData(newItem, item);
    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {

    }
}

package io.github.divios.dailyrandomshop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dPrice;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.inventory.ItemStack;

public class shopItemsLore implements loreStrategy {
    private final dShop.dShopT type;

    public shopItemsLore(dShop.dShopT type) {
        this.type = type;
    }

    @Override
    public void setLore(ItemStack item) {
        ItemUtils.translateAllItemData(new ItemBuilder(item)
                        .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_BUY_PRICE)
                                .add("\\{buyPrice}", "" +
                                        dItem.of(item).getBuyPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                        .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_SELL_PRICE)
                                .add("\\{sellPrice}", "" +
                                        dItem.of(item).getSellPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                        .addLore("")

                        .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY)
                                .add("\\{currency}", new dItem(item).getEconomy().getName()).build())

                        .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_RARITY)
                                .add("\\{rarity}", new dItem(item).getRarity().toString()).build())
                , item);
    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {

    }
}

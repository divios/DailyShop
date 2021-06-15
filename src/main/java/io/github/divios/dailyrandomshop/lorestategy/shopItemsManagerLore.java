package io.github.divios.dailyrandomshop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.inventory.ItemStack;

public class shopItemsManagerLore implements loreStrategy {

    private final dShop.dShopT type;

    public shopItemsManagerLore(dShop.dShopT type) {
        this.type = type;
    }

    @Override
    public void setLore(ItemStack item) {


        ItemStack aux = new ItemBuilder(item)
                .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_PRICE)
                        .add("\\{price}", "" + (type.equals(dShop.dShopT.buy) ?
                                dItem.of(item).getBuyPrice().get().getVisualPrice() :
                                dItem.of(item).getSellPrice().get().getVisualPrice())).build())

                .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY)
                        .add("\\{currency}", "" + dItem.of(item).getEconomy().getName()).build())

                .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_RARITY)
                        .add("\\{rarity}", "" + dItem.of(item).getRarity().toString()).build())

                .addLore("")
                .addLore(conf_msg.DAILY_ITEMS_MENU_ITEMS_LORE);

        ItemUtils.translateAllItemData(aux, item);

    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {

    }
}

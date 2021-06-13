package io.github.divios.dailyrandomshop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import org.bukkit.inventory.ItemStack;
import io.github.divios.dailyrandomshop.conf_msg;

public class shopItemsLore implements loreStrategy{
    private final dShop.dShopT type;

    public shopItemsLore(dShop.dShopT type) { this.type = type; }

    @Override
    public void setLore(ItemStack item) {
        ItemUtils.translateAllItemData(new ItemBuilder(item)
            .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_PRICE)
                    .add("\\{price}", "" + (type.equals(dShop.dShopT.buy) ?
                            new dItem(item).getBuyPrice(): new dItem(item).getSellPrice())).build())
            .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY)
                    .add("\\{currency}", new dItem(item).getEconomy().getName()).build())   //TODO
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

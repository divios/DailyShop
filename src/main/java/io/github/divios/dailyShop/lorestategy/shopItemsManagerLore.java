package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.inventory.ItemStack;

public class shopItemsManagerLore implements loreStrategy {

    private final dShop.dShopT type;

    public shopItemsManagerLore(dShop.dShopT type) {
        this.type = type;
    }

    @Override
    public void setLore(ItemStack item) {


        ItemBuilder aux = new ItemBuilder(item)
                .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_BUY_PRICE)
                        .add("\\{buyPrice}", "" +
                                dItem.of(item).getBuyPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_SELL_PRICE)
                        .add("\\{sellPrice}", "" +
                                dItem.of(item).getSellPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                .addLore("");

                if (dItem.of(item).getStock().isPresent() && dItem.of(item).getStock().get() != -1) {
                    aux = aux.addLore(FormatUtils.color("&6Stock: &7") + dItem.of(item).getStock().get());
                }

                aux = aux.addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY)
                        .add("\\{currency}", "" + dItem.of(item).getEconomy().getName()).build());

                if (DRShop.getInstance().getConfig().getBoolean("enable-rarity", true))
                    aux = aux.addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_RARITY)
                        .add("\\{rarity}", dItem.of(item).getRarity().toString()).build());

                aux = aux.addLore("")
                .addLore(conf_msg.DAILY_ITEMS_MENU_ITEMS_LORE);

        ItemUtils.translateAllItemData(aux, item);

    }

    public ItemStack applyLore(ItemStack item) {

        ItemBuilder aux = new ItemBuilder(item)
                .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_BUY_PRICE)
                        .add("\\{buyPrice}", "" +
                                dItem.of(item).getBuyPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                .addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_SELL_PRICE)
                        .add("\\{sellPrice}", "" +
                                dItem.of(item).getSellPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                .addLore("");

        if (dItem.of(item).getStock().isPresent() && dItem.of(item).getStock().get() != -1) {
            aux = aux.addLore(FormatUtils.color("&6Stock: &7") + dItem.of(item).getStock().get());
        }

        aux = aux.addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_CURRENCY)
                .add("\\{currency}", "" + dItem.of(item).getEconomy().getName()).build());

        if (DRShop.getInstance().getConfig().getBoolean("enable-rarity", true))
            aux = aux.addLore(Msg.singletonMsg(conf_msg.BUY_GUI_ITEMS_LORE_RARITY)
                    .add("\\{rarity}", dItem.of(item).getRarity().toString()).build());

        aux = aux.addLore("")
                .addLore(conf_msg.DAILY_ITEMS_MENU_ITEMS_LORE);

        return aux;
    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {

    }
}

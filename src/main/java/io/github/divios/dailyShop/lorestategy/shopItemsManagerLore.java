package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dPrice;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.inventory.ItemStack;

public class shopItemsManagerLore implements loreStrategy {

    private final dShop.dShopT type;
    private static final DailyShop plugin = DailyShop.getInstance();

    public shopItemsManagerLore(dShop.dShopT type) {
        this.type = type;
    }

    @Override
    public void setLore(ItemStack item) {
        ItemUtils.translateAllItemData(applyLore(item), item);
    }

    public ItemStack applyLore(ItemStack item, Object... data) {

        dItem ditem = dItem.of(item);
        ItemBuilder aux = ItemBuilder.of(item)
                .addLore("")
                .addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_BUY_PRICE)
                        .add("\\{buyPrice}", "" +
                                ditem.getBuyPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                .addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_SELL_PRICE)
                        .add("\\{sellPrice}", "" +
                                ditem.getSellPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                .addLore("");

        if (ditem.hasStock()) {
            aux = aux.addLore(plugin.configM.getLangYml().DAILY_ITEMS_STOCK + ditem.getStock().getDefault() + " (" + ditem.getStock().getName() + ")");
        }

        aux = aux.addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_CURRENCY)
                .add("\\{currency}", "" + ditem.getEconomy().getName()).build());

        if (DailyShop.getInstance().getConfig().getBoolean("enable-rarity", true))
            aux = aux.addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_RARITY)
                    .add("\\{rarity}", ditem.getRarity().toString()).build());

        aux = aux.addLore("").addLore(plugin.configM.getLangYml().DAILY_ITEMS_MANAGER_LORE);

        return aux;
    }

    @Override
    public void removeLore(ItemStack item) {

    }

    @Override
    public void update(ItemStack item) {

    }
}

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

    public ItemStack applyLore(ItemStack item) {

        ItemBuilder aux = ItemBuilder.of(item)
                .addLore("")
                .addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_BUY_PRICE)
                        .add("\\{buyPrice}", "" +
                                dItem.of(item).getBuyPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                .addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_SELL_PRICE)
                        .add("\\{sellPrice}", "" +
                                dItem.of(item).getSellPrice().orElse(new dPrice(-1)).getVisualPrice()).build())

                .addLore("");

        if (dItem.of(item).getStock().isPresent() && dItem.of(item).getStock().get() != -1) {
            aux = aux.addLore(plugin.configM.getLangYml().DAILY_ITEMS_STOCK + dItem.of(item).getStock().get());
        }

        aux = aux.addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_CURRENCY)
                .add("\\{currency}", "" + dItem.of(item).getEconomy().getName()).build());

        if (DailyShop.getInstance().getConfig().getBoolean("enable-rarity", true))
            aux = aux.addLore(Msg.singletonMsg(plugin.configM.getLangYml().DAILY_ITEMS_RARITY)
                    .add("\\{rarity}", dItem.of(item).getRarity().toString()).build());

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

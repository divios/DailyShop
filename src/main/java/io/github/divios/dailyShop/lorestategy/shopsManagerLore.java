package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class shopsManagerLore implements loreStrategy {

    @Override
    public void setLore(ItemStack item) {
        ItemUtils.translateAllItemData(applyLore(item), item);
    }

    @Override
    public ItemStack applyLore(ItemStack item, Object... data) {

        String name = FormatUtils.stripColor(item.getItemMeta().getDisplayName().substring(4));
        dShop shop = shopsManager.getInstance().getShop(name).get();

        List<String> placeholder = Msg.msgList(
                DailyShop.getInstance().configM.getLangYml().SHOPS_MANAGER_LORE)
                .add("\\{timer}", "" + shop.getTimer())
                .add("\\{amount}", String.valueOf(shop.getItems().size()))
                .add("\\{c_timer}", utils.getDiffActualTimer(shop))
                .build();

        return ItemBuilder.of(item).addLore(placeholder);
    }

    @Override
    public void removeLore(ItemStack item) {  // Not needed

    }

    @Override
    public void update(ItemStack item) {    // Not needed

    }
}

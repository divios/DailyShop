package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings({"ConstantConditions"})
public class shopsManagerLore implements loreStrategy {

    @Override
    public ItemStack applyLore(ItemStack item, Object... data) {

        String name = FormatUtils.stripColor(item.getItemMeta().getDisplayName().substring(4));
        dShop shop = shopsManager.getInstance().getShop(name).orElse(null);

        return addLore(item, shop);
    }

    private ItemStack addLore(ItemStack item, dShop shop) {
        return ItemBuilder.of(item).addLore(getLore(shop));
    }

    private List<String> getLore(dShop shop) {
        return Msg.msgList(
                DailyShop.get().configM.getLangYml().SHOPS_MANAGER_LORE)
                .add("\\{timer}", String.valueOf(shop.getTimer()))
                .add("\\{amount}", getShopAmountOfItems(shop))
                .add("\\{c_timer}", getShopTimerFormatted(shop))
                .build();
    }

    private String getShopAmountOfItems(dShop shop) {
        return String.valueOf(shop.getItems().size());
    }

    private String getShopTimerFormatted(dShop shop) {
        if (shopTimerIsDisabled(shop))
            return getRedCross();
        else
            return Utils.getDiffActualTimer(shop);
    }

    private boolean shopTimerIsDisabled(dShop shop) {
        return shop.getTimer() == -1;
    }

    private String getRedCross() {
        return XSymbols.TIMES_3.parseSymbol();
    }

}

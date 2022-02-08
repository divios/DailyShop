package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"ConstantConditions"})
public class shopsManagerLore {

    private shopsManagerLore() {
        throw new RuntimeException("This class cannot be instantiated");
    }

    public static ItemStack applyLore(ItemStack item) {

        String name = FormatUtils.stripColor(item.getItemMeta().getDisplayName().substring(4));
        dShop shop = DailyShop.get().getShopsManager().getShop(name).orElse(null);

        return new ItemBuilder(item)
                .addLore(Lang.SHOPS_MANAGER_LORE.getAsListString(
                                Template.of("timer", shop.getTimer()),
                                Template.of("amount", shop.size()),
                                Template.of("c_timer", getShopTimerFormatted(shop))
                        )
                );
    }

    private static String getShopTimerFormatted(dShop shop) {
        if (shop.getTimer() == -1)
            return XSymbols.TIMES_3.parseSymbol();
        else
            return Utils.getDiffActualTimer(shop);
    }

}

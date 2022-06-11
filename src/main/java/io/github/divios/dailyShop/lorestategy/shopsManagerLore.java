package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"ConstantConditions"})
public class shopsManagerLore {

    private static final String SHOP_META = "dShopD";

    private shopsManagerLore() {
        throw new RuntimeException("This class cannot be instantiated");
    }

    public static ItemStack applyLore(ItemStack item) {

        String name = ItemUtils.getMetadata(item, SHOP_META, String.class);
        dShop shop = DailyShop.get().getShopsManager().getShop(name).orElse(null);

        ItemBuilder builder = new ItemBuilder(item)
                .addLore(Lang.SHOPS_MANAGER_LORE_1.getAsListString(
                                Template.of("timer", shop.getTimer()),
                                Template.of("amount", shop.size()),
                                Template.of("c_timer", getShopTimerFormatted(shop))
                        )
                );

        if (shop.getAccount() != null) {

            builder = builder
                    .addLore(Lang.SHOPS_MANAGER_LORE_2.getAsListString(
                            Template.of("c_balance", PrettyPrice.pretty(shop.getAccount().getBalance())),
                            Template.of("max_balance", getMaxBalanceFormatted(shop.getAccount().getMaxBalance())),
                            Template.of("type_balance", shop.getAccount().getGenerator().toString())
                    ));

        }

        return builder.addLore(Lang.SHOPS_MANAGER_LORE_3.getAsListString());
    }

    private static String getShopTimerFormatted(dShop shop) {
        if (shop.getTimer() == -1)
            return XSymbols.TIMES_3.parseSymbol();
        else
            return Utils.getDiffActualTimer(shop);
    }

    private static String getMaxBalanceFormatted(double mBalance) {
        if (mBalance == Double.MAX_VALUE)
            return XSymbols.INFINITY.parseSymbol();
        else
            return PrettyPrice.pretty(mBalance);
    }

}

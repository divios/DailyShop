package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.inventory.ItemStack;

public class shopsManagerLore implements loreStrategy{

    @Override
    public void setLore(ItemStack item) {
        ItemUtils.translateAllItemData(new ItemBuilder(item)
            .addLore("",
                    "&6Left Click: &7Manage items",
                    "&6Right Click: &7Delete shop",
                    "&6Middle Click: &7Change shop name",
                    "&6Q Click: &7Change reset time (Actual: &6" +
                            shopsManager.getInstance()
                                    .getShop(FormatUtils.stripColor(
                                            item.getItemMeta().getDisplayName()))
                                .get().getTimer()
                                + "&7)",
                    "",
                    "&6Shift Left Click: &7Customize shop appearance"),
                item);
    }

    @Override
    public void removeLore(ItemStack item) {  // Not needed

    }

    @Override
    public void update(ItemStack item) {    // Not needed

    }
}

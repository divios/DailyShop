package io.github.divios.dailyShop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class shopsManagerLore implements loreStrategy{

    @Override
    public void setLore(ItemStack item) {

        String name = FormatUtils.stripColor(item.getItemMeta().getDisplayName());

        List<String> placeholder = Msg.msgList(conf_msg.SHOPS_MANAGER_LORE)
                .add("\\{amount}", "" + shopsManager.getInstance()
                        .getShop(name).get().getTimer()
                ).build();

        ItemUtils.translateAllItemData(new ItemBuilder(item)
            .addLore(placeholder), item);

    }

    @Override
    public void removeLore(ItemStack item) {  // Not needed

    }

    @Override
    public void update(ItemStack item) {    // Not needed

    }
}

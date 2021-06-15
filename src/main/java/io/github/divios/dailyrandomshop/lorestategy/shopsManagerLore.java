package io.github.divios.dailyrandomshop.lorestategy;

import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class shopsManagerLore implements loreStrategy{

    @Override
    public void setLore(ItemStack item) {
        ItemUtils.translateAllItemData(new ItemBuilder(item)
            .addLore("",
                    "&6Left click: &7Manage items",
                    "&6Right click: &7Delete shop",
                    "&6Middle click: &7Change shop name",
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

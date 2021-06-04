package io.github.divios.dailyrandomshop.guis;

import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;


public class confirmIH {

    private final static DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final BiConsumer<Player, Boolean> bi;
    private final ItemStack item;
    private final String title;
    private final String confirmLore;
    private final String cancelLore;
    private boolean backFlag = true;
    private static final DRShop main = DRShop.getInstance();

    /**
     * @param p          Player to show the GUI
     * @param true_false Block of code to execute
     * @param title      Title of the GUI
     */

    public confirmIH(Player p,
                     BiConsumer<Player , Boolean> true_false,
                     ItemStack item,
                     String title,
                     String confirmLore,
                     String cancelLore) {

        this.p = p;
        this.item = item;
        bi = true_false;
        this.title = FormatUtils.color(title);
        this.confirmLore = confirmLore;
        this.cancelLore = cancelLore;
        openInventory();
    }

    public void openInventory() {

        InventoryGUI gui = new InventoryGUI(plugin, 27, title);

        gui.addButton(ItemButton.create(new ItemBuilder(
                utils.isEmpty(item) ? XMaterial.AIR.parseItem():item), e -> {}), 4);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.EMERALD_BLOCK)
                .setName(confirmLore), (e) -> bi.accept(p, true)), 11);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.REDSTONE_BLOCK)
                .setName(cancelLore), (e) -> bi.accept(p, false)), 15);

        gui.destroysOnClose();
        gui.open(p);
    }


}

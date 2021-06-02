package io.github.divios.dailyrandomshop.guis;

import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.redLib.inventorygui.InventoryGUI;
import io.github.divios.dailyrandomshop.redLib.inventorygui.ItemButton;
import io.github.divios.dailyrandomshop.redLib.itemutils.ItemBuilder;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class confirmIH {

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
        this.title = utils.formatString(title);
        this.confirmLore = confirmLore;
        this.cancelLore = cancelLore;
        openInventory();
    }

    public void openInventory() {

        InventoryGUI gui = new InventoryGUI(27, title);

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

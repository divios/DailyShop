package io.github.divios.dailyShop.guis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.utils.utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.stream.IntStream;


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
        this.item = item.clone();
        bi = true_false;
        this.title = FormatUtils.color(title);
        this.confirmLore = confirmLore;
        this.cancelLore = cancelLore;
        openInventory();
    }

    public void openInventory() {

        InventoryGUI gui = new InventoryGUI(plugin, 27, title);

        IntStream.of(0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26).forEach(value -> {
            gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                    .setName("&c"), e -> {}), value); });

        gui.addButton(ItemButton.create(new ItemBuilder(
                utils.isEmpty(item) ? XMaterial.AIR.parseItem():item), e -> {}), 13);

        IntStream.range(9, 13).forEach(value ->
                gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE)
                .setName(confirmLore), (e) -> bi.accept(p, true)), value));

        IntStream.range(14, 18).forEach(value ->
                gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE)
                .setName(cancelLore), (e) -> bi.accept(p, false)), value));

        gui.destroysOnClose();
        gui.open(p);
    }


}

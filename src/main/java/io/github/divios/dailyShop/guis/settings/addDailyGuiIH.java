package io.github.divios.dailyShop.guis.settings;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ItemPrompt;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.guis.customizerguis.changeBundleItem;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class addDailyGuiIH {

    private static final DailyShop plugin = DailyShop.getInstance();

    private final Player p;
    private final dShop shop;
    private final Consumer<ItemStack> onComplete;
    private final Runnable back;

    public static void open(Player p, dShop shop, Consumer<ItemStack> consumer, Runnable back) {
        new addDailyGuiIH(p, shop, consumer, back);
    }

    private addDailyGuiIH(Player p,
                          dShop shop,
                          Consumer<ItemStack> onComplete,
                          Runnable back
    ) {
        this.p = p;
        this.shop = shop;
        this.onComplete = onComplete;
        this.back = back;

        body();
    }

    private void body() {

        InventoryGUI gui = new InventoryGUI(plugin, 27, plugin.configM.getLangYml().ADD_ITEMS_TITLE);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.REDSTONE_TORCH)
                        .setName(plugin.configM.getLangYml().ADD_ITEMS_FROM_ZERO)
                        .addLore(plugin.configM.getLangYml().ADD_ITEMS_FROM_ZERO_LORE)
                , e -> onComplete.accept(XMaterial.GRASS.parseItem())), 11);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.HOPPER)
                        .setName(plugin.configM.getLangYml().ADD_ITEMS_FROM_EXISTING)
                        .addLore(plugin.configM.getLangYml().ADD_ITEMS_FROM_EXISTING_LORE)
                , e -> {
                    ItemPrompt.builder()
                            .withPlayer(p)
                            .withComplete(onComplete)
                            .withTitle(plugin.configM.getLangYml().ADD_ITEMS_TITLE)
                            .build();

                    p.closeInventory();
                }), 15);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.CHEST_MINECART)
                        .setName(plugin.configM.getLangYml().ADD_ITEMS_FROM_BUNDLE)
                        .addLore(plugin.configM.getLangYml().ADD_ITEMS_FROM_BUNDLE_LORE)
                , e -> {
                    new changeBundleItem(p, dItem.of(XMaterial.CHEST_MINECART.parseItem()), shop,
                            (player, uuids) -> {
                                gui.destroy();
                                dItem newBundle = dItem.of(XMaterial.CHEST_MINECART.parseItem());
                                newBundle.setBundle(uuids);
                                shop.addItem(newBundle);
                                shopGui.open(p, shop);
                            }, gui::open);
                }), 13);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.PLAYER_HEAD)
                        .setName(plugin.configM.getLangYml().ADD_ITEMS_RETURN)
                        .addLore(plugin.configM.getLangYml().ADD_ITEMS_RETURN_LORE)
                        .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                , e -> back.run()), 22);

        IntStream.range(0, 27).forEach(value -> {
            if (utils.isEmpty(gui.getInventory().getItem(value)))
                gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                        , e -> {
                        }), value);
        });


        gui.setDestroyOnClose(false);
        gui.open(p);
    }

}
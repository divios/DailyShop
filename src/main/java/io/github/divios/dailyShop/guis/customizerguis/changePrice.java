package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.IntStream;


public class changePrice {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final dItem item;
    private final dShop shop;
    private final dShop.dShopT type;
    private final Runnable accept;
    private final Runnable back;

    public changePrice(
            Player p,
            dItem item,
            dShop shop,
            dShop.dShopT type,
            Runnable accept,
            Runnable back
    ) {

        this.p = p;
        this.item = item;
        this.type = type;
        this.shop = shop;
        this.accept = accept;
        this.back = back;

        open();
    }

    public void open() {

        InventoryGUI gui = new InventoryGUI(plugin, 27, "&bChange price");

        IntStream.of(0, 1, 9, 18, 19, 7, 8, 17, 25, 26)
                .forEach(value -> gui.addButton(value, new ItemButton(new ItemBuilder(XMaterial.BLUE_STAINED_GLASS_PANE)
                        .setName("&c"), e -> {
                })));

        IntStream.of(2, 3, 5, 6, 10, 16, 20, 21, 23, 24)
                .forEach(value -> gui.addButton(value, new ItemButton(new ItemBuilder(XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE)
                        .setName("&c"), e -> {
                })));

        IntStream.of(4, 12, 13, 14)
                .forEach(value -> gui.addButton(value, new ItemButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                        .setName("&c"), e -> {
                })));

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.SUNFLOWER)
                        .setName("&6&lSet fixed price").addLore("&7The item 'll always have", "&7the given price"),
                e -> ChatPrompt.prompt(plugin, p, (s) -> {

                    if (!utils.isDouble(s)) {
                        utils.sendMsg(p, "&7Not double");
                        Task.syncDelayed(plugin, back, 0L);
                        return;
                    }
                    double price = Double.parseDouble(s);
                    if (price <= 0) price = -1;
                    if (type == dShop.dShopT.buy)
                        item.setBuyPrice(price);
                    else
                        item.setSellPrice(price);

                    Task.syncDelayed(plugin, () -> Bukkit.getPluginManager().callEvent(
                            new updateItemEvent(item,
                                    updateItemEvent.updatetype.UPDATE_ITEM, shop)));

                    Task.syncDelayed(plugin, accept);
                }, cause -> Task.syncDelayed(plugin, back), "&6&lInput new Price", "")), 11);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.REPEATER)
                        .setName("&c&lSet interval").addLore("&7The price of the item",
                        "&7will take a random value between", "&7the given interval"),
                e -> {

                    ChatPrompt.prompt(plugin, p, (s) -> {

                                String[] pricesS = s.split(":");

                                if (pricesS.length != 2) {
                                    utils.sendMsg(p, "&7Wrong format -> minPrice:maxPrice (Ex 30:50)");
                                    Task.syncDelayed(plugin, back, 0L);
                                    return;
                                }

                                Double[] prices;

                                try {
                                    prices = new Double[]{Double.parseDouble(pricesS[0]),
                                            Double.parseDouble(pricesS[1])};
                                } catch (Exception err) {
                                    utils.sendMsg(p, "&7Not double");
                                    Task.syncDelayed(plugin, back, 0L);
                                    return;
                                }

                                if (prices[0] >= prices[1]) {
                                    utils.sendMsg(p, "&7Max price can't be lower than min price");
                                    Task.syncDelayed(plugin, back, 0L);
                                    return;
                                }


                                if (type == dShop.dShopT.buy)
                                    item.setBuyPrice(prices[0], prices[1]);
                                else
                                    item.setSellPrice(prices[0], prices[1]);

                                Task.syncDelayed(plugin, () -> Bukkit.getPluginManager().callEvent(
                                        new updateItemEvent(item,
                                                updateItemEvent.updatetype.UPDATE_ITEM, shop)));

                                Task.syncDelayed(plugin, accept);
                            }, player -> Task.syncDelayed(plugin, back),
                            "&6&lInput new Price", "&8Format: minPrice:maxPrice");

                }), 15);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.PLAYER_HEAD)
                        .setName(conf_msg.CONFIRM_GUI_RETURN_NAME)
                        .setLore(conf_msg.CONFIRM_GUI_RETURN_PANE_LORE)
                        .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")
                , e -> back.run()), 22);

        gui.destroysOnClose();
        gui.open(p);
    }


}
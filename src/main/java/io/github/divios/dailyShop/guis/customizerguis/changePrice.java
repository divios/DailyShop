package io.github.divios.dailyShop.guis.customizerguis;

import com.google.common.util.concurrent.AtomicDouble;
import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.conf_msg;
import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;


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

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.SUNFLOWER)
                        .setName("&6&lSet fixed price").addLore("&7The item 'll always have", "&7the given price"),
                e -> new AnvilGUI.Builder()
                        .onComplete((player, text) -> {
                            try {
                                Double.parseDouble(text);
                            } catch (NumberFormatException err) {
                                return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER);
                            }

                            double price = Double.parseDouble(text);
                            if (price <= 0) price = -1;
                            if (type == dShop.dShopT.buy)
                                item.setBuyPrice(price);
                            else
                                item.setSellPrice(price);

                            Bukkit.getPluginManager().callEvent(
                                    new updateItemEvent(item,
                                            updateItemEvent.updatetype.UPDATE_ITEM, shop));

                            accept.run();
                            return AnvilGUI.Response.close();
                        })
                        .text(conf_msg.DAILY_ITEMS_MENU_ANVIL_DEFAULT_TEXT)
                        .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                        .title(conf_msg.DAILY_ITEMS_MENU_ANVIL_TITLE)
                        .plugin(DRShop.getInstance())
                        .open(p)), 11);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.REPEATER)
                        .setName("&c&lSet interval").addLore("&7The price of the item",
                        "&7will take a random value between", "&7the given interval"),
                e -> {
                    AtomicBoolean closeFlag = new AtomicBoolean(false);
                    AtomicDouble aux = new AtomicDouble();

                    new AnvilGUI.Builder()
                            .onClose(p -> Task.syncDelayed(plugin, back, 1L))
                            .onComplete((player, text) -> {
                                try {
                                    Double.parseDouble(text);
                                } catch (NumberFormatException err) {
                                    return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER);
                                }
                                closeFlag.set(true);
                                aux.set(Double.parseDouble(text));
                                Task.syncDelayed(plugin, () -> new AnvilGUI.Builder()
                                        .onComplete((player1, text1) -> {
                                            try {
                                                Double.parseDouble(text1);
                                            } catch (NumberFormatException err) {
                                                return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER);
                                            }

                                            if (aux.get() >= Double.parseDouble(text1))
                                                return AnvilGUI.Response.text("Max price can't be lower than min price");

                                            if (type == dShop.dShopT.buy)
                                                item.setBuyPrice(Double.parseDouble(text), Double.parseDouble(text1));
                                            else
                                                item.setSellPrice(Double.parseDouble(text), Double.parseDouble(text1));

                                            Bukkit.getPluginManager().callEvent(
                                                    new updateItemEvent(item,
                                                            updateItemEvent.updatetype.UPDATE_ITEM, shop));

                                            accept.run();
                                            return AnvilGUI.Response.close();
                                        })
                                        .text("input max price")
                                        .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                                        .title("Input max price")
                                        .plugin(plugin)
                                        .open(p), 1L);
                                return AnvilGUI.Response.close();
                            })
                            .onClose(p -> Task.syncDelayed(plugin, () -> {
                                if (closeFlag.get()) return;
                                back.run();
                            }, 1L))
                            .text("input min price")
                            .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                            .title("Input min price")
                            .plugin(plugin)
                            .open(p);

                }), 15);

        gui.addButton(ItemButton.create(new ItemBuilder(XMaterial.OAK_SIGN)
                        .setName(conf_msg.CONFIRM_GUI_RETURN_NAME)
                        .setLore(conf_msg.CONFIRM_GUI_RETURN_PANE_LORE)
                , e -> back.run()), 22);

        gui.destroysOnClose();
        gui.open(p);
    }


}
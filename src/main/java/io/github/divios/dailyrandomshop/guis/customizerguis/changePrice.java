package io.github.divios.dailyrandomshop.guis.customizerguis;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.lib.itemHolder.dItem;
import io.github.divios.lib.itemHolder.dShop;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class changePrice {

    private static final DRShop plugin = DRShop.getInstance();

    private final Player p;
    private final dItem item;
    private final dShop shop;
    private final Runnable accept;
    private final Runnable back;

    public changePrice(
            Player p,
            dItem item,
            dShop shop,
            Runnable accept,
            Runnable back
    ) {

        this.p = p;
        this.item = item;
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

                            //item.setPDouble.parseDouble(text))).getItem();

                            //buyGui.getInstance().updateItem(dailyItem.getUuid(this.item),
                            //      buyGui.updateAction.update);

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
                    AtomicDouble aux = new AtomicDouble();

                    new AnvilGUI.Builder()
                            .onComplete((player, text) -> {
                                try {
                                    Double.parseDouble(text);
                                } catch (NumberFormatException err) {
                                    return AnvilGUI.Response.text(conf_msg.MSG_NOT_INTEGER);
                                }

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

                                            //new dailyItem(this.item)
                                            //      .addNbt(dailyItem.dMeta.rds_price,
                                            //          new dailyItem.dailyItemPrice(aux.get(),
                                            //                    Double.parseDouble(text1))).getItem();

                                            //buyGui.getInstance().updateItem(dailyItem.getUuid(this.item),
                                            //      buyGui.updateAction.update);

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
                            .text("input min price")
                            .itemLeft(new ItemStack(XMaterial.EMERALD.parseMaterial()))
                            .title("Input max price")
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
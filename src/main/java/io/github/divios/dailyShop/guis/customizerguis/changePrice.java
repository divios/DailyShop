package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.inventory.builder.inventoryPopulator;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;

import java.util.function.Consumer;


public class changePrice {

    private static final DailyShop plugin = DailyShop.get();

    private final Player p;
    private final dItem item;
    private final Type type;
    private final dShop shop;
    private final Consumer<dItem> accept;
    private final Runnable back;

    @Deprecated
    public changePrice(
            Player p,
            dItem item,
            dShop shop,
            Type type,
            Consumer<dItem> accept,
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

        inventoryPopulator.builder()
                .ofGlass()
                .mask("111111111")
                .mask("110111011")
                .mask("111101111")
                .scheme(13, 13, 5, 5, 0, 5, 5, 13, 13)
                .scheme(13, 5, 0, 0, 0, 5, 13)
                .scheme(13, 13, 5, 5, 5, 5, 13, 13)
                .apply(gui.getInventory());

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.SUNFLOWER)
                                .setName("&6&lSet fixed price").addLore("&7The item 'll always have", "&7the given price"),

                        e ->
                                ChatPrompt.builder()
                                        .withPlayer(p)
                                        .withResponse(s -> {

                                            if (!Utils.isDouble(s)) {
                                                Utils.sendRawMsg(p, "&7Not double");
                                                Schedulers.sync().run(back);
                                                return;
                                            }
                                            double price = Double.parseDouble(s);
                                            if (price < 0) price = -1;

                                            if (type == Type.BUY)
                                                item.setBuyPrice(price);
                                            else item.setSellPrice(price);

                                            accept.accept(item);
                                        })
                                        .withCancel(cancelReason -> Schedulers.sync().run(back))
                                        .withTitle("&6&lInput new Price")
                                        .prompt()),

                11);

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.REPEATER)
                        .setName("&c&lSet interval").addLore("&7The price of the item",
                                "&7will take a random value between", "&7the given interval"),
                e -> {

                    ChatPrompt.builder()
                            .withPlayer(p)
                            .withResponse(s -> {

                                String[] pricesS = s.split(":");

                                if (pricesS.length != 2) {
                                    Utils.sendRawMsg(p, "&7Wrong format -> minPrice:maxPrice (Ex 30:50)");
                                    Schedulers.sync().run(back);
                                    return;
                                }

                                Double[] prices;

                                try {
                                    prices = new Double[]{Double.parseDouble(pricesS[0]),
                                            Double.parseDouble(pricesS[1])};
                                } catch (Exception err) {
                                    Utils.sendRawMsg(p, "&7Not double");
                                    Schedulers.sync().run(back);
                                    return;
                                }

                                if (prices[0] >= prices[1]) {
                                    Utils.sendRawMsg(p, "&7Max price can't be lower than min price");
                                    Schedulers.sync().run(back);
                                    return;
                                }

                                if (type == Type.BUY)
                                    item.setBuyPrice(prices[0], prices[1]);
                                else item.setSellPrice(prices[0], prices[1]);

                                accept.accept(item);

                            })

                            .withCancel(cancelReason -> Schedulers.sync().run(back))
                            .withTitle("&6&lInput new Price")
                            .withSubtitle("&7Format: minPrice:maxPrice")
                            .prompt();


                }), 15);

        gui.addButton(ItemButton.create(new
                        ItemBuilder(XMaterial.PLAYER_HEAD)
                        .setName(Lang.CONFIRM_GUI_RETURN_NAME.getAsString(p))
                        .setLore(Lang.CONFIRM_GUI_RETURN_PANE_LORE.getAsListString(p))
                        .applyTexture("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf")

                , e -> back.run()), 22);

        gui.destroysOnClose();
        gui.open(p);
    }

    public static changePriceBuilder builder() {
        return new changePriceBuilder();
    }

    public static final class changePriceBuilder {
        private Player p;
        private dItem item;
        private Type type;
        private Consumer<dItem> accept;
        private Runnable back;

        private changePriceBuilder() {
        }

        public changePriceBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public changePriceBuilder withItem(dItem item) {
            this.item = item;
            return this;
        }

        public changePriceBuilder withType(Type type) {
            this.type = type;
            return this;
        }

        public changePriceBuilder withAccept(Consumer<dItem> accept) {
            this.accept = accept;
            return this;
        }

        public changePriceBuilder withBack(Runnable back) {
            this.back = back;
            return this;
        }

        public changePrice prompt() {
            // TODO: add preconditions
            return new changePrice(p, item, null, type, accept, back);
        }
    }

    public enum Type {
        BUY,
        SELL
    }

}
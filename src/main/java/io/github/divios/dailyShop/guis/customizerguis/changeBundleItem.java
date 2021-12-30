package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.inventory.dynamicGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class changeBundleItem {


    private final Player p;
    private final dItem item;
    private final dShop shop;
    private final Consumer<List<String>> confirm;
    private final Runnable back;

    @Deprecated
    public changeBundleItem(
            Player p,
            dItem item,
            dShop shop,
            Consumer<List<String>> confirm,
            Runnable back
    ) {
        this.p = p;
        this.item = item.clone();
        this.shop = shop;
        this.confirm = confirm;
        this.back = back;

        open();
    }

    public static changeBundleItemBuilder builder() {
        return new changeBundleItemBuilder();
    }

    private void open() {

        List<String> added = item.getBundle().orElse(new ArrayList<>());
        String ownId = item.getID();

        new dynamicGui.Builder()
                .contents(() -> {
                    //loreStrategy ls = new bundleSettingsLore();
                    return shop.getItems().stream()
                            .filter(dItem -> !dItem.getID().equals(ownId))
                            .map(dItem -> dItem.getDailyItem().clone())  // Todo Aplicar lore strategy a los items
                            .map(_item -> {
                                if (added.contains(dItem.getId(_item))) {
                                    _item = ItemUtils.addEnchant(_item, Enchantment.DAMAGE_ALL, 1);
                                    _item = ItemUtils.addItemFlags(_item, ItemFlag.HIDE_ENCHANTS);
                                }

                                return _item;
                            }).collect(Collectors.toList());

                }).addItems((inventory, integer) ->
                        inventory.setItem(47, ItemBuilder.of(XMaterial.EMERALD_BLOCK)
                                .setName("&6&lConfirm").setLore("&7Click to confirm")))

                .contentAction(event -> {

                    String uid = dItem.getId(event.getCurrentItem());
                    if (added.contains(uid)) {
                        added.remove(uid);
                    } else {
                        added.add(uid);
                    }
                    return dynamicGui.Response.update();
                }).nonContentAction((integer, player) -> {

                    if (integer == 47) {
                        confirm.accept(added);
                    }
                    return dynamicGui.Response.nu();

                }).setSearch(false)
                .back(player -> back.run())
                .title(_i -> "&6Set items on the bundle")
                .plugin(DailyShop.get())
                .open(p);

    }

    public static final class changeBundleItemBuilder {
        private Player p;
        private dItem item;
        private dShop shop;
        private Consumer<List<String>> confirm;
        private Runnable back;

        private changeBundleItemBuilder() {
        }

        public changeBundleItemBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public changeBundleItemBuilder withItem(dItem item) {
            this.item = item;
            return this;
        }

        public changeBundleItemBuilder withShop(dShop shop) {
            this.shop = shop;
            return this;
        }

        public changeBundleItemBuilder withConfirm(Consumer<List<String>> confirm) {
            this.confirm = confirm;
            return this;
        }

        public changeBundleItemBuilder withBack(Runnable back) {
            this.back = back;
            return this;
        }

        public changeBundleItem prompt() {
            return new changeBundleItem(p, item, shop, confirm, back);
        }
    }
}
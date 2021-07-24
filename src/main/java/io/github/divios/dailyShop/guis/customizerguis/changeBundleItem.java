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
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class changeBundleItem {


    private final Player p;
    private final dItem item;
    private final dShop shop;
    private final BiConsumer<Player, List<UUID>> confirm;
    private final Consumer<Player> back;

    @Deprecated
    public changeBundleItem(
            Player p,
            dItem item,
            dShop shop,
            BiConsumer<Player, List<UUID>> confirm,
            Consumer<Player> back
    ) {
        this.p = p;
        this.item = item.clone();
        this.shop = shop;
        this.confirm = confirm;
        this.back = back;

        open();
    }

    private void open() {

        List<UUID> added = item.getBundle().orElse(new ArrayList<>());
        UUID ownId = item.getUid();

        new dynamicGui.Builder()
                .contents(() -> {
                    //loreStrategy ls = new bundleSettingsLore();
                    return shop.getItems().stream()
                            .filter(dItem -> !dItem.getUid().equals(ownId))
                            .map(dItem -> dItem.getItem().clone())  // Todo Aplicar lore strategy a los items
                            .map(_item -> {
                                if (added.contains(dItem.getUid(_item))) {
                                    _item = ItemUtils.addEnchant(_item, Enchantment.DAMAGE_ALL, 1);
                                    _item = ItemUtils.addItemFlags(_item, ItemFlag.HIDE_ENCHANTS);
                                }

                                return _item;
                            }).collect(Collectors.toList());

                }).addItems((inventory, integer) ->
                inventory.setItem(47, new ItemBuilder(XMaterial.EMERALD_BLOCK)
                        .setName("&6&lConfirm").setLore("&7Click to confirm")))

                .contentAction(event -> {

                    UUID uid = dItem.getUid(event.getCurrentItem());
                    if (added.contains(uid)) {
                        added.remove(uid);
                    } else {
                        added.add(uid);
                    }
                    return dynamicGui.Response.update();
                }).nonContentAction((integer, player) -> {

            if (integer == 47) {
                confirm.accept(p, added);
            }
            return dynamicGui.Response.nu();

        }).setSearch(false)
                .back(back)
                .title(_i -> "&6Set items on the bundle")
                .plugin(DailyShop.getInstance())
                .open(p);

    }

}
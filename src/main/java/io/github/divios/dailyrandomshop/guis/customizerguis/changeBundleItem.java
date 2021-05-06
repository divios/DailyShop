package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.builders.dynamicGui;
import io.github.divios.dailyrandomshop.builders.factory.dailyItem;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.lorestategy.bundleSettingsLore;
import io.github.divios.dailyrandomshop.lorestategy.loreStrategy;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class changeBundleItem {

    private static final dataManager dbManager = dataManager.getInstance();

    private final Player p;
    private final ItemStack item;
    private final BiConsumer<Player, ItemStack> confirm;
    private final Consumer<Player> back;

    public changeBundleItem (
            Player p,
            ItemStack item,
            BiConsumer<Player, ItemStack> confirm,
            Consumer<Player> back
    ) {
       this.p = p;
       this.item = item.clone();
       this.confirm = confirm;
       this.back = back;

       open();
    }

    private void open() {

        List<String> added = (List<String>) new dailyItem(item)
                .getMetadata(dailyItem.dailyMetadataType.rds_bundle);

        String ownId = dailyItem.getUuid(item);

        new dynamicGui.Builder()
                .contents(() -> {
                    loreStrategy ls = new bundleSettingsLore();
                    List<ItemStack> contents = new ArrayList<>();

                    for (ItemStack i: dbManager.listDailyItems.keySet()) {

                        if (dailyItem.getUuid(i).equals(ownId))
                            continue;

                        ItemStack aux = i.clone();
                        ls.setLore(aux);

                        if (added.contains(dailyItem.getUuid(aux)) ) {
                            aux.addUnsafeEnchantment(Enchantment.LUCK, 1);
                            utils.addFlag(aux, ItemFlag.HIDE_ENCHANTS);
                        }
                        contents.add(aux);
                    }
                    return contents;
                }).addItems((inventory, integer) -> {
                    ItemStack confirm = XMaterial.EMERALD_BLOCK.parseItem();
                    utils.setDisplayName(confirm, "&6&lConfirm");
                    utils.setLore(confirm, Arrays.asList("&7Click to confirm"));
                    inventory.setItem(47, confirm);
                }).contentAction(event -> {
                    String s = dailyItem.getUuid(event.getCurrentItem());
                    if (!added.contains(s))
                        added.add(s);
                    else
                        added.remove(s);
                    return dynamicGui.Response.update();
                }).nonContentAction((integer, player) -> {
                    if (integer == 47) {
                        new dailyItem(item)
                                .removeNbt(dailyItem.dailyMetadataType.rds_bundle).getItem();
                        for (String _s: added)
                            new dailyItem(item)
                                    .addNbt(dailyItem.dailyMetadataType.rds_bundle, _s)
                                    .getItem();

                        confirm.accept(p, item);
                    }
                    return dynamicGui.Response.nu();
                }).setSearch(false)
                .back(back)
                .title(_i -> "&6Set items on the bundle")
                .open(p);

    }

}

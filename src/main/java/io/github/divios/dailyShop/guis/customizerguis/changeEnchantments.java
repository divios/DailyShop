package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.inventory.dynamicGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class changeEnchantments {

    private static final DailyShop plugin = DailyShop.get();

    private static final List<ItemStack> contentsList = contents();
    private final Player p;
    private final dItem ditem;
    private final dShop shop;
    private Map<Enchantment, Integer> e;

    private changeEnchantments (
            Player p,
            dItem ditem,
            dShop shop
    ) {
        this.p = p;
        this.ditem = ditem;
        this.shop = shop;

        new dynamicGui.Builder()
                .contents(this::getContents)
                .contentAction(this::contentAction)
                .back(this::backAction)
                .plugin(plugin)
                .open(p);
    }

    private changeEnchantments (
            Player p,
            dItem ditem,
            dShop shop,
            Map<Enchantment, Integer> e
    ) {
        this.p = p;
        this.ditem = ditem;
        this.shop = shop;
        this.e = e;

        new dynamicGui.Builder()
                .contents(this::contentsX)
                .contentAction(this::contentActionX)
                .back(this::backAction)
                .plugin(plugin)
                //.preventClose()
                .open(p);
    }

    @Deprecated
    public static void openInventory(Player p, dItem ditem, dShop shop) {
        new changeEnchantments(p, ditem, shop);
    }

    private static List<ItemStack> contents() {
        List<ItemStack> contents = new ArrayList<>();
        for(Enchantment e : Enchantment.values()) {
            ItemStack item = ItemBuilder.of(XMaterial.BOOK.parseItem())
                    .setName("&f&l" + e.getName());
            contents.add(item);
        }
        return contents;
    }

    private List<ItemStack> getContents() {
        return contentsList;
    }

    private void backAction(Player p) {
        CustomizerMenu.open(p, ditem, shop);
    }

    private dynamicGui.Response contentAction(InventoryClickEvent e) {

        String s = FormatUtils.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        ChatPrompt.prompt(plugin, p, s1 -> {

            if (!Utils.isInteger(s1)) {
                Utils.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_INTEGER);
                Schedulers.sync().run(()  -> CustomizerMenu.open(p, ditem, shop));
            }
            ditem.addEnchantments(Enchantment.getByName(s), Integer.parseInt(s1));
                    Schedulers.sync().run(()  -> CustomizerMenu.open(p, ditem, shop));

        }, cause -> Schedulers.sync().run(() -> CustomizerMenu.open(p, ditem, shop)),
                "&1&lInput Enchant lvl", "");

        return dynamicGui.Response.nu();
    }

    public static void openInventory(Player p, dItem ditem, Map<Enchantment, Integer> e, dShop shop) {
        new changeEnchantments(p, ditem, shop, e);
    }

    private List<ItemStack> contentsX() {
        List<ItemStack> contents = new ArrayList<>();
        for(Map.Entry<Enchantment, Integer> e : e.entrySet()) {
            ItemStack item = ItemBuilder.of(XMaterial.ENCHANTED_BOOK.parseItem())
                    .setName("&f&l" + e.getKey().getName() + ":" + e.getValue());
            contents.add(item);
        }
        return contents;
    }

    private dynamicGui.Response contentActionX(InventoryClickEvent e) {
        String[] entry = FormatUtils.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).split(":");
        ditem.removeEnchantments(Enchantment.getByName(entry[0]));
        CustomizerMenu.open(p, ditem, shop);
        return dynamicGui.Response.nu();


    }

    public static changeEnchantmentsBuilder builder() { return new changeEnchantmentsBuilder(); }

    public static final class changeEnchantmentsBuilder {
        private Player p;
        private dItem ditem;
        private dShop shop;
        private Map<Enchantment, Integer> e;

        private changeEnchantmentsBuilder() { }

        public changeEnchantmentsBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public changeEnchantmentsBuilder withDitem(dItem ditem) {
            this.ditem = ditem;
            return this;
        }

        public changeEnchantmentsBuilder withShop(dShop shop) {
            this.shop = shop;
            return this;
        }

        public changeEnchantmentsBuilder withEnchants(Map<Enchantment, Integer> e) {
            this.e = e;
            return this;
        }

        public changeEnchantments prompt() {

            Preconditions.checkNotNull(p, "player null");
            Preconditions.checkNotNull(ditem, "item null");
            Preconditions.checkNotNull(shop, "shop null");

            if (e == null) return new changeEnchantments(p, ditem, shop);
            else return new changeEnchantments(p, ditem, shop, e);
        }
    }
}
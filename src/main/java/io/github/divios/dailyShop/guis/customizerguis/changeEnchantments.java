package io.github.divios.dailyShop.guis.customizerguis;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import io.github.divios.core_lib.inventory.dynamicGui;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.ChatPrompt;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Messages;
import io.github.divios.dailyShop.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings({"deprecation", "unused"})
public class changeEnchantments {

    private static final DailyShop plugin = DailyShop.get();

    private static final List<ItemStack> contentsList = contents();
    private final Player p;
    private ItemStack item;
    private Map<Enchantment, Integer> enchantments;
    private final Consumer<ItemStack> accept;
    private final Runnable fallback;

    private changeEnchantments(
            Player p,
            ItemStack item,
            Consumer<ItemStack> accept,
            Runnable fallback
    ) {
        this.p = p;
        this.item = item;
        this.accept = accept;
        this.fallback = fallback;

        new dynamicGui.Builder()
                .contents(this::getContents)
                .contentAction(this::contentAction)
                .back(player -> fallback.run())
                .plugin(plugin)
                .open(p);
    }

    private changeEnchantments(
            Player p,
            ItemStack item,
            Map<Enchantment, Integer> e,
            Consumer<ItemStack> accept,
            Runnable fallback
    ) {
        this.p = p;
        this.item = item;
        this.enchantments = e;
        this.accept = accept;
        this.fallback = fallback;

        new dynamicGui.Builder()
                .contents(this::contentsX)
                .contentAction(this::contentActionX)
                .back(player -> fallback.run())
                .plugin(plugin)
                //.preventClose()
                .open(p);
    }

    @Deprecated
    public static void openInventory(Player p, ItemStack item, Consumer<ItemStack> accept, Runnable fallback) {
        new changeEnchantments(p, item, accept, fallback);
    }

    private static List<ItemStack> contents() {
        List<ItemStack> contents = new ArrayList<>();
        for (Enchantment e : Enchantment.values()) {
            ItemStack item = ItemBuilder.of(getBook())
                    .setName("&f&l" + e.getName());
            contents.add(item);
        }
        return contents;
    }

    private List<ItemStack> getContents() {
        return contentsList;
    }

    private dynamicGui.Response contentAction(InventoryClickEvent e) {

        if (e.getCurrentItem() == null) return dynamicGui.Response.nu();

        String s = FormatUtils.stripColor(ItemUtils.getName(e.getCurrentItem()));

        ChatPrompt.prompt(plugin, p, s1 -> {

                    if (!Utils.isInteger(s1)) {
                        Messages.MSG_NOT_INTEGER.send(p);
                        Schedulers.sync().run(fallback);
                    }
                    item = ItemUtils.addEnchant(item, Enchantment.getByName(s), Integer.parseInt(s1));
                    Schedulers.sync().run(() -> accept.accept(item));

                }, cause -> Schedulers.sync().run(() -> accept.accept(item)),
                "&1&lInput Enchant lvl", "");

        return dynamicGui.Response.nu();
    }

    private List<ItemStack> contentsX() {
        List<ItemStack> contents = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
            ItemStack item = ItemBuilder.of(getBook())
                    .setName("&f&l" + e.getKey().getName() + ":" + e.getValue());
            contents.add(item);
        }
        return contents;
    }

    private dynamicGui.Response contentActionX(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return dynamicGui.Response.nu();

        String[] entry = FormatUtils.stripColor(ItemUtils.getName(e.getCurrentItem())).split(":");
        item = ItemUtils.removeEnchant(item, Enchantment.getByName(entry[0]));
        accept.accept(item);
        return dynamicGui.Response.nu();

    }

    private static ItemStack getBook() {
        ItemStack book = XMaterial.BOOK.parseItem();
        return book == null ? new ItemStack(Material.BOOK) : book;
    }

    public static changeEnchantmentsBuilder builder() {
        return new changeEnchantmentsBuilder();
    }

    public static final class changeEnchantmentsBuilder {
        private Player p;
        private ItemStack item;
        private Map<Enchantment, Integer> e;
        private Consumer<ItemStack> accept;
        private Runnable fallback;

        private changeEnchantmentsBuilder() {
        }

        public changeEnchantmentsBuilder withPlayer(Player p) {
            this.p = p;
            return this;
        }

        public changeEnchantmentsBuilder withItem(ItemStack item) {
            this.item = item;
            return this;
        }

        public changeEnchantmentsBuilder withEnchants(Map<Enchantment, Integer> e) {
            this.e = e;
            return this;
        }

        public changeEnchantmentsBuilder withAccept(Consumer<ItemStack> accept) {
            this.accept = accept;
            return this;
        }

        public changeEnchantmentsBuilder withFallback(Runnable fallback) {
            this.fallback = fallback;
            return this;
        }

        public changeEnchantments prompt() {

            Preconditions.checkNotNull(p, "player null");
            Preconditions.checkNotNull(item, "item null");

            if (e == null) return new changeEnchantments(p, item, accept, fallback);
            else return new changeEnchantments(p, item, e, accept, fallback);
        }
    }
}
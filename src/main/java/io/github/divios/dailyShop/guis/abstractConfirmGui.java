package io.github.divios.dailyShop.guis;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.Events;
import io.github.divios.core_lib.Schedulers;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import jdk.jpackage.internal.Log;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class abstractConfirmGui {

    protected static final DailyShop main = DailyShop.getInstance();

    protected static final Map<UUID, cacheEntry> sellCache = new ConcurrentHashMap<>();
    protected static final Map<UUID, cacheEntry> buyCache = new ConcurrentHashMap<>();

    static {
        Events.subscribe(PlayerDeathEvent.class)
                .handler(e -> {

                    List<ItemStack> items = e.getDrops();
                    for (ItemStack item : items) {
                        if (item == null) continue;
                        NBTItem nbtItem = new NBTItem(item);
                        if (nbtItem.hasKey("rds_temp_item")) item.setAmount(0);
                    }
                    cacheEntry entryb = buyCache.get(e.getEntity().getUniqueId());
                    if (entryb != null) {
                        int quantity = entryb.getQuantity();
                        while (quantity > 64) {
                            e.getDrops().add(ItemBuilder.of(entryb.getItem()).setCount(64));
                            quantity -= 64;
                        }
                        e.getDrops().add(ItemBuilder.of(entryb.getItem()).setCount(quantity));
                        buyCache.remove(e.getEntity().getUniqueId());
                    }

                    cacheEntry entry = sellCache.get(e.getEntity().getUniqueId());
                    if (entry != null) {
                        int quantity = entry.getQuantity();
                        while (quantity > 64) {
                            e.getDrops().add(ItemBuilder.of(entry.getItem()).setCount(64));
                            quantity -= 64;
                        }
                        e.getDrops().add(ItemBuilder.of(entry.getItem()).setCount(quantity));
                        sellCache.remove(e.getEntity().getUniqueId());
                    }

                });

        Events.subscribe(PlayerDropItemEvent.class)
                .handler(e -> {
                    NBTItem nbtItem = new NBTItem(e.getItemDrop().getItemStack());
                    if (nbtItem.hasKey("rds_temp_item")) e.getItemDrop().remove();
                });

        Events.subscribe(PlayerJoinEvent.class)
                .handler(e -> {
                    for (ItemStack item : e.getPlayer().getInventory().getContents()) {
                        if (item == null) continue;
                        if (new NBTItem(item).hasKey("rds_temp_item")) item.setAmount(0);
                    }
                    cacheEntry entryb = buyCache.get(e.getPlayer().getUniqueId());
                    if (entryb != null) {
                        entryb.restore(e.getPlayer());
                        buyCache.remove(e.getPlayer().getUniqueId());
                    }

                    cacheEntry entry = sellCache.get(e.getPlayer().getUniqueId());
                    if (entry != null) {
                        entry.restore(e.getPlayer());
                        sellCache.remove(e.getPlayer().getUniqueId());
                    }

                });
    }

    protected int added = 0;

    protected final ItemStack add1 = ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS_PANE)
            .setName(main.configM.getLangYml().CONFIRM_GUI_ADD_PANE + " 1");
    protected final ItemStack add5 = ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS_PANE)
            .setName(main.configM.getLangYml().CONFIRM_GUI_ADD_PANE + " 10").setCount(10);
    protected final ItemStack add10 = ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS_PANE)
            .setName(main.configM.getLangYml().CONFIRM_GUI_ADD_PANE + " 64").setCount(64);

    protected final ItemStack set64 = ItemBuilder.of(XMaterial.BLUE_STAINED_GLASS_PANE)
            .setName(main.configM.getLangYml().CONFIRM_GUI_ADD_PANE + " 64");

    protected final ItemStack rem1 = ItemBuilder.of(XMaterial.RED_STAINED_GLASS_PANE)
            .setName(main.configM.getLangYml().CONFIRM_GUI_REMOVE_PANE + " 1");
    protected final ItemStack rem5 = ItemBuilder.of(XMaterial.RED_STAINED_GLASS_PANE)
            .setName(main.configM.getLangYml().CONFIRM_GUI_REMOVE_PANE + " 10").setCount(10);
    protected final ItemStack rem10 = ItemBuilder.of(XMaterial.RED_STAINED_GLASS_PANE)
            .setName(main.configM.getLangYml().CONFIRM_GUI_REMOVE_PANE + " 64").setCount(64);

    protected final ItemStack set1 = ItemBuilder.of(XMaterial.BLUE_STAINED_GLASS_PANE)
            .setName(main.configM.getLangYml().CONFIRM_GUI_REMOVE_PANE + " 64");

    protected final ItemStack setPane = ItemBuilder.of(XMaterial.YELLOW_STAINED_GLASS)
            .setName(main.configM.getLangYml().CONFIRM_GUI_SET_PANE);

    protected static final int MAX_AMOUNT = 64 * 9 * 4;

    protected final BiConsumer<ItemStack, Integer> c;
    protected final Consumer<Player> b;

    protected final dShop shop;
    protected final Player p;
    protected final dItem dItem;
    protected final ItemStack item;
    protected final dShop.dShopT type;

    protected final String title;
    protected final String confirmLore;
    protected final String backLore;

    protected InventoryGUI gui;

    protected int stock;
    protected int maxPriceAmount;

    protected abstractConfirmGui(
            dShop shop,
            Player p,
            BiConsumer<ItemStack, Integer> accept,
            Consumer<Player> back,
            dItem item,
            dShop.dShopT type,
            String title,
            String acceptLore,
            String backLore
    ) {
        this.shop = shop;
        this.p = p;
        this.c = accept;
        this.b = back;
        this.title = title;
        this.dItem = item;
        this.item = item.getRawItem().clone();
        this.type = type;
        this.confirmLore = acceptLore;
        this.backLore = backLore;

        init();
        gui.open(p);

    }

    private void init() {

        gui = new InventoryGUI(main, 54, title);

        gui.setDestroyOnClose(true);
        gui.setOnDestroy(() -> Schedulers.sync().runLater(this::removeAllItems, 2L));

        gui.getInventory().setItem(22, item);

        gui.addButton(ItemButton.create(add1, e -> {
            if (ItemUtils.isEmpty(e.getCurrentItem())) return;
            addItem(1);
            update();
        }), 24);

        gui.addButton(ItemButton.create(add5, e -> {
            if (ItemUtils.isEmpty(e.getCurrentItem())) return;
            addItem(10);
            update();
        }), 25);

        gui.addButton(ItemButton.create(add10, e -> {
            if (ItemUtils.isEmpty(e.getCurrentItem())) return;
            addItem(64);
            update();
        }), 26);

        gui.addButton(ItemButton.create(rem1, e -> {
            if (ItemUtils.isEmpty(e.getCurrentItem())) return;
            remItem(1);
            update();
        }), 20);

        gui.addButton(ItemButton.create(rem10, e -> {
            if (ItemUtils.isEmpty(e.getCurrentItem())) return;
            remItem(10);
            update();
        }), 19);

        gui.addButton(ItemButton.create(rem10, e -> {
            if (ItemUtils.isEmpty(e.getCurrentItem())) return;
            remItem(64);
            update();
        }), 18);

        gui.addButton(ItemButton.create(ItemBuilder.of(XMaterial.PAPER)
                        .setName(main.configM.getLangYml().CONFIRM_GUI_STATS_NAME)
                        .setLore(main.configM.getLangYml().CONFIRM_GUI_STATS_LORE.stream()
                                .map(s -> {
                                    String s1 = s
                                            .replaceAll("\\{economy}", PriceWrapper.format(dItem.getEconomy().getBalance(p)))
                                            .replaceAll("\\{economy_name}", dItem.getEconomy().getName());
                                    if (!utils.isOperative("PlaceholderAPI")) return s1;
                                   try { return PlaceholderAPI.setPlaceholders(p, s1);
                                    } catch (Exception e) { return s1; }
                                }).collect(Collectors.toList()))
                , e -> {
                }), 45);

        gui.addButton(ItemButton.create(setPane, e -> {
            addMaximum();
            update();
        }), 40);

        gui.addButton(41, ItemButton.create(ItemBuilder.of(XMaterial.RED_STAINED_GLASS)
                        .setName(backLore).setLore(main.configM.getLangYml().CONFIRM_GUI_RETURN_PANE_LORE)
                ,
                e -> {
                    removeAllItems();
                    b.accept((Player) e.getWhoClicked());
                }));

        gui.addButton(39, ItemButton.create(ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS)
                        .setName(confirmLore)
                        .addLore(Msg.singletonMsg(main.configM.getLangYml().CONFIRM_GUI_BUY_NAME).add("\\{price}",
                                String.valueOf(type.equals(dShop.dShopT.buy) ?
                                        dItem.getBuyPrice().get().getPrice() :
                                        dItem.getSellPrice().get().getPrice())).build()),
                e -> {
                    if (added == 0) {
                        b.accept((Player) e.getWhoClicked());
                        return;
                    }
                    removeAllItems();
                    c.accept(item, added);
                }));

        update();

    }

    protected abstract void update();

    protected abstract void addItem(int amount);

    protected abstract void remItem(int amount);

    protected abstract void addMaximum();

    protected abstract void removeAllItems();



    static final class cacheEntry {

        private final ItemStack item;
        private final int quantity;

        public cacheEntry(ItemStack item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }

        public void restore(Player p) {
            ItemUtils.give(p, item, quantity);
        }
    }

}

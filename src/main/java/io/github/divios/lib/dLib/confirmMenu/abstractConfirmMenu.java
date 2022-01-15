package io.github.divios.lib.dLib.confirmMenu;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XMaterial;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.dailyShop.utils.NMSUtils.NMSClass;
import io.github.divios.dailyShop.utils.NMSUtils.NMSHelper;
import io.github.divios.dailyShop.utils.NMSUtils.NMSObject;
import io.github.divios.dailyShop.utils.PrettyPrice;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


public abstract class abstractConfirmMenu {

    protected static final DailyShop plugin = DailyShop.get();

    protected static final int MAX_INVENTORY_ITEMS = 9 * 4 * 64;

    protected final dShop shop;
    protected final Player player;
    protected final dItem item;
    protected final Consumer<Integer> onCompleteAction;
    protected final Runnable fallback;
    protected boolean confirmButton = false;

    protected int nAddedItems = 1;
    protected InventoryGUI menu;

    protected final Inventory clonedPlayerInventory;
    private final List<Subscription> listeners;
    private final Task giveItemsLoop;

    public abstractConfirmMenu(dShop shop,
                               Player player,
                               dItem item,
                               Consumer<Integer> onCompleteAction, Runnable fallback
    ) {
        this.shop = shop;
        this.player = player;
        this.item = item.clone();
        this.onCompleteAction = onCompleteAction;
        this.fallback = fallback;

        this.clonedPlayerInventory = Bukkit.createInventory(null, 36, "");
        this.listeners = new ArrayList<>();
        this.giveItemsLoop = Schedulers.async().runRepeating(this::update, 20L, 20L);

        update();
        createMenu();
        openMenu();

        listeners.add(
                Events.subscribe(InventoryCloseEvent.class)
                        .biHandler((o, e) -> {
                            if (e.getPlayer().getUniqueId().equals(player.getUniqueId())
                                    && menu.getInventory().equals(e.getInventory())) {
                                DebugLog.info("Inventory close event inside buyconfirmMenu");
                                listeners.forEach(Subscription::unregister);
                                giveItemsLoop.stop();
                                Schedulers.sync().runLater(player::updateInventory, 2L);
                            }
                        })
        );

        listeners.add(
                Events.subscribe(InventoryClickEvent.class)
                        .filter(e -> e.getWhoClicked().getUniqueId().equals(player.getUniqueId())
                                && e.getInventory().equals(menu.getInventory()))
                        .handler(e -> update())
        );
    }

    private void createMenu() {
        menu = new InventoryGUI(plugin, 54, getTitle());
        menu.setDestroyOnClose(true);
        updateButtons();
    }

    private void openMenu() {
        menu.open(player);
    }

    protected abstract String getTitle();

    protected void updateButtons() {
        menu.clear();       // Clears all items and buttons
        createButtons();
    }

    private void createButtons() {
        createAddButtons();
        createDeleteButtons();
        createMiscButtons();
    }

    private void createAddButtons() {
        if (addConditions(1)) createAddButton(1, 24);
        if (addConditions(10)) createAddButton(10, 25);
        if (addConditions(64)) createAddButton(64, 26);
    }

    private void createDeleteButtons() {
        if (removeConditions(1)) createRemoveButton(1, 20);
        if (removeConditions(10)) createRemoveButton(10, 19);
        if (removeConditions(64)) createRemoveButton(64, 18);
    }

    private void createMiscButtons() {
        createConfirmButton();
        createFallbackButton();
        createSetMaxButton();
        createItemDisplayButton();
        createStatsButton();
    }

    protected abstract boolean addConditions(int quantity);

    protected abstract boolean removeConditions(int quantity);

    private void createAddButton(int quantity, int slot) {
        menu.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS_PANE)
                                .setName(Lang.CONFIRM_GUI_ADD_PANE.getAsString(player) + " " + quantity)
                                .setCount(quantity)
                        , e -> {
                            if (ItemUtils.isEmpty(e.getCurrentItem())) return;
                            nAddedItems += quantity;
                            update();
                            updateButtons();
                        }), slot);
    }

    private void createRemoveButton(int quantity, int slot) {
        menu.addButton(
                ItemButton.create(
                        ItemBuilder.of(XMaterial.RED_STAINED_GLASS_PANE)
                                .setName(Lang.CONFIRM_GUI_REMOVE_PANE.getAsString(player) + " " + quantity)
                                .setCount(quantity)
                        , e -> {
                            if (ItemUtils.isEmpty(e.getCurrentItem())) return;
                            nAddedItems -= quantity;
                            update();
                            updateButtons();
                        }), slot);
    }

    private void createConfirmButton() {
        menu.addButton(39, ItemButton.create(ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS)
                        .setName(getConfirmName())
                        .addLore(getConfirmLore())
                , e -> {
                    if (nAddedItems == 0) {
                        fallback.run();
                        return;
                    }
                    confirmButton = true;
                    onCompleteAction.accept(nAddedItems);
                }));
    }

    private void createFallbackButton() {
        menu.addButton(41, ItemButton.create(
                ItemBuilder.of(XMaterial.RED_STAINED_GLASS)
                        .setName(getBackName())
                        .setLore(Lang.CONFIRM_GUI_RETURN_PANE_LORE.getAsListString(player))
                ,
                e -> fallback.run()));
    }

    private void createSetMaxButton() {
        menu.addButton(ItemButton.create(
                ItemBuilder.of(XMaterial.YELLOW_STAINED_GLASS)
                        .setName(Lang.CONFIRM_GUI_SET_PANE.getAsString(player))
                , e -> {
                    nAddedItems += setMaxItems();
                    update();
                    updateButtons();
                }), 40);
    }

    private void createItemDisplayButton() {
        menu.addButton(ItemButton.create(
                item.getItem()
                , e -> {
                }), 22);
    }

    private void createStatsButton() {
        menu.addButton(ItemButton.create(ItemBuilder.of(XMaterial.PAPER)
                        .setName(Lang.CONFIRM_GUI_STATS_NAME.getAsString(player))
                        .setLore(Lang.CONFIRM_GUI_STATS_LORE
                                .getAsListString(player,
                                        Template.of("economy", Utils.round(item.getEcon().getBalance(player), 2)),
                                        Template.of("economy_name", item.getEcon().getName())
                                )
                        )
                , e -> {
                }), 45);
    }

    private void update() {
        mirrorPlayerInventory();
        updateMockInventory();
        sendPackets();
    }

    private void mirrorPlayerInventory() {
        clonedPlayerInventory.setContents(Arrays.copyOf(player.getInventory().getContents(), 36));
    }

    protected abstract void updateMockInventory();

    private void sendPackets() {
        Schedulers.sync().run(() -> {           // Needs a delay, if not, update between server and client removes items
            for (int slot = 0; slot < 36; slot++) {
                SetSlotPacket.constructAndSend(player, clonedPlayerInventory.getItem(slot), slot);
            }
        });
    }

    protected abstract String getConfirmName();

    private List<String> getConfirmLore() {
        return setItemPricePlaceholder(Lang.CONFIRM_GUI_SELL_ITEM.getAsListString(player));
    }

    protected abstract String getBackName();

    protected abstract int setMaxItems();

    private List<String> setItemPricePlaceholder(List<String> str) {
        return Utils.JTEXT_PARSER
                .withTemplate(
                        Template.of("price", getFormattedPrice(getItemPrice() * nAddedItems) + " " + item.getEcon().getName()),
                        Template.of("quantity", nAddedItems)
                )
                .parse(str, player);
    }

    private String getFormattedPrice(double value) {
        return PrettyPrice.pretty(value);
    }

    protected abstract double getItemPrice();

    protected static class SetSlotPacket {

        protected static final NMSClass packetClazz;
        protected static final NMSClass CraftItemClazz;
        protected static final NMSClass NMSItemStackClazz;

        protected static final Constructor<?> packetConstructorPost_1_17;
        protected static final Constructor<?> packetConstructorPre_1_17;

        static {
            packetClazz = NMSHelper.getClass(ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutSetSlot").getName());
            CraftItemClazz = NMSHelper.getClass(ReflectionUtils.CRAFTBUKKIT + "inventory.CraftItemStack");
            NMSItemStackClazz = ReflectionUtils.VER >= 17
                    ? NMSHelper.getClass("net.minecraft.world.item.ItemStack")
                    : NMSHelper.getClass(ReflectionUtils.getNMSClass("ItemStack").getName());

            packetConstructorPost_1_17 = NMSHelper.getConstructor(packetClazz.getWrappedClass(), int.class, int.class, int.class, NMSItemStackClazz.getWrappedClass());
            packetConstructorPre_1_17 = NMSHelper.getConstructor(packetClazz.getWrappedClass(), int.class, int.class, NMSItemStackClazz.getWrappedClass());
        }

        protected static void constructAndSend(@NotNull Player p, @Nullable ItemStack item, int slot) {
            try {
                if (item == null) item = new ItemStack(Material.AIR);
                NMSObject craftItem = CraftItemClazz.callStaticMethod("asNMSCopy", item);

                Object packetClass = ReflectionUtils.VER >= 17
                        ? packetConstructorPost_1_17.newInstance(-2, 1, slot, craftItem.getObject())
                        : packetConstructorPre_1_17.newInstance(-2, slot, craftItem.getObject());
                ReflectionUtils.sendPacket(p, packetClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

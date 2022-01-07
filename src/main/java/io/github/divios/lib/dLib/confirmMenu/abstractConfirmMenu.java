package io.github.divios.lib.dLib.confirmMenu;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.inventory.InventoryGUI;
import io.github.divios.core_lib.inventory.ItemButton;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.PriceWrapper;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;


public abstract class abstractConfirmMenu {

    protected static final DailyShop plugin = DailyShop.get();

    protected static final String MARK_KEY = "rds_temp_item";
    protected static final int MAX_INVENTORY_ITEMS = 9 * 4 * 64;

    protected final dShop shop;
    protected final Player player;
    protected final dItem item;
    protected final Consumer<Integer> onCompleteAction;
    protected final Runnable fallback;

    protected int nAddedItems = 0;
    protected InventoryGUI menu;

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

        addItemsAndIncrement(item.getItem().getAmount());
        createMenu();
        openMenu();
    }

    private void createMenu() {
        menu = new InventoryGUI(plugin, 54, getTitle());
        setActionOnDestroy();
        updateButtons();
    }

    private void openMenu() {
        menu.open(player);
    }

    private void setActionOnDestroy() {
        menu.setDestroyOnClose(true);
        menu.setOnDestroy(() -> Schedulers.sync().runLater(this::removeAddedItems, 2L));
    }

    protected abstract String getTitle();

    protected void updateButtons() {
        menu.clear();       // Clears all items and buttons
        createButtons();
    }

    protected abstract void removeAddedItems();

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
                            addItemsAndIncrement(quantity);
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
                            removeItemsAndDecrement(quantity);
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
                    removeAddedItems();
                    onCompleteAction.accept(nAddedItems);
                }));
    }

    private void createFallbackButton() {
        menu.addButton(41, ItemButton.create(
                ItemBuilder.of(XMaterial.RED_STAINED_GLASS)
                        .setName(getBackName())
                        .setLore(Lang.CONFIRM_GUI_RETURN_PANE_LORE.getAsListString(player))
                ,
                e -> {
                    removeAddedItems();
                    fallback.run();
                }));
    }

    private void createSetMaxButton() {
        menu.addButton(ItemButton.create(
                ItemBuilder.of(XMaterial.YELLOW_STAINED_GLASS)
                        .setName(Lang.CONFIRM_GUI_SET_PANE.getAsString(player))
                , e -> {
                    setMaxItems();
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

    private void addItemsAndIncrement(int quantity) {
        nAddedItems += quantity;
        addItems(quantity);
    }

    protected abstract void addItems(int quantity);

    private void removeItemsAndDecrement(int quantity) {
        nAddedItems -= quantity;
        removeItems(quantity);
    }

    protected abstract void removeItems(int quantity);

    protected abstract String getConfirmName();

    private List<String> getConfirmLore() {
        return setItemPricePlaceholder(Lang.CONFIRM_GUI_SELL_ITEM.getAsListString(player));
    }

    protected abstract String getBackName();

    protected abstract void setMaxItems();

    private List<String> setItemPricePlaceholder(List<String> str) {
        return Utils.JTEXT_PARSER
                .withTemplate(
                        Template.of("price", getFormattedPrice(getItemPrice() * nAddedItems) + " " + item.getEcon().getName()),
                        Template.of("quantity", nAddedItems)
                )
                .parse(str, player);
    }

    private String getFormattedPrice(double value) {
        return PriceWrapper.format(value);
    }

    protected abstract double getItemPrice();

    protected static boolean isMarkedItem(ItemStack item) {
        return new NBTItem(item).hasKey(MARK_KEY);
    }

    protected ItemStack getMarkedItem() {
        NBTItem markedItem = new NBTItem(item.getItem());
        markedItem.setBoolean(MARK_KEY, true);
        ItemStack toReturn = markedItem.getItem();
        toReturn.setAmount(1);
        return toReturn;
    }

    protected static void deleteItem(ItemStack item) {
        item.setAmount(0);
    }

}

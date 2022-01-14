package io.github.divios.lib.dLib.confirmMenu;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.events.Subscription;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.files.Lang;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class BuyConfirmPacketsMenu extends abstractConfirmMenu {

    public static buyConfirmMenuBuilder builder() {
        return new buyConfirmMenuBuilder();
    }

    public static BuyConfirmPacketsMenu create(dShop shop, Player player, dItem item, Consumer<Integer> onCompleteAction, Runnable fallback) {
        return new BuyConfirmPacketsMenu(shop, player, item, onCompleteAction, fallback);
    }

    private Inventory clonedPlayerInventory;
    private final List<Subscription> listeners;

    public BuyConfirmPacketsMenu(dShop shop, Player player, dItem item, Consumer<Integer> onCompleteAction, Runnable fallback) {
        super(shop, player, item, onCompleteAction, fallback);
        this.listeners = new ArrayList<>();

        listeners.add(
                Events.subscribe(InventoryCloseEvent.class)
                        .biHandler((o, e) -> {
                            if (e.getPlayer().getUniqueId().equals(player.getUniqueId())
                                    && super.menu.getInventory().equals(e.getInventory())) {
                                DebugLog.info("Inventory close event inside buyconfirmMenu");
                                player.updateInventory();   // Removes the "ghost items"
                                listeners.forEach(Subscription::unregister);
                            }
                        })
        );

        listeners.add(
                Events.subscribe(InventoryClickEvent.class)
                        .filter(e -> e.getWhoClicked().getUniqueId().equals(player.getUniqueId())
                                && e.getInventory().equals(super.menu.getInventory()))
                        .handler(e -> {
                            if (e.getSlot() != e.getRawSlot() || e.getSlot() == -999)
                                addMockedItems();
                        })
        );
    }

    @Override
    protected String getTitle() {
        return Lang.CONFIRM_GUI_BUY_NAME.getAsString(player);
    }

    @Override
    protected void removeAddedItems() {
        player.updateInventory();
    }

    @Override
    protected boolean addConditions(int quantity) {
        return quantity <= getMinLimit();
    }

    @Override
    protected boolean removeConditions(int quantity) {
        return nAddedItems - 1 >= quantity;
    }

    @Override
    protected void addItems(int quantity) {
        if (clonedPlayerInventory == null) this.clonedPlayerInventory = Bukkit.createInventory(null, 36, "");

        clonedPlayerInventory.setContents(Arrays.copyOf(player.getInventory().getContents(), 36));

        int amount = super.nAddedItems;
        int ceil = (int) Math.ceil(amount / 64F);
        DebugLog.info("Total Amount: " + amount);
        for (int i = 0; i <= ceil; i++) {
            DebugLog.info("Itit: " + i);
            int aux = Math.min(amount, 64);
            DebugLog.info("Aux: " + aux);
            clonedPlayerInventory.addItem(ItemUtils.setAmount(item.getItem(), aux));
            amount -= aux;
        }
        addMockedItems();
    }

    @Override
    protected void removeItems(int quantity) {
        addItems(0);    // is the same as calling addItems with packets
    }

    private void addMockedItems() {
        Schedulers.sync().run(() -> {           // Needs a delay, if not, update between server and client removes items
            for (int slot = 0; slot < 36; slot++) {
                PacketContainer fakeItem = new PacketContainer(PacketType.Play.Server.SET_SLOT);
                fakeItem.getIntegers()
                        .write(0, -2)
                        .write(1, 0)
                        .write(2, slot);
                fakeItem.getItemModifier()
                        .write(0, clonedPlayerInventory.getItem(slot));

                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, fakeItem);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                /*ReflectionUtils.sendPacket(player,
                        new PacketPlayOutSetSlot(0, 0, 36 + slot, CraftItemStack.asNMSCopy(clonedPlayerInventory.getItem(slot)))
                );*/
            }
        });
    }

    @Override
    protected String getConfirmName() {
        return Lang.CONFIRM_GUI_YES.getAsString(player);
    }

    @Override
    protected String getBackName() {
        return Lang.CONFIRM_GUI_NO.getAsString(player);
    }

    @Override
    protected void setMaxItems() {
        int limit = getMinLimit();
        if (limit == 0) return;
        super.nAddedItems += limit;
        addItems(0);
    }

    @Override
    protected double getItemPrice() {
        return item.getPlayerBuyPrice(player, shop) / item.getItem().getAmount();
    }

    private int getMinLimit() {
        int stockLimit = getStockLimit();
        int balanceLimit = getBalanceLimit();
        int inventoryLimit = getPlayerInventoryLimit();

        return getMinimumValue(stockLimit, balanceLimit, inventoryLimit);
    }

    private int getMinimumValue(int... values) {
        int minValue = MAX_INVENTORY_ITEMS;

        for (int value : values)
            minValue = Math.min(minValue, value);

        return minValue;
    }

    private int getStockLimit() {
        return ((item.getDStock() != null) ? getItemStock() : MAX_INVENTORY_ITEMS) - nAddedItems;
    }

    private int getBalanceLimit() {
        return (int) Math.floor(item.getEcon().getBalance(player) / getItemPrice()) - nAddedItems;
    }

    private int getPlayerInventoryLimit() {
        int limit = 0;
        Inventory playerMockInventory = Bukkit.createInventory(null, 36);
        for (int i = 0; i < 36; i++)
            playerMockInventory.setItem(i, player.getInventory().getItem(i));

        while (playerMockInventory.addItem(getMarkedItem()).isEmpty()) limit++;

        return limit;
    }

    private int getItemStock() {
        return item.getPlayerStock(player);
    }

    public static final class buyConfirmMenuBuilder {
        private dShop shop;
        private Player player;
        private dItem item;
        private Consumer<Integer> onCompleteAction;
        private Runnable fallback;

        private buyConfirmMenuBuilder() {
        }

        public buyConfirmMenuBuilder withShop(dShop shop) {
            this.shop = shop;
            return this;
        }

        public buyConfirmMenuBuilder withPlayer(Player player) {
            this.player = player;
            return this;
        }

        public buyConfirmMenuBuilder withItem(dItem item) {
            this.item = item;
            return this;
        }

        public buyConfirmMenuBuilder withOnCompleteAction(Consumer<Integer> onCompleteAction) {
            this.onCompleteAction = onCompleteAction;
            return this;
        }

        public buyConfirmMenuBuilder withFallback(Runnable fallback) {
            this.fallback = fallback;
            return this;
        }

        public BuyConfirmPacketsMenu prompt() {
            return new BuyConfirmPacketsMenu(shop, player, item, onCompleteAction, fallback);
        }
    }

}

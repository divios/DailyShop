package io.github.divios.lib.dLib.shop;

import com.google.gson.JsonElement;
import io.github.divios.dailyShop.events.checkoutEvent;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.view.ShopView;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;

public interface dShop {

    static dShopImp create(String name) {
        return new dShopImp(name);
    }

    static dShopImp create(String name, int timer) {
        return new dShopImp(name, timer);
    }

    static dShopImp create(String name, int timer, LocalDateTime timestamp) {
        return new dShopImp(name, timer, timestamp, new ArrayList<>());
    }

    static dShopImp create(String name, int timer, LocalDateTime timestamp, Collection<dItem> items) {
        return new dShopImp(name, timer, timestamp, items);
    }

    static dShopImp create(String name, JsonElement gui, LocalDateTime timestamp, int timer) {
        return new dShopImp(name, gui, timestamp, timer, new HashSet<>());
    }

    static dShopImp create(String name, JsonElement gui, LocalDateTime timestamp, int timer, Set<dItem> items) {
        return new dShopImp(name, gui, timestamp, timer, items);
    }

    void openShop(@NotNull Player p);

    /**
     * Opens the gui to manage the items of this shop
     */
    void manageItems(Player p);

    /**
     * Opens the gui to customize the display of this shop
     */
    void openCustomizeGui(Player p);

    /**
     * Gets the name of the shop
     */
    String getName();

    /**
     * Sets the name of the shop
     */
    void rename(String name);

    /**
     * Returns the amount of items in this shop
     */

    int size();

    /**
     * Gets a copy the items in the shop
     *
     * @return returns a List of dItems. Note that this list is a copy of the original,
     * any change made to it won't affect the original one
     */
    @NotNull
    Set<dItem> getItems();

    /**
     * Returns an unmodifiable view of the items map
     */
    @NotNull
    Map<String, dItem> getMapItems();

    @NotNull
    Map<String, dItem> getCurrentItems();

    /**
     * Gets the item by ID
     *
     * @param ID the ID to search
     * @return null if it does not exist
     */
    @Nullable
    dItem getItem(@NotNull String ID);

    boolean hasItem(@NotNull String id);

    /**
     * Gets the dStock for a current daily item. Returns null if the shop does not
     * have that item on sale or the item has no stock defined.
     */
    dStock getStockForItem(String id);

    ShopAccount getAccount();

    LogCache getShopCache();

    /**
     * Restocks the items of this shop.
     */
    void reStock();

    /**
     * Updates the item of the shop
     */
    void updateItem(@NotNull dItem newItem);

    /**
     * Sets the items of this shop
     */
    default void setItems(@NotNull Collection<dItem> items) {
        DebugLog.info("Setting items");
        Map<String, dItem> newItems = new HashMap<>();
        items.forEach(dItem -> newItems.put(dItem.getID(), dItem));            // Cache values for a O(1) search

        for (Map.Entry<String, dItem> entry : new HashMap<>(getMapItems()).entrySet()) {          // Remove or update
            if (newItems.containsKey(entry.getKey())) {     // Update items if changed
                dItem toUpdateItem = newItems.remove(entry.getKey());

                if (toUpdateItem != null && !toUpdateItem.isSimilar(entry.getValue())) {
                    DebugLog.info("Updating item with ID: %s from dShop", toUpdateItem.getID());
                    updateItem(toUpdateItem);
                }
            } else {
                DebugLog.info("Removing item with ID: %s from dShop", entry.getValue().getID());
                removeItem(entry.getKey());
            }
        }

        newItems.values().forEach(newDItem -> {
            addItem(newDItem);             // Add newItems
            DebugLog.info("Added new item with ID: %s", newDItem.getID());
        });
    }

    /**
     * Adds an item to this shop
     *
     * @param item item to be added
     */
    void addItem(@NotNull dItem item);

    /**
     * Removes an item from the shop
     *
     * @param id of the item to be removed
     * @return true if the item was removed. False if not
     */
    boolean removeItem(String id);

    void computeBill(checkoutEvent e);

    /**
     * Return the dGui of this shop
     */
    ShopView getView();

    void setAccount(ShopAccount account);

    ShopOptions getOptions();

    ShopView getGui();

    void setOptions(ShopOptions options);

    void setTimestamp(LocalDateTime timestamp);

    LocalDateTime getTimestamp();

    int getTimer();

    boolean get_announce();

    void set_announce(boolean announce_restock);

    void setDefault(boolean aDefault);

    boolean isDefault();

    void setTimer(int timer);

    void destroy();

    default void setState(dShopState state) {
        if (!getName().equalsIgnoreCase(state.getName())) return;

        setTimer(state.getTimer());
        set_announce(state.isAnnounce());
        setDefault(state.isDefault());
        setAccount(state.getAccount());
        setOptions(state.getOptions());
        getGui().setState(state.getView());
        setItems(state.getItems());
    }

    dShopState toState();

}

package io.github.divios.lib.dLib;

import io.github.divios.lib.dLib.synchronizedGui.syncMenu;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface dShopI {

    void openShop(Player p);
    void manageItems(Player p);
    void openCustomizeGui(Player p);
    String getName();
    void rename(String name);
    Set<dItem> getItems();
    Optional<dItem> getItem(UUID uid);
    boolean hasItem(UUID uid);
    syncMenu getGuis();
    void setTimestamp(Timestamp timestamp);
    Timestamp getTimestamp();
    int getTimer();
    void setTimer(int timer);
    void addItem(dItem item);
    boolean removeItem(UUID uid);
    void updateItem(UUID uid, dItem newItem);
    void setItems(Set<dItem> newItem);
    void destroy();

}

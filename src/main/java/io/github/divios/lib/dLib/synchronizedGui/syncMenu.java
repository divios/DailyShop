package io.github.divios.lib.dLib.synchronizedGui;

import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.dInventory;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

/**
 * Interface declaring the contract of a syncMenu
 */

public interface syncMenu {

    void generate(Player p);

    default void reStock() { reStock(false); }

    void reStock(boolean silent);

    default singleGui getGui(Player p) { return getGui(p.getUniqueId()); }

    singleGui getGui(UUID key);

    default boolean contains(Player p) {
        return contains(p.getUniqueId());
    }

    boolean contains(UUID p);

    default int size() { return getMenus().size(); }

    Collection<singleGui> getMenus();

    void invalidate(UUID key);

    void invalidateAll();

    void destroy();

    void customizeGui(Player p);

    dInventory getDefault();

    dShop getShop();

    String toJson();

    int hashCode();

}

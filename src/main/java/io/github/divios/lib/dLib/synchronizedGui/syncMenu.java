package io.github.divios.lib.dLib.synchronizedGui;

import io.github.divios.lib.dLib.dInventory;
import io.github.divios.lib.dLib.dShop;
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

    singleGui get(UUID key);

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

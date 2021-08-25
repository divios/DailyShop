package io.github.divios.lib.dLib.synchronizedGui;

import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface syncMenu {

    void generate(Player p);

    void renovate();

    singleGui get(UUID key);

    int size();

    Collection<singleGui> getMenus();

    void invalidate(UUID key);

    void invalidateAll();

    void destroy();

    void customizeGui(Player p);

    dShop getShop();

    String toJson();

    int hashCode();

}

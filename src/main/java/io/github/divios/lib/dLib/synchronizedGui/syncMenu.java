package io.github.divios.lib.dLib.synchronizedGui;

import io.github.divios.dailyShop.events.updateItemEvent;
import io.github.divios.lib.dLib.dGui;
import io.github.divios.lib.dLib.dItem;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface syncMenu {

    void generate(Player p);

    void renovate();

    dGui get(UUID key);

    int size();

    Collection<dGui> getMenus();

    void invalidate(UUID key);

    void invalidateAll();

    void manageItems(Player p);

    void customizeGui(Player p);

    String toJson();

}

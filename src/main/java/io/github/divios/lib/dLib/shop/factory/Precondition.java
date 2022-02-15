package io.github.divios.lib.dLib.shop.factory;

import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

public interface Precondition {

    void validate(dShop shop, Player p, dItem item, int quantity);

}

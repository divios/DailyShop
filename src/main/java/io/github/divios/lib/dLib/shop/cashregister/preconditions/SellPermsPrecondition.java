package io.github.divios.lib.dLib.shop.cashregister.preconditions;

import io.github.divios.dailyShop.files.Messages;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.cashregister.exceptions.IllegalPrecondition;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

import java.util.List;

public class SellPermsPrecondition implements Precondition {

    @Override
    public void validate(dShop shop, Player p, dItem item, int quantity) {
        List<String> perms;
        if ((perms = item.getSellPerms()) != null && !perms.stream().allMatch(p::hasPermission))
            throw new IllegalPrecondition(Messages.MSG_NOT_PERMS);
    }

}

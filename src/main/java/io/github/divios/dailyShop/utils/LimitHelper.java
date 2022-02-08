package io.github.divios.dailyShop.utils;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.jcommands.util.Value;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.registry.util.Pair;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Locale;

public class LimitHelper {

    /**
     * Retrieves the player limit. More formally, returns the value that is more
     * limiting, either from shop or item limit.
     * <p>
     * Returns -1 if no limit was found.
     */
    public static int getPlayerLimit(Player p, dShop shop, dItem item, Transactions.Type type) {
        Pair<Integer, Integer> amounts = DailyShop.get().getRecordBook()
                .getTupleAmount(shop, p, type, item);

        //String a = "dailyrandomshop.limit.shop.sell.drops.15";
        //String b = "dailyrandomshop.limit.item.buy.DIRT.3";

        String shopPerm = "dailyRandomShop.limit.shop." +
                type.name().toLowerCase(Locale.ROOT) + "." +
                shop.getName() + ".";
        String itemPerm = "dailyRandomShop.limit.item." +
                type.name().toLowerCase(Locale.ROOT) + "." +
                item.getID() + ".";

        int shopLimit = getMinPermLimitRegistered(p, shopPerm);
        int itemLimit = getMinPermLimitRegistered(p, itemPerm);

        int finalShopLimit = Math.max(-1, (shopLimit - amounts.getLeft()));
        int finalItemLimit = Math.max(-1, (itemLimit - amounts.getRight()));

        if (finalShopLimit == -1 && finalItemLimit == -1)
            return -1;
        else if (finalShopLimit == -1 && finalItemLimit >= 0)
            return finalItemLimit;
        else if (finalItemLimit == -1 && finalShopLimit >= 0)
            return finalShopLimit;
        else return Math.min(finalShopLimit, finalItemLimit);
    }

    private static int getMinPermLimitRegistered(Player p, String perm) {
        return p.getEffectivePermissions().stream()
                .map(PermissionAttachmentInfo::getPermission)
                .filter(s -> s.toLowerCase().startsWith(perm.toLowerCase()))
                .map(s -> s.substring(perm.length()))
                .mapToInt(value -> Value.ofString(value).getAsIntOrDefault(Integer.MAX_VALUE))
                .min().orElse(-1);
    }


}

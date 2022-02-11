package io.github.divios.dailyShop.utils;

import io.github.divios.jcommands.util.Value;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.registry.util.Pair;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Locale;

public class LimitHelper {

    /**
     * Retrieves the player shop limit. Returns -1 if no limit;
     */
    public static int getShopLimit(Player p, dShop shop, Transactions.Type type) {
        int amount = shop.getShopCache().getTotalAmount(p, type);

        int shopLimit = getMinPermLimit(p, shop, type);
        if (shopLimit == -1) return -1;

        return Math.max(0, (shopLimit - amount));
    }

    /**
     * Retrieves the player item limit. Returns -1 if no limit;
     */
    public static int getItemLimit(Player p, dShop shop, String id, Transactions.Type type) {
        int amount = shop.getShopCache().getAmountForItem(p, id, type);

        int shopLimit = getMinPermLimit(p, shop, id, type);
        if (shopLimit == -1) return -1;

        return Math.max(0, (shopLimit - amount));
    }

    // String a = "dailyrandomshop.limit.shop.sell.drops.15";
    public static int getMinPermLimit(Player p, dShop shop, Transactions.Type type) {
        String perm = "dailyRandomShop.limit.shop." +
                type.name().toLowerCase(Locale.ROOT) + "." +
                shop.getName() + ".";

        return getMinPermLimitRegistered(p, perm);
    }

    // String b = "dailyrandomshop.limit.item.buy.drops.DIRT.3";
    public static int getMinPermLimit(Player p, dShop shop, String id, Transactions.Type type) {
        String perm = "dailyRandomShop.limit.item." +
                type.name().toLowerCase(Locale.ROOT) + "." +
                shop.getName().toLowerCase(Locale.ROOT) + "." +
                id + ".";

        return getMinPermLimitRegistered(p, perm);
    }

    /**
     * Retrieves the player limit. More formally, returns the value that is more
     * limiting, either from shop or item limit.
     * <p>
     * Returns -1 if no limit was found.
     */
    public static int getPlayerLimit(Player p, dShop shop, dItem item, Transactions.Type type) {
        Timer timer = Timer.create();
        Pair<Integer, Integer> amounts = shop.getShopCache().getAmountTuple(p.getUniqueId(), item, type);
        timer.stop();
        DebugLog.info("Time elapsed to search limit: " + timer.getTime() + " ms");

        int shopLimit = getMinPermLimit(p, shop, type);
        int itemLimit = getMinPermLimit(p, shop, item.getID(), type);

        DebugLog.info("shopLimit: " + shopLimit);
        DebugLog.info("itemLimit: " + itemLimit);

        int finalShopLimit = Math.max(0, (shopLimit - amounts.getLeft()));
        int finalItemLimit = Math.max(0, (itemLimit - amounts.getRight()));

        DebugLog.info("finalShopLimit: " + finalShopLimit);
        DebugLog.info("finalItemLimit: " + finalItemLimit);

        if (shopLimit == -1 && itemLimit == -1)
            return -1;
        else if (shopLimit == -1 && itemLimit >= 0)
            return finalItemLimit;
        else if (itemLimit == -1 && shopLimit >= 0)
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

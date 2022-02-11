package io.github.divios.dailyShop.hooks.util.papiresolvers;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.LimitHelper;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerLimitPlaceholder implements PlaceholderResolver {

    private final List<PlaceholderResolver> innerResolvers;

    public PlayerLimitPlaceholder() {
        innerResolvers = new ArrayList<>();

        innerResolvers.add(new ShopLimitResolver());
        innerResolvers.add(new ItemLimitResolver());
    }

    @Override
    public boolean canResolve(String rawPlaceholder) {
        return rawPlaceholder.toLowerCase().startsWith("limit_")
                || rawPlaceholder.toLowerCase().startsWith("maxlimit");
    }

    @Override
    public String resolve(Player p, String rawPlaceholder) {
        String placeholderStr = null;

        for (PlaceholderResolver innerResolver : innerResolvers) {
            if (innerResolver.canResolve(rawPlaceholder)) {
                placeholderStr = innerResolver.resolve(p, rawPlaceholder);
                break;
            }
        }

        return placeholderStr;
    }

    private static final class ShopLimitResolver implements PlaceholderResolver {

        private static final Pattern pattern = Pattern.compile("(limit|maxLimit)_(buy|sell)_(.*)");

        @Override
        public boolean canResolve(String rawPlaceholder) {
            return pattern.matcher(rawPlaceholder).find();
        }

        @Override
        public String resolve(Player p, String rawPlaceholder) {
            Matcher matcher = pattern.matcher(rawPlaceholder);
            if (!matcher.find())
                return null;

            String limitStr = matcher.group(1);
            Transactions.Type type = Transactions.Type.getByKey(matcher.group(2));
            dShop shop = DailyShop.get().getShopsManager().getShop(matcher.group(3)).orElse(null);

            if (shop == null) return null;

            if (limitStr.equalsIgnoreCase("limit"))
                return String.valueOf(LimitHelper.getShopLimit(p, shop, type));
            else if (limitStr.equalsIgnoreCase("maxlimit"))
                return String.valueOf(LimitHelper.getMinPermLimit(p, shop, type));

            return null;
        }

    }

    private static final class ItemLimitResolver implements PlaceholderResolver {

        private static final Pattern pattern = Pattern.compile("(limit|maxLimit)_(buy|sell)_(.*)_(.*)");

        @Override
        public boolean canResolve(String rawPlaceholder) {
            return pattern.matcher(rawPlaceholder).find();
        }

        @Override
        public String resolve(Player p, String rawPlaceholder) {
            Matcher matcher = pattern.matcher(rawPlaceholder);
            if (!matcher.find())
                return null;

            String limitStr = matcher.group(1);
            Transactions.Type type = Transactions.Type.getByKey(matcher.group(2));
            dShop shop = DailyShop.get().getShopsManager().getShop(matcher.group(3)).orElse(null);

            if (shop == null) return null;

            dItem item = shop.getItem(matcher.group(4));
            if (item == null) return null;

            if (limitStr.equalsIgnoreCase("limit"))
                return String.valueOf(LimitHelper.getShopLimit(p, shop, type));
            else if (limitStr.equalsIgnoreCase("maxlimit"))
                return String.valueOf(LimitHelper.getMinPermLimit(p, shop, item.getID(), type));

            return null;
        }

    }

}

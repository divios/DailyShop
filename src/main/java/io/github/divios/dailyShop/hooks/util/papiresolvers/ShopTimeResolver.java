package io.github.divios.dailyShop.hooks.util.papiresolvers;

import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopTimeResolver implements PlaceholderResolver {

    private static final Pattern pattern = Pattern.compile("time_(.*)");

    @Override
    public boolean canResolve(String rawPlaceholder) {
        return pattern.matcher(rawPlaceholder).find();
    }

    @Override
    public String resolve(Player p, String rawPlaceholder) {
        Matcher matcher = pattern.matcher(rawPlaceholder);
        if (!matcher.find())
            return null;

        Optional<dShop> shop = DailyShop.get().getShopsManager().getShop(matcher.group(1));
        if (shop.isPresent() && shop.get().getTimer() == -1)
            return XSymbols.TIMES_3.parseSymbol();

        return shop.map(Utils::getDiffActualTimer).orElse(null);
    }
}

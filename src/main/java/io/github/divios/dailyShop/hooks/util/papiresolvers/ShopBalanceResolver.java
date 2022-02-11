package io.github.divios.dailyShop.hooks.util.papiresolvers;

import io.github.divios.core_lib.misc.XSymbols;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.shop.dShop;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShopBalanceResolver implements PlaceholderResolver {

    private static final Pattern pattern = Pattern.compile("(balance|maxbalance)_(.*)");

    @Override
    public boolean canResolve(String rawPlaceholder) {
        return pattern.matcher(rawPlaceholder).find();
    }

    @Override
    public String resolve(Player p, String rawPlaceholder) {
        Matcher matcher = pattern.matcher(rawPlaceholder);
        if (!matcher.find())
            return null;

        Optional<dShop> shop = DailyShop.get().getShopsManager().getShop(matcher.group(2));
        if (!shop.isPresent()) return null;
        if (shop.get().getAccount() == null)
            return XSymbols.INFINITY.parseSymbol();

        double balance = shop.get().getAccount().getBalance();
        double maxBalance = shop.get().getAccount().getMaxBalance();

        if (balance == 0 || Double.compare(balance, maxBalance) == 0)
            return "&c" + balance;
        else
            return String.valueOf(balance);
    }
}

package io.github.divios.dailyShop.economies;

import java.util.function.Function;

public enum econTypes {

    vault(s -> new vault()),
    gemsEconomy(gemEcon::new),
    tokenEnchants(s -> new tokenEnchantsE()),
    tokenManager(s -> new tokenManagerE()),
    MPoints(MPointsE::new),
    playerPoints(playerPointsE::new),
    ultraEconomy(ultraEconomyE::new),
    item(itemEconomy::new),
    exp(expEconomy::new),
    elementalGems(ElementalGemsEcon::new);

    private final Function<String, economy> function;

    econTypes(Function<String, economy> function) {
        this.function = function;
    }

    public economy getEconomy(String currency) {
        return function.apply(currency);
    }

}

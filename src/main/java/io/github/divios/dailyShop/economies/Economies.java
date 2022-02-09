package io.github.divios.dailyShop.economies;

import java.util.function.Function;

public enum Economies {

    vault(s -> new vault()),
    gemsEconomy(gemEcon::new),
    tokenEnchants(s -> new tokenEnchantsE()),
    tokenManager(s -> new tokenManagerE()),
    MPoints(MPointsE::new),
    playerPoints(playerPointsE::new),
    ultraEconomy(ultraEconomyE::new),
    item(itemEconomy::new),
    exp(expEconomy::new),
    elementalGems(ElementalGemsEcon::new),
    gettoni(s -> new gettoniE());

    private final Function<String, Economy> function;

    Economies(Function<String, Economy> function) {
        this.function = function;
    }

    public Economy getEconomy() {
        return function.apply("");
    }

    public Economy getEconomy(String currency) {
        return function.apply(currency);
    }

}

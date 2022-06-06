package io.github.divios.dailyShop.economies;

import java.util.HashMap;
import java.util.Map;
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
    private final Map<String, Economy> cached = new HashMap<>();

    Economies(Function<String, Economy> function) {
        this.function = function;
    }

    public Economy getEconomy() {
        Economy aux;
        if ((aux = cached.get(null)) != null)
            return aux;

        aux = function.apply("");
        cached.put(null, aux);
        return aux;
    }

    public Economy getEconomy(String currency) {
        Economy aux;
        if ((aux = cached.get(currency)) != null)
            return aux;

        aux = function.apply(currency);
        cached.put(currency, aux);
        return aux;
    }

    public static void reload() {
        for (Economies value : values()) {
            value.cached.values().forEach(Economy::reload);
        }
    }

}

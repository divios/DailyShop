package io.github.divios.dailyShop.economies;

import java.util.function.Function;

public enum econTypes {

    vault(s -> new vault());

    private final Function<String, economy> function;

    econTypes (Function<String, economy> function) {
        this.function = function;
    }

    public economy getEconomy(String currency) {
        return function.apply(currency);
    }

}
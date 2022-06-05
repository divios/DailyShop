package io.github.divios.dailyShop.utils.valuegenerators.util;

import java.util.Optional;
import java.util.function.Function;

public enum Rounder {
    NONE(Function.identity()),
    CEIL(Math::ceil),
    FLOOR(Math::floor);

    private final Function<Double, Double> transformer;

    Rounder(Function<Double, Double> transformer) {
        this.transformer = transformer;
    }

    public double transform(Double aDouble) {
        return transformer.apply(aDouble);
    }

    public static Optional<Rounder> getByName(String name) {
        for (Rounder value : Rounder.values()) {
            if (value.name().equalsIgnoreCase(name))
                return Optional.of(value);
        }

        return Optional.empty();
    }

}

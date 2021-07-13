package io.github.divios.dailyShop.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Collections {

    public static <T> List<T> listOf(T value, int amount) {
        List<T> toReturn = new ArrayList<>();
        IntStream.range(0, amount).forEach(value1 -> {
            toReturn.add(value);
        });

        return toReturn;
    }

}

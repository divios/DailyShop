package io.github.divios.dailyShop.utils;

import io.github.divios.core_lib.utils.Log;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, wrappedResult<E>> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, wrappedResult.of(weight, result));
        return this;
    }

    public E next() {
        double value = random.nextDouble() * total;
        wrappedResult<E> result = map.remove(map.higherKey(value));
        if (result == null) return null;
        total -= result.getWeight();
        return result.getResult();
    }


    private static final class wrappedResult<E> {

        private final double weight;
        private final E result;

        private wrappedResult(double weight, E result) {
            this.weight = weight;
            this.result = result;
        }

        public static <E> wrappedResult<E> of(double weight, E result) {
            Log.severe(String.valueOf(weight));
            return new wrappedResult<>(weight, result);
        }

        public double getWeight() {
            return weight;
        }

        public E getResult() {
            return result;
        }
    }

}



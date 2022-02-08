package io.github.divios.lib.dLib.registry.util;

import java.util.Objects;

public class Pair<K, T> {

    public static <K, T> Pair<K, T> of (K key, T value) {
        return new Pair<>(key, value);
    }

    private final K key;
    private final T value;

    public Pair(K key, T value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public K getLeft() {
        return key;
    }

    public T getRight() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}

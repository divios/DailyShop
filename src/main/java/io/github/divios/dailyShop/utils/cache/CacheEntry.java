package io.github.divios.dailyShop.utils.cache;

public class CacheEntry<T, K> {

    private final T key;
    private final K value;

    public CacheEntry(T key, K value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public K getValue() {
        return value;
    }
}

package io.github.divios.dailyShop.utils.cache;

import java.util.Map;

public interface Cache<K, T> {

    T get(K key);

    void put(K key, T value);

    void putAll(Map<? extends K, ? extends T> m);

    T invalidate(K key);

    void invalidateAll();

    int size();

    void cleanUp();

    Map<K, T> asMap();

}

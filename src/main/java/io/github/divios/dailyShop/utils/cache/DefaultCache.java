package io.github.divios.dailyShop.utils.cache;

import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultCache<K,T> implements Cache<K, T> {

    private final HashMap<K, T> map;

    public DefaultCache() {
        this.map = new HashMap<>();
    }

    public T get(K key) {
        return map.get(key);
    }

    public void put(K key, T value) {
        map.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends T> m) {
        m.forEach(this::put);
    }

    public T invalidate(K key) {
        return map.remove(key);
    }

    public void invalidateAll() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public void cleanUp() {
        invalidateAll();
    }

    @Override
    public Map<K, T> asMap() {
        return Collections.unmodifiableMap(map);
    }
}

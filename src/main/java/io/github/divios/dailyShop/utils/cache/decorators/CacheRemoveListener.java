package io.github.divios.dailyShop.utils.cache.decorators;

import io.github.divios.dailyShop.utils.cache.Cache;
import io.github.divios.dailyShop.utils.cache.RemovalCause;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class CacheRemoveListener<K, T> implements Cache<K, T> {

    private final Cache<K, T> cache;
    private final Consumer<RemoveEvent<K,T>> removeListener;

    public CacheRemoveListener(Cache<K, T> cache, Consumer<RemoveEvent<K, T>> removeListener) {
        this.cache = cache;
        this.removeListener = removeListener;
    }

    @Override
    public T get(K key) {
        return cache.get(key);
    }

    @Override
    public void put(K key, T value) {
        T replaced;
        if ((replaced = cache.get(key)) != null)
            removeListener.accept(new RemoveEvent<>(RemovalCause.REPLACED, key, replaced));
        cache.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends T> m) {
        m.forEach(this::put);
    }

    @Override
    public T invalidate(K key) {
        T removed;
        if ((removed = cache.invalidate(key)) != null)
            removeListener.accept(new RemoveEvent<>(RemovalCause.REMOVED, key, removed));

        return removed;
    }

    @Override
    public void invalidateAll() {
        new HashSet<>(cache.asMap().keySet()).forEach(k ->
                removeListener.accept(
                        new RemoveEvent<>(RemovalCause.REMOVED, k, cache.invalidate(k))
                )
        );
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }

    @Override
    public Map<K, T> asMap() {
        return cache.asMap();
    }

    public static final class RemoveEvent<K, T> {

        private final RemovalCause cause;
        private final K key;
        private final T value;

        public RemoveEvent(RemovalCause cause, K key, T value) {
            this.cause = cause;
            this.key = key;
            this.value = value;
        }

        public RemovalCause getCause() {
            return cause;
        }

        public K getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }
    }

}

package io.github.divios.dailyShop.utils.cache;

import io.github.divios.dailyShop.utils.cache.decorators.CacheRemoveListener;
import io.github.divios.dailyShop.utils.cache.decorators.CacheRemoveTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CacheBuilder<K, T> {

    public static <K, T> CacheBuilder<K, T> create() {
        return new CacheBuilder<>();
    }

    private int time;
    private TimeUnit timeUnit;

    private Consumer<CacheRemoveListener.RemoveEvent<K, T>> removeListener;

    public CacheBuilder() {
    }

    public CacheBuilder<K, T> expireAfter(int time) {
        return expireAfter(time, TimeUnit.SECONDS);
    }

    public CacheBuilder<K, T> expireAfter(int time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;

        return this;
    }

    public CacheBuilder<K, T> removalListener(Consumer<CacheRemoveListener.RemoveEvent<K, T>> removeListener) {
        this.removeListener = removeListener;
        return this;
    }

    public Cache<K, T> build() {
        Cache<K, T> cache = new DefaultCache<>();

        if (removeListener != null)
            cache = new CacheRemoveListener<>(cache, removeListener);

        if (timeUnit != null)
            cache = new CacheRemoveTask<>(cache, time, timeUnit);

        return cache;
    }

}

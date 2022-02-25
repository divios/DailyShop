package io.github.divios.dailyShop.utils.cache.decorators;

import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.core_lib.scheduler.Task;
import io.github.divios.dailyShop.utils.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CacheRemoveTask<K, T> implements Cache<K, T> {

    private final Cache<K, T> cache;

    private int time;
    private final TimeUnit maxUnit;
    private final HashMap<K, TimeEntry> cacheTime;

    private Task task;

    public CacheRemoveTask(Cache<K, T> cache, int time, TimeUnit maxUnit) {
        this.cache = cache;

        this.time = time;
        this.maxUnit = maxUnit;
        this.cacheTime = new HashMap<>();

        startTask();
    }

    private void startTask() {
        task = Schedulers.sync().runRepeating(() -> {
            cacheTime.forEach((k, timeEntry) -> {
                if (timeEntry.hasExpired())
                    Schedulers.sync().run(() -> invalidate(k));
            });
        }, 1, TimeUnit.SECONDS, 1, TimeUnit.SECONDS);
    }

    @Override
    public T get(K key) {
        return cache.get(key);
    }

    @Override
    public void put(K key, T value) {
        cache.put(key, value);
        cacheTime.put(key, new TimeEntry(maxUnit.toMillis(time)));
    }

    @Override
    public void putAll(Map<? extends K, ? extends T> m) {
        m.forEach(this::put);
    }

    @Override
    public T invalidate(K key) {
        cacheTime.remove(key);
        return cache.invalidate(key);
    }

    @Override
    public void invalidateAll() {
        cacheTime.clear();
        cache.invalidateAll();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void cleanUp() {
        task.stop();
        cache.cleanUp();
    }

    @Override
    public Map<K, T> asMap() {
        return cache.asMap();
    }

    private static final class TimeEntry {

        private final long max;

        public TimeEntry(long max) {
            this.max = System.currentTimeMillis() + max;
        }

        public boolean hasExpired() {
            return System.currentTimeMillis() > max;
        }

    }

}

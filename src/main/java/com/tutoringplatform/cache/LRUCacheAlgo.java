package com.tutoringplatform.cache;

import java.util.LinkedHashMap;
import java.util.Map;


public class LRUCacheAlgo<K, V> implements CacheAlgo<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> cache;

    public LRUCacheAlgo(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;

        this.cache = new LinkedHashMap<K, V>(capacity + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUCacheAlgo.this.capacity;
            }
        };
    }

    @Override
    public synchronized void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value cannot be null");
        }
        cache.put(key, value);
    }

    @Override
    public synchronized V get(K key) {
        if (key == null) {
            return null;
        }
        return cache.get(key);
    }

    @Override
    public synchronized boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
        return cache.containsKey(key);
    }

    @Override
    public synchronized int size() {
        return cache.size();
    }

    public synchronized void clear() {
        cache.clear();
    }

    public synchronized V remove(K key) {
        if (key == null) {
            return null;
        }
        return cache.remove(key);
    }
}
package com.tutoringplatform.cache;

public interface CacheAlgo<K, V> {
    void put(K key, V value);
    V get(K key);
    boolean containsKey(K key);
    int size();
}
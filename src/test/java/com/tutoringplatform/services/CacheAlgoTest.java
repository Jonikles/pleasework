package com.tutoringplatform.services;

import com.tutoringplatform.cache.LRUCacheAlgo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CacheAlgoTest {

    private LRUCacheAlgo<String, String> cache;
    private static final int CACHE_CAPACITY = 3;

    @BeforeEach
    public void setUp() {
        cache = new LRUCacheAlgo<>(CACHE_CAPACITY);
    }

    @Test
    public void testBasicPutAndGet() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        assertEquals("value1", cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertNull(cache.get("key3"));
    }

    @Test
    public void testContainsKey() {
        cache.put("key1", "value1");

        assertTrue(cache.containsKey("key1"));
        assertFalse(cache.containsKey("key2"));
    }

    @Test
    public void testSize() {
        assertEquals(0, cache.size());

        cache.put("key1", "value1");
        assertEquals(1, cache.size());

        cache.put("key2", "value2");
        assertEquals(2, cache.size());
    }

    @Test
    public void testLRUEviction() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        assertEquals(CACHE_CAPACITY, cache.size());

        cache.get("key1");

        cache.put("key4", "value4");

        assertEquals(CACHE_CAPACITY, cache.size());
        assertNotNull(cache.get("key1"));
        assertNull(cache.get("key2"));
        assertNotNull(cache.get("key3"));
        assertNotNull(cache.get("key4"));
    }

    @Test
    public void testUpdateExistingKey() {
        cache.put("key1", "value1");
        cache.put("key1", "updatedValue1");

        assertEquals("updatedValue1", cache.get("key1"));
        assertEquals(1, cache.size());
    }

    @Test
    public void testNullKeyHandling() {
        assertThrows(IllegalArgumentException.class, () -> cache.put(null, "value"));
        assertThrows(IllegalArgumentException.class, () -> cache.put("key", null));

        assertNull(cache.get(null));
        assertFalse(cache.containsKey(null));
    }

    @Test
    public void testRemove() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        assertEquals("value1", cache.remove("key1"));
        assertNull(cache.get("key1"));
        assertEquals(1, cache.size());

        assertNull(cache.remove("key3"));
    }

    @Test
    public void testClear() {
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        cache.clear();

        assertEquals(0, cache.size());
        assertNull(cache.get("key1"));
        assertNull(cache.get("key2"));
    }

    @Test
    public void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new LRUCacheAlgo<>(0));
        assertThrows(IllegalArgumentException.class, () -> new LRUCacheAlgo<>(-1));
    }
}
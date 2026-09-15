package com.sahilkhurakte.redis.core;

import java.util.concurrent.ConcurrentHashMap;

public class Database {

    private final ConcurrentHashMap<String,RedisValue> map = new ConcurrentHashMap<>();

    public void set(String key, String value) {
        map.put(key, new RedisValue(value, null)); // null = no expiry
    }

    public void set(String key, String value, long ttlMillis) {
        long expiresAt = System.currentTimeMillis() + ttlMillis;
        map.put(key, new RedisValue(value, expiresAt));
    }

    public String get(String key) {
        RedisValue value = getLive(key);
        return value == null ? null : value.value();
    }

    public int del(String... keys) {
        int removed = 0;
        for (String key : keys) {
            if (getLive(key) != null) {
                map.remove(key);
                removed++;
            }
        }
        return removed;
    }

    public int exists(String... keys) {
        int count = 0;
        for (String key : keys) {
            if (getLive(key) != null) {
                count++;
            }
        }
        return count;
    }

    private RedisValue getLive(String key) {
        RedisValue value = map.get(key);
        if (value == null) {
            return null;
        }
        if(value.expiresAt() != null && value.expiresAt() <= System.currentTimeMillis()) {
            // Already expired
            map.remove(key);
            return null;
        }
        return value;
    }
}

package com.sahilkhurakte.redis.core;

import java.util.concurrent.ConcurrentHashMap;

public class Database {

    private final ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();

    public void set(String key, String value) {
        map.put(key, value);
    }

    public String get(String key) {
        if(!map.containsKey(key)) return null;
        return map.get(key);
    }

    public int del(String... keys) {
        int removed = 0;
        for (String key : keys) {
            if (map.remove(key) != null) {
                removed++;
            }
        }
        return removed;
    }

    public int exists(String... keys) {
        int count = 0;
        for (String key : keys) {
            if (map.containsKey(key)) {
                count++;
            }
        }
        return count;
    }
}

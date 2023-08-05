package com.portabull.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CacheData {

    private final Map<String, Object> data;

    public CacheData() {
        data = new HashMap<>();
    }

    public Map<String, Object> getData() {
        return data;
    }
}

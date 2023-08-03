package com.portabull.payloads;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

public class KeyValueMapping<K, V> {

    private K[] keys;

    private V[] values;

    public KeyValueMapping<K, V> setKeys(K... keys) {
        this.keys = keys;
        return this;
    }

    public KeyValueMapping<K, V> setValues(V... values) {
        this.values = values;
        return this;
    }

    public Map<K, V> getResult() {
        Map<K, V> map = new HashMap<>();
        if (keys.length >= values.length) {
            invokeResult(map, values.length);
        } else {
            invokeResult(map, keys.length);
        }
        return map;
    }

    public MultiValueMap<K, V> getResultAsMultiValueMap() {
        MultiValueMap<K, V> map = new LinkedMultiValueMap<>();
        if (keys.length >= values.length) {
            invokeResult(map, values.length);
        } else {
            invokeResult(map, keys.length);
        }
        return map;
    }

    public void invokeResult(Map<K, V> map, int size) {
        for (int index = 0; size > index; index++) {
            map.put(keys[index], values[index]);
        }
    }

    public void invokeResult(MultiValueMap<K, V> map, int size) {
        for (int index = 0; size > index; index++) {
            map.add(keys[index], values[index]);
        }
    }


}

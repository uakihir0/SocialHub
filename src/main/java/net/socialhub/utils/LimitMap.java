package net.socialhub.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LimitMap<K, V> extends LinkedHashMap<K, V> {

    private int limit;

    public LimitMap(int limit) {
        this.limit = limit;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return (size() > limit);
    }
}

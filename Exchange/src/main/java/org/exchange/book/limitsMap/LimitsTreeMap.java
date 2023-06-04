package org.exchange.book.limitsMap;

import java.util.*;

/**
 * Wrapper around TreeMap that implements LimitsMap interface
 *
 * @param <V>
 */
public class LimitsTreeMap<V> implements LimitsMap<V> {
    private final TreeMap<Float, V> treeMap;

    public LimitsTreeMap() {
        treeMap = new TreeMap<>();
    }

    public LimitsTreeMap(Comparator<Float> tComparator) {
        treeMap = new TreeMap<>(tComparator);
    }

    @Override
    public Map.Entry<Float, V> firstEntry() {
        return treeMap.firstEntry();
    }

    @Override
    public void put(Float key, V value) {
        treeMap.put(key, value);
    }

    @Override
    public void remove(Float key) {
        treeMap.remove(key);
    }

    @Override
    public V get(Float key) {
        return treeMap.get(key);
    }

    @Override
    public List<V> getFirstN(int cnt) {
        List<V> list = new ArrayList<>(cnt);
        int i = 0;
        for (var v : treeMap.entrySet()) {
            list.add(v.getValue());
            i++;
            if (i == cnt)
                break;
        }
        return list;
    }


}

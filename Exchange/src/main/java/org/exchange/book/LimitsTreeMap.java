package org.exchange.book;

import java.util.*;

/**
 * Wrapper around TreeMap that implements LimitsMap interface
 *
 * @param <K>
 * @param <V>
 */
public class LimitsTreeMap<K extends Comparable<K>, V> implements LimitsMap<K, V> {
    private final TreeMap<K, V> treeMap;

    public LimitsTreeMap() {
        treeMap = new TreeMap<>();
    }

    public LimitsTreeMap(Comparator<K> tComparator) {
        treeMap = new TreeMap<>(tComparator);
    }

    @Override
    public Map.Entry<K, V> firstEntry() {
        return treeMap.firstEntry();
    }

    @Override
    public void put(K key, V value) {
        treeMap.put(key, value);
    }

    @Override
    public void remove(K key) {
        treeMap.remove(key);
    }

    @Override
    public V get(K key) {
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

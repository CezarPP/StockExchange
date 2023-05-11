package org.exchange.book;

import java.util.Map;

/**
 * Interface for ds that has the methods needed for interacting with the best bid/ask order
 *
 * @param <K> -> key type -> Integer -> limit's price
 * @param <V> -> value type -> Limit
 */
public interface LimitsMap<K, V> {
    Map.Entry<K, V> firstEntry();

    void put(K key, V value);

    void remove(K key);

    V get(K key);
}

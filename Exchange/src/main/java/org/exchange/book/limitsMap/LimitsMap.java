package org.exchange.book.limitsMap;

import java.util.List;
import java.util.Map;

/**
 * Interface for ds that has the methods needed for interacting with the best bid/ask order
 *
 * @param <V> -> value type -> Limit
 */
public interface LimitsMap<V> {
    Map.Entry<Float, V> firstEntry();

    void put(Float key, V value);

    void remove(Float key);

    V get(Float key);

    List<V> getFirstN(int cnt);
}

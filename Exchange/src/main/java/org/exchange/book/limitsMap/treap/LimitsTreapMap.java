package org.exchange.book.limitsMap.treap;

import org.exchange.book.limitsMap.LimitsMap;

import java.util.List;
import java.util.Map;

public class LimitsTreapMap<V> implements LimitsMap<V> {
    private Treap<V> root;
    private final boolean isDecreasing;

    public LimitsTreapMap() {
        this.root = null;
        this.isDecreasing = false;
    }

    public LimitsTreapMap(boolean isDecreasing) {
        this.root = null;
        this.isDecreasing = isDecreasing;
    }

    @Override
    public Map.Entry<Float, V> firstEntry() {
        if (root == null) {
            return null;
        }
        Treap<V> element = (isDecreasing) ? Treap.last(root) : Treap.first(root);
        if (element == null)
            return null;
        return Map.entry(element.key, element.value);
    }

    @Override
    public void put(Float key, V value) {
        Treap<V> node = Treap.search(root, key);
        if (node != null) {
            node.value = value;
        } else {
            root = Treap.add(root, new Treap<>(key, value));
        }
    }

    @Override
    public void remove(Float key) {
        root = Treap.remove(root, key);
    }

    @Override
    public V get(Float key) {
        Treap<V> node = Treap.search(root, key);
        if (node == null)
            return null;
        return node.value;
    }

    @Override
    public List<V> getFirstN(int cnt) {
        return (isDecreasing) ? Treap.lastN(root, cnt) : Treap.firstN(root, cnt);
    }
}

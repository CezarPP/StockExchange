package org.exchange.book.limitsMap.splay;

class Node<V> {
    float key;
    V value;
    Node<V> left, right;

    public Node(float key, V value) {
        this.key = key;
        this.value = value;
        left = right = null;
    }
}
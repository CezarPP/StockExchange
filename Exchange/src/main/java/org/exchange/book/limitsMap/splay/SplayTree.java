package org.exchange.book.limitsMap.splay;

import org.exchange.book.limitsMap.LimitsMap;

import java.util.*;

public class SplayTree<V> implements LimitsMap<V> {
    private Node<V> root;
    private final boolean isDescending;

    public SplayTree() {
        root = null;
        isDescending = false;
    }

    public SplayTree(boolean isDescending) {
        root = null;
        this.isDescending = isDescending;
    }

    private Node<V> rightRotate(Node<V> node) {
        Node<V> left = node.left;
        node.left = left.right;
        left.right = node;
        return left;
    }

    private Node<V> leftRotate(Node<V> node) {
        Node<V> right = node.right;
        node.right = right.left;
        right.left = node;
        return right;
    }

    private Node<V> splay(Node<V> root, float key) {
        if (root == null || root.key == key)
            return root;

        if (root.key > key) {
            if (root.left == null)
                return root;

            if (root.left.key > key) {
                root.left.left = splay(root.left.left, key);
                root = rightRotate(root);
            } else if (root.left.key < key) {
                root.left.right = splay(root.left.right, key);
                if (root.left.right != null)
                    root.left = leftRotate(root.left);
            }

            return (root.left == null) ? root : rightRotate(root);
        } else {
            if (root.right == null)
                return root;

            if (root.right.key < key) {
                root.right.right = splay(root.right.right, key);
                root = leftRotate(root);
            } else if (root.right.key > key) {
                root.right.left = splay(root.right.left, key);
                if (root.right.left != null)
                    root.right = rightRotate(root.right);
            }

            return (root.right == null) ? root : leftRotate(root);
        }
    }

    @Override
    public Map.Entry<Float, V> firstEntry() {
        return (isDescending) ? getLastEntry() : getFirstEntry();
    }

    private Map.Entry<Float, V> getFirstEntry() {
        if (root == null) {
            return null;
        }

        Node<V> node = root;
        while (node.left != null) {
            node = node.left;
        }

        return Map.entry(node.key, node.value);
    }

    @Override
    public void put(Float key, V value) {
        if (root == null) {
            root = new Node<>(key, value);
            return;
        }

        root = splay(root, key);

        if (root.key == key) {
            root.value = value;
            return;
        }

        Node<V> newNode = new Node<>(key, value);

        if (root.key > key) {
            newNode.right = root;
            newNode.left = root.left;
            root.left = null;
        } else {
            newNode.left = root;
            newNode.right = root.right;
            root.right = null;
        }

        root = newNode;
    }

    @Override
    public void remove(Float key) {
        if (root == null) return;

        root = splay(root, key);

        if (root.key != key) return;

        if (root.left == null) {
            root = root.right;
        } else {
            Node<V> temp = root;
            root = splay(root.left, key);
            root.right = temp.right;
        }
    }

    @Override
    public V get(Float key) {
        root = splay(root, key);
        return root.key == key ? root.value : null;
    }


    public List<V> getFirstN(int cnt) {
        return (isDescending) ? doGetLastN(cnt) : doGetFirstN(cnt);
    }

    public List<V> doGetFirstN(int cnt) {
        List<V> result = new ArrayList<>();
        Stack<Node<V>> stack = new Stack<>();
        Node<V> node = root;

        while (node != null || !stack.isEmpty()) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }

            node = stack.pop();
            result.add(node.value);
            if (result.size() == cnt) break;
            node = node.right;
        }

        return result;
    }

    private Map.Entry<Float, V> getLastEntry() {
        if (root == null) {
            return null;
        }

        Node<V> node = root;
        while (node.right != null) {
            node = node.right;
        }

        return Map.entry(node.key, node.value);
    }

    public List<V> doGetLastN(int cnt) {
        List<V> result = new ArrayList<>();
        Stack<Node<V>> stack = new Stack<>();
        Node<V> node = root;

        while (node != null || !stack.isEmpty()) {
            while (node != null) {
                stack.push(node);
                node = node.right;
            }

            node = stack.pop();
            result.add(node.value);
            if (result.size() == cnt)
                break;
            node = node.left;
        }

        return result;
    }
}
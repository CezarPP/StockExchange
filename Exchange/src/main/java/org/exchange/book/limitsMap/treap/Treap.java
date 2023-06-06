package org.exchange.book.limitsMap.treap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Treap<T> {
    private static final Random rand = new Random();
    float key;
    int priority;
    T value;
    Treap<T>[] children;

    @SuppressWarnings("unchecked")
    public Treap(float key, T value) {
        this.key = key;
        this.value = value;
        children = (Treap<T>[]) new Treap[2];
        priority = rand.nextInt();
    }

    @SuppressWarnings("unchecked")
    static <T> Treap<T>[] split(Treap<T> treap, float x) {
        if (treap == null)
            return new Treap[]{null, null};
        if (treap.key < x) {
            Treap<T>[] p = split(treap.children[1], x);
            treap.children[1] = p[0];
            return new Treap[]{treap, p[1]};
        } else {
            Treap<T>[] p = split(treap.children[0], x);
            treap.children[0] = p[1];
            return new Treap[]{p[0], treap};
        }
    }

    static <T> Treap<T> merge(Treap<T> A, Treap<T> B) {
        if (A == null)
            return B;
        if (B == null)
            return A;
        if (A.priority > B.priority) {
            A.children[1] = merge(A.children[1], B);
            return A;
        } else {
            B.children[0] = merge(A, B.children[0]);
            return B;
        }
    }

    static <T> Treap<T> add(Treap<T> treap, Treap<T> x) {
        if (treap == null) {
            return x;
        }
        if (x.priority > treap.priority) {
            x.children = split(treap, x.key);
            return x;
        }
        if (x.key < treap.key) {
            treap.children[0] = add(treap.children[0], x);
        } else {
            treap.children[1] = add(treap.children[1], x);
        }
        return treap;
    }

    static <T> Treap<T> remove(Treap<T> treap, float x) {
        if (treap == null) {
            return null;
        }
        if (x < treap.key) {
            treap.children[0] = remove(treap.children[0], x);
        } else if (x > treap.key) {
            treap.children[1] = remove(treap.children[1], x);
        } else {
            treap = merge(treap.children[0], treap.children[1]);
        }
        return treap;
    }

    static <T> Treap<T> search(Treap<T> treap, float x) {
        if (treap == null) {
            return null;
        }
        if (x < treap.key) {
            return search(treap.children[0], x);
        } else if (x > treap.key) {
            return search(treap.children[1], x);
        } else {
            return treap;
        }
    }

    static <T> Treap<T> first(Treap<T> treap) {
        if (treap == null) {
            return null;
        }
        Treap<T> node = treap;
        while (node.children[0] != null) {
            node = node.children[0];
        }
        return node;
    }

    static <T> Treap<T> last(Treap<T> treap) {
        if (treap == null) {
            return null;
        }
        Treap<T> node = treap;
        while (node.children[1] != null) {
            node = node.children[1];
        }
        return node;
    }

    static <T> List<T> firstN(Treap<T> treap, int n) {
        List<T> result = new ArrayList<>();
        firstNRec(treap, result, n);
        return result;
    }

    private static <T> void firstNRec(Treap<T> node, List<T> result, int n) {
        if (node == null || result.size() >= n) {
            return;
        }
        firstNRec(node.children[0], result, n);
        if (result.size() < n) {
            result.add(node.value);
            firstNRec(node.children[1], result, n);
        }
    }

    static <T> List<T> lastN(Treap<T> treap, int n) {
        List<T> result = new ArrayList<>();
        lastNRec(treap, result, n);
        return result;
    }

    private static <T> void lastNRec(Treap<T> node, List<T> result, int n) {
        if (node == null || result.size() >= n) {
            return;
        }
        lastNRec(node.children[1], result, n);
        if (result.size() < n) {
            result.add(node.value);
            lastNRec(node.children[0], result, n);
        }
    }

}
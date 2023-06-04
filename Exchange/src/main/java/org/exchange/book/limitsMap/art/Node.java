package org.exchange.book.limitsMap.art;

import java.util.Arrays;

/*
 * Paper:
 * - 2 types of nodes: inner nodes and leaf nodes (store values)
 * - "the most efficient representation of an inner node is as an array of 2^s pointers"
 * - span ->>> "s bit chunk of the key" -> 1 byte for ART -> height = k / s, where we have a k bit key
 *
 * Lazy expansion   -> "Inner nodes are created only if they are required to distinguish at least two leaf nodes"
 *                  -> "Paths to leaves may be truncated: this optimization requires that the key is stored at the leaf"
 *
 * Path compression -> "Removes all inner nodes that have only a single child"
 *      2 approaches:
 *      Pessimistic      -> Partial key vector for each inner node, contains the keys of all preceding nodes which have been removed
 *                       -> During lookup, this vector is compared to the search key
 *      Optimistic       -> Only the count of the preceding one way nodes (the length of the vector is stored)
 *                       -> During lookup, skip this number of bytes. When at leaf, compare to ensure no wrong turn was taken
 *
 */


/**
 * Each inner node contains the node type, the number of children and the compressed path
 * Each inner node contains a key prefix
 */
abstract class Node {
    // Number of children
    int count;

    // Useful for keeping the radix tree structure
    Node parent;

    // Useful for fast operations, what key the parent has for this child node
    // If the parent is null, the value of edge is irrelevant
    byte edge;

    // The prefix contains only the new info that the node brings
    // It doesn't contain the full path from the root
    byte[] prefix;
    int prefixLength;

    public Node(Node parent) {
        count = 0;
        this.parent = parent;
        this.prefix = new byte[8];
        this.prefixLength = 0;
    }

    public Node(Node parent, byte[] prefix, int prefixLength) {
        this(parent);
        System.arraycopy(prefix, 0, this.prefix, 0, prefixLength);
        this.prefixLength = prefixLength;
    }
    // public abstract Node findChild();
}

/**
 * because the path can be truncated, we need to store the key at the leaf
 *
 * @param <V>
 */
class LeafNode<V> extends Node {
    V value;
    byte[] key; // the full key

    public LeafNode(Node parent, byte[] key, V value) {
        super(parent);
        this.key = key;
        this.value = value;
    }
}

class Node4 extends Node {
    // keys are sorted, linear search to find one
    byte[] keys = new byte[4];
    Node[] children = new Node[4];

    public Node4(Node parent) {
        super(parent);
    }

    public Node4(Node parent, byte[] prefix, int prefixLength) {
        super(parent, prefix, prefixLength);
    }
}

class Node16 extends Node {
    // keys are sorted, can find either using binary search or SIMD instructions
    byte[] keys = new byte[16];
    Node[] children = new Node[16];

    public Node16(Node parent) {
        super(parent);
    }

    public Node16(Node parent, byte[] prefix, int prefixLength) {
        super(parent, prefix, prefixLength);
    }
}

class Node48 extends Node {
    /// 1 indirection layer that saves space
    // array indexed by key bytes
    byte[] index = new byte[256];
    Node[] children = new Node[48];

    public Node48(Node parent) {
        super(parent);
        Arrays.fill(index, (byte) -1);
    }

    public Node48(Node parent, byte[] prefix, int prefixLength) {
        super(parent, prefix, prefixLength);
        Arrays.fill(index, (byte) -1);
    }
}

class Node256 extends Node {
    // one child for every possible value of a byte
    Node[] children = new Node[256];

    public Node256(Node parent) {
        super(parent);
    }

    public Node256(Node parent, byte[] prefix, int prefixLength) {
        super(parent, prefix, prefixLength);
    }
}
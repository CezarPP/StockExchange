package org.exchange.book.limitsMap.art;

import org.exchange.book.limitsMap.LimitsMap;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Adaptive Radix Tree that implements LimitsMap interface
 *
 * @param <V>
 */
public class LimitsArtMap<V> implements LimitsMap<V> {
    Node root;
    private final boolean isDecreasing;

    public LimitsArtMap() {
        this.root = null;
        this.isDecreasing = false;
    }

    public LimitsArtMap(boolean isDecreasing) {
        this.root = null;
        this.isDecreasing = isDecreasing;
    }


    /**
     * @param node  -> node having a compressed path
     * @param key   -> key
     * @param depth -> depth to start comparing
     * @return -> number of equal bytes
     */
    private int checkPrefix(Node node, byte[] key, int depth) {
        int i = depth;
        while (i < key.length && i - depth < node.prefixLength && key[i] == node.prefix[i - depth])
            i++;
        return i - depth;
    }

    private Node findChild(Node node, byte key) {
        if (node instanceof Node4 node4) {
            for (int i = 0; i < node4.count; i++)
                if (node4.keys[i] == key) {
                    return node4.children[i];
                }
        } else if (node instanceof Node16 node16) {
            for (int i = 0; i < node16.count; i++)
                if (node16.keys[i] == key)
                    return node16.children[i];
        } else if (node instanceof Node48 node48) {
            byte index = node48.index[key & 0xFF];
            if (index != -1) {
                return node48.children[index & 0xFF];
            }
        } else if (node instanceof Node256 node256) {
            return node256.children[key & 0xFF];
        } else {
            throw new IllegalArgumentException("Node type unknown");
        }
        return null;
    }

    private boolean isFull(Node node) {
        if (node instanceof Node4) {
            return node.count == 4;
        } else if (node instanceof Node16)
            return node.count == 16;
        else if (node instanceof Node48)
            return node.count == 48;
        else if (node instanceof Node256)
            return false;
        throw new IllegalArgumentException("Node type unknown for isFull");
    }

    /**
     * @param node -> Node that is already full
     * @return -> New bigger node
     */
    private Node grow(Node node) {
        if (node instanceof Node4 node4) {
            Node16 newNode = new Node16(node.parent, node.prefix, node.prefixLength);
            for (int i = 0; i < 4; i++) {
                newNode.keys[i] = node4.keys[i];
                newNode.children[i] = node4.children[i];
                newNode.children[i].parent = newNode;
            }
            newNode.count = 4;
            return newNode;
        } else if (node instanceof Node16 node16) {
            Node48 newNode = new Node48(node.parent, node.prefix, node.prefixLength);
            for (byte i = 0; i < 16; i++) {
                newNode.index[node16.keys[i] & 0xFF] = i;
                newNode.children[i] = node16.children[i];
                newNode.children[i].parent = newNode;
            }
            newNode.count = 16;
            return newNode;
        } else if (node instanceof Node48 node48) {
            Node256 newNode = new Node256(node.parent, node.prefix, node.prefixLength);
            for (int i = 0; i < 256; i++) {
                int index = node48.index[i];
                if (index != -1) {
                    newNode.children[i] = node48.children[index & 0xFF];
                    newNode.children[i].parent = newNode;
                }
            }
            newNode.count = 48;
            return newNode;
        }
        throw new IllegalArgumentException("Node type unknown for grow");
    }

    private void replaceChild(Node parent, byte key, Node newChild) {
        assert parent != null;
        if (parent instanceof Node4 node4) {
            int index = -1;
            for (int i = 0; i < parent.count; i++)
                if (node4.keys[i] == key)
                    index = i;

            assert index != -1;

            node4.children[index] = newChild;
        } else if (parent instanceof Node16 node16) {
            int index = -1;
            for (int i = 0; i < parent.count; i++)
                if (node16.keys[i] == key)
                    index = i;

            assert index != -1;

            node16.children[index] = newChild;
        } else if (parent instanceof Node48 node48) {
            byte index = node48.index[key & 0xFF];

            assert index != -1;

            node48.children[index & 0xFF] = newChild;
        } else if (parent instanceof Node256 node256) {
            node256.children[key & 0xFF] = newChild;
        } else {
            throw new IllegalArgumentException("Parent node type is unknown for replaceChild");
        }
        newChild.edge = key;
        newChild.parent = parent;
    }

    private void replace(Node node, Node newNode) {
        if (node.parent == null) {
            root = newNode;
        } else {
            replaceChild(node.parent, node.edge, newNode);
        }
    }

    boolean leafMatches(Node node, byte[] key) {
        assert node instanceof LeafNode<?>;
        if (((LeafNode<?>) node).key.length == key.length) {
            for (int i = 0; i < key.length; i++)
                if (key[i] != ((LeafNode<?>) node).key[i])
                    return false;
            return true;
        }
        return false;
    }

    private LeafNode<?> search(Node node, byte[] key, int depth) {
        if (node == null)
            return null;
        if (node instanceof LeafNode<?>) {
            if (leafMatches(node, key))
                return (LeafNode<?>) node;
            return null;
        }
        if (checkPrefix(node, key, depth) != node.prefixLength)
            return null;
        depth = depth + node.prefixLength;
        Node next = findChild(node, key[depth]);
        return search(next, key, depth + 1);
    }

    private void insert(Node node, byte[] key, LeafNode<V> leaf, int depth) {
        // empty tree
        if (node == null) {
            root = leaf;
            return;
        }

        if (node instanceof LeafNode<?>) {
            // Leaf could be the same as the leaf we want to insert
            if (leafMatches(node, key)) {
                ((LeafNode<V>) node).value = leaf.value;
                return;
            }

            // Encountered leaf because of lazy expansion
            // It is replaced by an inner node
            // It will have as children the new value inserted (a new leaf) and the existing leaf
            Node4 newNode = new Node4(node.parent);
            int i;
            for (i = depth; i < key.length && key[i] == ((LeafNode<?>) node).key[i]; i++) {
                newNode.prefix[i - depth] = key[i];
            }
            newNode.prefixLength = i - depth;
            depth = depth + newNode.prefixLength;

            addChild(newNode, key[depth], leaf);

            replace(node, newNode);

            // node is leaf, so should still contain the full key
            addChild(newNode, ((LeafNode<?>) node).key[depth], node);
            return;
        }

        int p = checkPrefix(node, key, depth);
        if (p != node.prefixLength) {
            // Prefix mismatch
            // The key of the new leaf differs from the compressed path
            // Create a new inner node above the current node and adjust the compressed paths

            Node newNode = new Node4(node.parent);
            addChild(newNode, key[depth + p], leaf);

            replace(node, newNode);

            newNode.prefixLength = p;
            System.arraycopy(node.prefix, 0, newNode.prefix, 0, p);

            node.prefixLength = node.prefixLength - (p + 1);
            for (int i = 0; i < node.prefixLength; i++)
                node.prefix[i] = node.prefix[i + p + 1];

            addChild(newNode, node.prefix[p], node);
            return;
        }
        // prefix match
        depth = depth + node.prefixLength;
        Node next = findChild(node, key[depth]);
        if (next != null)
            insert(next, key, leaf, depth + 1);
        else {
            // add to inner node
            if (isFull(node)) {
                Node newNode = grow(node);
                replace(node, newNode);
                addChild(newNode, key[depth], leaf);
            } else
                addChild(node, key[depth], leaf);
        }
    }

    private void addChild(Node node, byte key, Node child) {
        assert (!isFull(node));
        if (node instanceof Node4 node4) {
            node4.keys[node.count] = key;
            node4.children[node.count] = child;
        } else if (node instanceof Node16 node16) {
            node16.keys[node.count] = key;
            node16.children[node.count] = child;
        } else if (node instanceof Node48 node48) {
            node48.index[key & 0xFF] = (byte) node.count;
            node48.children[node.count] = child;
        } else if (node instanceof Node256 node256) {
            node256.children[key & 0xFF] = child;
        } else
            throw new IllegalArgumentException("Node type unknown for addChild");
        child.edge = key;
        node.count++;
        child.parent = node;
    }

    public void delete(byte[] key) {
        // Find the leaf with the given key
        LeafNode<?> leaf = search(root, key, 0);
        if (leaf == null) {
            // The key does not exist in the tree
            return;
        }

        Node parent = leaf.parent;
        if (parent == null) {
            // leaf is root
            root = null;
            return;
        }

        // Start from the leaf and go up the tree
        deleteChild(parent, leaf.edge);

        // Check if parent needs to be shark
        while (parent != null && parent.count == 1 && !(parent instanceof LeafNode)) {
            shrink(parent);
            parent = parent.parent;
        }
    }

    private Node getOnlyChild(Node node) {
        Node child = null;
        if (node instanceof Node4 node4) {
            child = node4.children[0];
        } else if (node instanceof Node16 node16) {
            child = node16.children[0];
        } else if (node instanceof Node48 node48) {
            child = node48.children[0];
        } else if (node instanceof Node256 node256) {
            for (int i = 0; i < 256; i++) {
                if (node256.children[i] != null) {
                    child = node256.children[i];
                    break;
                }
            }
        } else
            throw new IllegalArgumentException("Unknown node type for getOnlyChild");
        return child;
    }

    private static byte[] concatenateArrays(byte[] prefix1, byte[] prefix2, int prefixLength1, int prefixLength2) {
        byte[] concat = new byte[prefixLength1 + prefixLength2];
        for (int i = 0; i < prefixLength1; i++)
            concat[i] = prefix1[i];
        for (int i = prefixLength1; i < prefixLength1 + prefixLength2; i++)
            concat[i] = prefix2[i - prefixLength1];
        return concat;
    }

    private void shrink(Node node) {
        // This node only has one child
        assert node.count == 1;
        Node child = getOnlyChild(node);
        assert child != null;
        // Replace the node with the child

        child.prefixLength = node.prefixLength + 1 + child.prefixLength;
        byte[] newPrefix = concatenateArrays(node.prefix, new byte[]{child.edge}, node.prefixLength, 1);
        child.prefix = concatenateArrays(newPrefix, child.prefix, newPrefix.length, child.prefixLength);
        replace(node, child);
    }

    private void deleteChild(Node parent, byte key) {
        int index = -1;
        if (parent instanceof Node4 node4) {
            for (int i = 0; i < node4.count; i++)
                if (node4.keys[i] == key)
                    index = i;
            assert index != -1;
            // Move remaining children left
            for (int i = index; i < node4.count - 1; i++) {
                node4.children[i] = node4.children[i + 1];
                node4.keys[i] = node4.keys[i + 1];
            }
        } else if (parent instanceof Node16 node16) {
            for (int i = 0; i < node16.count; i++)
                if (node16.keys[i] == key)
                    index = i;
            assert index != -1;
            // Move remaining children left
            for (int i = index; i < node16.count - 1; i++) {
                node16.children[i] = node16.children[i + 1];
                node16.keys[i] = node16.keys[i + 1];
            }
        } else if (parent instanceof Node48 node48) {
            index = node48.index[key & 0xFF];
            assert index != -1;
            node48.index[key & 0xFF] = -1;
            // Move remaining children left
            for (int i = index; i < node48.count - 1; i++) {
                node48.children[i] = node48.children[i + 1];
                node48.index[node48.children[i].edge & 0xFF] = (byte) i;
            }
        } else if (parent instanceof Node256 node256) {
            node256.children[key & 0xFF] = null;
        }

        parent.count--;
    }

    private NodeKeyChildPair[] createPairs(byte[] keys, Node[] children, int count) {
        NodeKeyChildPair[] pairs = new NodeKeyChildPair[count];
        for (int i = 0; i < count; i++) {
            pairs[i] = new NodeKeyChildPair(keys[i] & 0xFF, children[i]);
        }
        Arrays.sort(pairs, Comparator.comparingInt(NodeKeyChildPair::getKey));
        return pairs;
    }

    private void updateNode(byte[] keys, Node[] children, NodeKeyChildPair[] pairs) {
        for (int i = 0; i < pairs.length; i++) {
            keys[i] = (byte) pairs[i].getKey();
            children[i] = pairs[i].getChild();
        }
    }

    private void sortChildren(Node node) {
        NodeKeyChildPair[] pairs;
        if (node instanceof Node4 node4) {
            pairs = createPairs(node4.keys, node4.children, node4.count);
            updateNode(node4.keys, node4.children, pairs);
        } else if (node instanceof Node16 node16) {
            pairs = createPairs(node16.keys, node16.children, node16.count);
            updateNode(node16.keys, node16.children, pairs);
        } else {
            throw new IllegalArgumentException("Unknown node type in sortChildren");
        }
    }

    private LeafNode<V> getFirstElement(Node node) {
        if (node == null)
            return null;
        if (node instanceof LeafNode<?>) {
            return (LeafNode<V>) node;
        } else {
            if (node.count == 0)
                return null;
            Node newNode = null;
            if (node instanceof Node4 node4) {
                sortChildren(node4);
                newNode = node4.children[0];
            } else if (node instanceof Node16 node16) {
                sortChildren(node16);
                newNode = node16.children[0];
            } else if (node instanceof Node48 node48) {
                for (int i = 0; i < 256; i++) {
                    if (node48.index[i] != -1) {
                        newNode = node48.children[node48.index[i]];
                        break;
                    }
                }
            } else if (node instanceof Node256 node256) {
                for (int i = 0; i < 256; i++) {
                    if (node256.children[i] != null) {
                        newNode = node256.children[i];
                        break;
                    }
                }
            } else
                throw new IllegalArgumentException("Unknown node type");
            return getFirstElement(newNode);
        }
    }

    private LeafNode<V> getLastElement(Node node) {
        if (node instanceof LeafNode<?>)
            return (LeafNode<V>) node;
        else {
            Node newNode = null;
            if (node.count == 0)
                return null;
            if (node instanceof Node4 node4) {
                sortChildren(node4);
                newNode = node4.children[node.count - 1];
            } else if (node instanceof Node16 node16) {
                sortChildren(node16);
                newNode = node16.children[node.count - 1];
            } else if (node instanceof Node48 node48) {
                for (int i = 255; i >= 0; i--) {
                    if (node48.index[i] != -1) {
                        newNode = node48.children[node48.index[i]];
                        break;
                    }
                }
            } else if (node instanceof Node256 node256) {
                for (int i = 255; i >= 0; i--) {
                    if (node256.children[i] != null) {
                        newNode = node256.children[i];
                        break;
                    }
                }
            } else
                throw new IllegalArgumentException("Unknown node type for getLastElement");
            return getLastElement(newNode);
        }
    }

    // Returns the bytes of the float that will be inserted
    public static byte[] floatToBytes(float value) {
        int intBits = Float.floatToIntBits(value);
        return new byte[]{
                (byte) ((intBits >> 24) & 0xFF),
                (byte) ((intBits >> 16) & 0xFF),
                (byte) ((intBits >> 8) & 0xFF),
                (byte) (intBits & 0xFF)
        };
    }

    public static float bytesToFloat(byte[] bytes) {
        int intBits = ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
        return Float.intBitsToFloat(intBits);
    }

    private LeafNode<V> getNextSmallestLeaf(LeafNode<V> node) {
        Node parent = node.parent;
        byte edge = node.edge;
        while (parent != null) {
            if (parent instanceof Node4 parent4) {
                sortChildren(parent4);
                for (int i = 0; i < parent4.count; i++) {
                    if (parent4.keys[i] == edge && i + 1 < parent4.count) {
                        return getFirstElement(parent4.children[i + 1]);
                    }
                }
            } else if (parent instanceof Node16 parent16) {
                sortChildren(parent16);
                for (int i = 0; i < parent16.count; i++) {
                    if (parent16.keys[i] == edge && i + 1 < parent16.count) {
                        return getFirstElement(parent16.children[i + 1]);
                    }
                }
            } else if (parent instanceof Node48 parent48) {
                for (int i = (edge & 0xFF) + 1; i < 256; i++)
                    if (parent48.index[i] != -1) {
                        return getFirstElement(parent48.children[parent48.index[i]]);
                    }
            } else if (parent instanceof Node256 parent256) {
                for (int i = (edge & 0xFF) + 1; i < 256; i++) {
                    if (parent256.children[i] != null) {
                        return getFirstElement(parent256.children[i]);
                    }
                }
            }
            edge = parent.edge;
            parent = parent.parent;
        }
        return null;
    }

    private LeafNode<V> getNextBiggestLeaf(LeafNode<V> node) {
        Node parent = node.parent;
        byte edge = node.edge;
        while (parent != null) {
            if (parent instanceof Node4 parent4) {
                sortChildren(parent4);
                for (int i = parent4.count - 1; i >= 0; i--) {
                    if (parent4.keys[i] == edge && i - 1 >= 0) {
                        return getLastElement(parent4.children[i - 1]);
                    }
                }
            } else if (parent instanceof Node16 parent16) {
                sortChildren(parent16);
                for (int i = parent16.count - 1; i >= 0; i--) {
                    if (parent16.keys[i] == edge && i - 1 >= 0) {
                        return getLastElement(parent16.children[i - 1]);
                    }
                }
            } else if (parent instanceof Node48 parent48) {
                for (int i = (edge & 0xFF) - 1; i >= 0; i--)
                    if (parent48.index[i] != -1) {
                        return getLastElement(parent48.children[parent48.index[i]]);
                    }
            } else if (parent instanceof Node256 parent256) {
                for (int i = (edge & 0xFF) - 1; i >= 0; i--) {
                    if (parent256.children[i] != null) {
                        return getLastElement(parent256.children[i]);
                    }
                }
            }
            edge = parent.edge;
            parent = parent.parent;
        }
        return null;
    }

    @Override
    public List<V> getFirstN(int n) {
        List<V> entries = new ArrayList<>();
        LeafNode<V> node = (isDecreasing) ? getLastElement(root) : getFirstElement(root);
        while (node != null && entries.size() < n) {
            entries.add(node.value);
            node = (isDecreasing) ? getNextBiggestLeaf(node) : getNextSmallestLeaf(node);
        }
        return entries;
    }

    @Override
    public void put(Float key, V value) {
        byte[] keyBytes = floatToBytes(key);
        LeafNode<V> leafNode = new LeafNode<>(null, keyBytes, value);
        insert(root, keyBytes, leafNode, 0);
    }

    @Override
    public void remove(Float key) {
        byte[] keyBytes = floatToBytes(key);
        delete(keyBytes);
    }

    @Override
    public V get(Float key) {
        byte[] keyBytes = ByteBuffer.allocate(4).putFloat(key).array();
        LeafNode<V> leaf = (LeafNode<V>) search(root, keyBytes, 0);
        if (leaf == null)
            return null;
        return leaf.value;
    }

    @Override
    public Map.Entry<Float, V> firstEntry() {
        if (root == null) {
            return null;
        }
        LeafNode<V> element = (isDecreasing) ? getLastElement(root) : getFirstElement(root);
        if (element == null)
            return null;
        return Map.entry(bytesToFloat(element.key), element.value);
    }
}

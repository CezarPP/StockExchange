package org.exchange.book.limitsMap.art;

import org.exchange.book.limitsMap.LimitsMap;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Adaptive Radix Tree that implements LimitsMap interface
 *
 * @param <V>
 */
public class LimitsArtMap<V> implements LimitsMap<V> {
    Node root;

    public LimitsArtMap() {
        root = null;
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
        if (node instanceof Node4) {
            for (int i = 0; i < node.count; i++)
                if (((Node4) node).keys[i] == key) {
                    return ((Node4) node).children[i];
                }
        } else if (node instanceof Node16) {
            for (int i = 0; i < node.count; i++)
                if (((Node16) node).keys[i] == key)
                    return ((Node16) node).children[i];
        } else if (node instanceof Node48) {
            byte index = ((Node48) node).index[key & 0xFF];
            if (index != -1) {
                return ((Node48) node).children[index & 0xFF];
            }
        } else if (node instanceof Node256) {
            return ((Node256) node).children[key & 0xFF];
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

    private void setNewParentToChildren(Node node, Node newNode) {
        if (node instanceof Node4) {
            for (int i = 0; i < node.count; i++)
                ((Node4) node).children[i].parent = newNode;
        } else if (node instanceof Node16) {
            for (int i = 0; i < node.count; i++)
                ((Node16) node).children[i].parent = newNode;
        } else if (node instanceof Node48) {
            for (int i = 0; i < node.count; i++)
                ((Node48) node).children[i].parent = newNode;
        } else if (node instanceof Node256) {
            for (int i = 0; i < 256; i++)
                if (((Node256) node).children[i] != null)
                    ((Node256) node).children[i].parent = newNode;
        }
    }

    /**
     * @param node -> Node that is already full
     * @return -> New bigger node
     */
    private Node grow(Node node) {
        if (node instanceof Node4) {
            Node16 newNode = new Node16(node.parent, node.prefix, node.prefixLength);
            for (int i = 0; i < 4; i++) {
                newNode.keys[i] = ((Node4) node).keys[i];
                newNode.children[i] = ((Node4) node).children[i];
                newNode.children[i].parent = newNode;
            }
            newNode.count = 4;
            return newNode;
        } else if (node instanceof Node16) {
            Node48 newNode = new Node48(node.parent, node.prefix, node.prefixLength);
            for (byte i = 0; i < 16; i++) {
                newNode.index[((Node16) node).keys[i] & 0xFF] = i;
                newNode.children[i] = ((Node16) node).children[i];
                newNode.children[i].parent = newNode;
            }
            newNode.count = 16;
            return newNode;
        } else if (node instanceof Node48) {
            Node256 newNode = new Node256(node.parent, node.prefix, node.prefixLength);
            for (int i = 0; i < 256; i++) {
                int index = ((Node48) node).index[i];
                if (index != -1) {
                    newNode.children[i] = ((Node48) node).children[index & 0xFF];
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
        if (parent instanceof Node4) {
            int index = -1;
            for (int i = 0; i < parent.count; i++)
                if (((Node4) parent).keys[i] == key)
                    index = i;

            assert index != -1 : "Key not found in Node4 keys";

            ((Node4) parent).children[index] = newChild;
        } else if (parent instanceof Node16) {
            int index = -1;
            for (int i = 0; i < parent.count; i++)
                if (((Node16) parent).keys[i] == key)
                    index = i;

            assert index != -1 : "Key not found in Node16 keys";

            ((Node16) parent).children[index] = newChild;
        } else if (parent instanceof Node48) {
            byte index = ((Node48) parent).index[key & 0xFF];

            assert index != -1 : "Key not found in Node48 index";

            ((Node48) parent).children[index & 0xFF] = newChild;
        } else if (parent instanceof Node256) {
            ((Node256) parent).children[key & 0xFF] = newChild;
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
        if (node instanceof Node4) {
            ((Node4) node).keys[node.count] = key;
            ((Node4) node).children[node.count] = child;
        } else if (node instanceof Node16) {
            ((Node16) node).keys[node.count] = key;
            ((Node16) node).children[node.count] = child;
        } else if (node instanceof Node48) {
            ((Node48) node).index[key & 0xFF] = (byte) node.count;
            ((Node48) node).children[node.count] = child;
        } else if (node instanceof Node256) {
            ((Node256) node).children[key & 0xFF] = child;
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

/*        // Check if parent needs to be shark
        while (parent != null && parent.count == 1 && !(parent instanceof LeafNode)) {
            if (!shrink(parent))
                break;
            parent = parent.parent;
        }*/
    }


    private void deleteChild(Node parent, byte key) {
        int index = -1;
        if (parent instanceof Node4) {
            Node4 node = (Node4) parent;
            for (int i = 0; i < node.count; i++)
                if (node.keys[i] == key)
                    index = i;
            assert index != -1;
            // Move remaining children left
            for (int i = index; i < node.count - 1; i++) {
                node.children[i] = node.children[i + 1];
                node.keys[i] = node.keys[i + 1];
            }
        } else if (parent instanceof Node16) {
            Node16 node = (Node16) parent;
            for (int i = 0; i < node.count; i++)
                if (node.keys[i] == key)
                    index = i;
            assert index != -1;
            // Move remaining children left
            for (int i = index; i < node.count - 1; i++) {
                node.children[i] = node.children[i + 1];
                node.keys[i] = node.keys[i + 1];
            }
        } else if (parent instanceof Node48) {
            Node48 node = (Node48) parent;
            index = node.index[key & 0xFF];
            assert index != -1;
            node.index[key & 0xFF] = -1;
            // Move remaining children left
            for (int i = index; i < node.count - 1; i++) {
                node.children[i] = node.children[i + 1];
                node.index[node.children[i].edge & 0xFF] = (byte) i;
            }
        } else if (parent instanceof Node256) {
            Node256 node = (Node256) parent;
            node.children[key & 0xFF] = null;
        }

        parent.count--;
    }

    private boolean shrink(Node node) {
        // Assume that the count of the current node is 1
        assert node.count == 1;
        Node child = null;
        if (node instanceof Node4) {
            child = ((Node4) node).children[0];
        } else if (node instanceof Node16) {
            child = ((Node16) node).children[0];
        } else if (node instanceof Node48) {
            child = ((Node48) node).children[0];
        } else if (node instanceof Node256) {
            for (int i = 0; i < 256; i++) {
                if (((Node256) node).children[i] != null) {
                    child = ((Node256) node).children[i];
                    break;
                }
            }
        }

        if (child instanceof LeafNode<?>)
            return false;
        assert child != null;
        deleteChild(node, child.edge);
        node.prefix[node.prefixLength++] = child.edge;
        for (int i = 0; i < child.prefixLength; i++) {
            node.prefix[node.prefixLength++] = child.prefix[i];
        }
        return true;
    }

    private LeafNode<V> getFirstElement() {
        if (root == null) {
            return null;
        }
        Node node = root;
        while (!(node instanceof LeafNode)) {
            if (node instanceof Node4) {
                node = ((Node4) node).children[0];
            } else if (node instanceof Node16) {
                node = ((Node16) node).children[0];
            } else if (node instanceof Node48) {
                for (int i = 0; i < 256; i++) {
                    if (((Node48) node).index[i] != -1) {
                        node = ((Node48) node).children[((Node48) node).index[i]];
                        break;
                    }
                }
            } else if (node instanceof Node256) {
                for (int i = 0; i < 256; i++) {
                    if (((Node256) node).children[i] != null) {
                        node = ((Node256) node).children[i];
                        break;
                    }
                }
            }
        }
        return (LeafNode<V>) node;
    }

    private byte[] floatToBytes(Float key) {
        return ByteBuffer.allocate(4).putFloat(key).array();
    }

    @Override
    public Map.Entry<Float, V> firstEntry() {
        LeafNode<V> firstElement = getFirstElement();
        assert firstElement != null;
        return Map.entry(ByteBuffer.wrap(firstElement.key).getFloat(), firstElement.value);
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
    public List<V> getFirstN(int cnt) {
        return null;
    }
}

package org.exchange.book;

import org.exchange.book.limitsMap.LimitsMap;
import org.exchange.book.limitsMap.splay.SplayTree;

public class LimitsMapSplayTreeTest extends AbstractLimitsMapTest {
    @Override
    protected LimitsMap<Limit> getLimitsMapDescending() {
        return new SplayTree<>(true);
    }

    @Override
    protected LimitsMap<Limit> getLimitsMapAscending() {
        return new SplayTree<>();
    }
}

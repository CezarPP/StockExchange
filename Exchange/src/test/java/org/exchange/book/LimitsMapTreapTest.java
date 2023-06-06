package org.exchange.book;

import org.exchange.book.limitsMap.LimitsMap;
import org.exchange.book.limitsMap.treap.LimitsTreapMap;

public class LimitsMapTreapTest extends AbstractLimitsMapTest {
    @Override
    protected LimitsMap<Limit> getLimitsMapDescending() {
        return new LimitsTreapMap<>(true);
    }

    @Override
    protected LimitsMap<Limit> getLimitsMapAscending() {
        return new LimitsTreapMap<>();
    }
}

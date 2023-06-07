package org.exchange.book;

import org.exchange.book.limitsMap.LimitsMap;
import org.exchange.book.limitsMap.LimitsTreeMap;

public class LimitsMapTreeSetTest extends AbstractLimitsMapTest {
    @Override
    protected LimitsMap<Limit> getLimitsMapDescending() {
        return new LimitsTreeMap<>(true);
    }

    @Override
    protected LimitsMap<Limit> getLimitsMapAscending() {
        return new LimitsTreeMap<>();
    }
}

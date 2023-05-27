package org.exchange.book;

import org.common.fix.order.Side;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LimitOrderTest {
    @Test
    void quantityTest() {
        Limit limit = new Limit(100, Side.BUY);
        LimitOrder limitOrder = new LimitOrder(1, 100, null, null, limit);
        assertEquals(limitOrder.getParentLimit(), limit);
        limitOrder.decreaseQuantity(200);
        assertEquals(limitOrder.getQuantity(), 0);
    }
}

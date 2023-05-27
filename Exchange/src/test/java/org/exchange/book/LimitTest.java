package org.exchange.book;

import org.common.fix.order.Side;
import org.common.symbols.Symbol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LimitTest {
    @Test
    void simpleLimitTest() {
        final int portId = 1;
        Limit limit = new Limit(12, Side.BUY);
        Order order1 = new Order(1, portId, Symbol.BA, 12, 100, Side.BUY);
        Order order2 = new Order(2, portId, Symbol.BA, 12, 50, Side.BUY);
        limit.addOrder(order1);
        limit.addOrder(order2);
        assertEquals(limit.getFirstOrder().getParentLimit(), limit);
        assertEquals(limit.getFirstOrder().getId(), 1);
        assertEquals(limit.getFirstOrder().getQuantity(), 100);
        limit.getFirstOrder().decreaseQuantity(200);
        if (limit.getFirstOrder().getQuantity() == 0)
            limit.removeFirstOrder();
        assertEquals(limit.getFirstOrder().getParentLimit(), limit);
        assertEquals(limit.getFirstOrder().getId(), 2);
    }
}

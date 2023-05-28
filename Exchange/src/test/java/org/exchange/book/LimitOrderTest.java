package org.exchange.book;

import org.common.fix.order.Side;
import org.common.symbols.Symbol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LimitOrderTest {
    @Test
    void quantityTest() {
        Limit limit = new Limit(100, Side.BUY);
        Order order = new Order(1, 1, 1, Symbol.BA, 100, 100, Side.BUY);
        LimitOrder limitOrder = new LimitOrder(order, null, null, limit);
        assertEquals(limitOrder.getParentLimit(), limit);
        limitOrder.decreaseQuantity(200);
        assertEquals(limitOrder.getQuantity(), 0);
    }
}

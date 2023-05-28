package org.exchange.book;

import org.common.fix.order.Side;
import org.common.symbols.Symbol;
import org.exchange.broadcast.BroadcastSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderBookTest {
    @Test
    void buyFirstTest() {
        BroadcastSender.setIsActive(false);
        final int portId = 1;
        OrderBook orderBook = new OrderBook(Symbol.WBA);
        // buy orders
        Order buyOrder1 = new Order(1, 1, portId, Symbol.WBA, 100, 50, Side.BUY);
        Order buyOrder2 = new Order(2, 2, portId, Symbol.WBA, 100, 25, Side.BUY);
        Order buyOrder3 = new Order(3, 3, portId, Symbol.WBA, 150, 10, Side.BUY);

        orderBook.addNewSingleOrder(buyOrder1);
        orderBook.addNewSingleOrder(buyOrder2);
        orderBook.addNewSingleOrder(buyOrder3);

        Order sellOrder1 = new Order(4, 4, portId, Symbol.WBA, 151, 50, Side.SELL);
        Order sellOrder2 = new Order(5, 5, portId, Symbol.WBA, 101, 5, Side.SELL);
        Order sellOrder3 = new Order(6, 6, portId, Symbol.WBA, 100, 25, Side.SELL);

        orderBook.addNewSingleOrder(sellOrder1);
        orderBook.addNewSingleOrder(sellOrder2);
        orderBook.addNewSingleOrder(sellOrder3);

        Limit bestBuy = orderBook.getFirstBuyLimit();
        Limit bestSell = orderBook.getFirstSellLimit();
        assertEquals(bestBuy.getPrice(), 100);
        assertEquals(bestSell.getPrice(), 151);
    }

    @Test
    void sellFirstTest() {
        BroadcastSender.setIsActive(false);
        final int portId = 1;

        OrderBook orderBook = new OrderBook(Symbol.WBA);

        //sell orders

        Order sellOrder1 = new Order(4, 1, portId, Symbol.WBA, 151, 50, Side.SELL);
        Order sellOrder2 = new Order(5, 2, portId, Symbol.WBA, 101, 5, Side.SELL);
        Order sellOrder3 = new Order(6, 3, portId, Symbol.WBA, 100, 25, Side.SELL);

        orderBook.addNewSingleOrder(sellOrder1);
        orderBook.addNewSingleOrder(sellOrder2);
        orderBook.addNewSingleOrder(sellOrder3);

        // buy orders
        Order buyOrder1 = new Order(1, 4, portId, Symbol.WBA, 100, 50, Side.BUY);
        Order buyOrder2 = new Order(2, 5, portId, Symbol.WBA, 100, 25, Side.BUY);
        Order buyOrder3 = new Order(3, 6, portId, Symbol.WBA, 150, 10, Side.BUY);

        orderBook.addNewSingleOrder(buyOrder1);
        orderBook.addNewSingleOrder(buyOrder2);
        orderBook.addNewSingleOrder(buyOrder3);

        Limit bestBuy = orderBook.getFirstBuyLimit();
        Limit bestSell = orderBook.getFirstSellLimit();
        assertEquals(bestBuy.getPrice(), 150);
        assertEquals(bestSell.getPrice(), 151);
    }

    @Test
    void cancelTest() {
        BroadcastSender.setIsActive(false);
        final int portId = 1;
        OrderBook orderBook = new OrderBook(Symbol.AAPL);

        Order buyOrder1 = new Order(1, 1, portId, Symbol.AAPL, 250, 50, Side.BUY);
        Order buyOrder2 = new Order(2, 2, portId, Symbol.AAPL, 200, 50, Side.BUY);
        orderBook.addNewSingleOrder(buyOrder1);
        orderBook.addNewSingleOrder(buyOrder2);

        orderBook.cancelOrder(buyOrder1);

        assertEquals(orderBook.getFirstBuyLimit().getPrice(), 200);

        Order sellOrder = new Order(3, 3, portId, Symbol.AAPL, 200, 50, Side.SELL);

        orderBook.addNewSingleOrder(sellOrder);

        assertNull(orderBook.getFirstBuyLimit());
        assertNull(orderBook.getFirstSellLimit());
    }
}

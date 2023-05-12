package org.exchange.book;

import org.common.symbols.SymbolDOW;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrderBookTest {
    @Test
    void buyFirstTest() {
        OrderBook orderBook = new OrderBook(SymbolDOW.WBA);
        // buy orders
        Order buyOrder1 = new Order(1, SymbolDOW.WBA, 100, 50, Side.BUY);
        Order buyOrder2 = new Order(2, SymbolDOW.WBA, 100, 25, Side.BUY);
        Order buyOrder3 = new Order(3, SymbolDOW.WBA, 150, 10, Side.BUY);

        orderBook.addNewSingleOrder(buyOrder1);
        orderBook.addNewSingleOrder(buyOrder2);
        orderBook.addNewSingleOrder(buyOrder3);

        Order sellOrder1 = new Order(4, SymbolDOW.WBA, 151, 50, Side.SELL);
        Order sellOrder2 = new Order(5, SymbolDOW.WBA, 101, 5, Side.SELL);
        Order sellOrder3 = new Order(6, SymbolDOW.WBA, 100, 25, Side.SELL);

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
        OrderBook orderBook = new OrderBook(SymbolDOW.WBA);

        //sell orders

        Order sellOrder1 = new Order(4, SymbolDOW.WBA, 151, 50, Side.SELL);
        Order sellOrder2 = new Order(5, SymbolDOW.WBA, 101, 5, Side.SELL);
        Order sellOrder3 = new Order(6, SymbolDOW.WBA, 100, 25, Side.SELL);

        orderBook.addNewSingleOrder(sellOrder1);
        orderBook.addNewSingleOrder(sellOrder2);
        orderBook.addNewSingleOrder(sellOrder3);

        // buy orders
        Order buyOrder1 = new Order(1, SymbolDOW.WBA, 100, 50, Side.BUY);
        Order buyOrder2 = new Order(2, SymbolDOW.WBA, 100, 25, Side.BUY);
        Order buyOrder3 = new Order(3, SymbolDOW.WBA, 150, 10, Side.BUY);

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
        OrderBook orderBook = new OrderBook(SymbolDOW.AAPL);

        Order buyOrder1 = new Order(1, SymbolDOW.AAPL, 250, 50, Side.BUY);
        Order butOrder2 = new Order(2, SymbolDOW.AAPL, 200, 50, Side.BUY);
        orderBook.addNewSingleOrder(buyOrder1);
        orderBook.addNewSingleOrder(butOrder2);

        orderBook.cancelOrder(1);

        assertEquals(orderBook.getFirstBuyLimit().getPrice(), 200);

        Order sellOrder = new Order(3, SymbolDOW.AAPL, 200, 50, Side.SELL);

        orderBook.addNewSingleOrder(sellOrder);

        assertNull(orderBook.getFirstBuyLimit());
        assertNull(orderBook.getFirstSellLimit());
    }
}

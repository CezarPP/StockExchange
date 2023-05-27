package org.exchange.book;

import org.common.symbols.Symbol;

import java.util.HashMap;
import java.util.Map;

public class OrderBookFactory {
    private final static Map<Symbol, OrderBook> orderBookMap = new HashMap<>();

    public static OrderBook getOrderBook(Symbol symbol) {
        OrderBook orderBook = orderBookMap.get(symbol);
        if (orderBook != null)
            return orderBook;
        orderBook = new OrderBook(symbol);
        orderBookMap.put(symbol, orderBook);
        orderBook.start();
        return orderBook;
    }

}

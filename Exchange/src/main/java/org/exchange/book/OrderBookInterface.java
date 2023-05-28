package org.exchange.book;

import java.util.List;

public interface OrderBookInterface {
    void addNewSingleOrder(Order order);

    void cancelOrder(int orderID);

    /**
     * @param order Order from fix
     */
    void match(Order order);

    /**
     * @param cntEntries the number of aggregated entries -> limits
     * @return MarketDataEntries for fix
     */
    List<Limit> getFirstSellEntries(int cntEntries);

    List<Limit> getFirstBuyEntries(int cntEntries);
}

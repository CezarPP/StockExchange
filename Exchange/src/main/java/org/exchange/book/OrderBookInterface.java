package org.exchange.book;

public interface OrderBookInterface {
    void addNewSingleOrder(Order order);

    void cancelOrder(int orderID);

    /**
     * @param order Order from fix
     * @return returns the matched quantity
     */
    int match(Order order);
}

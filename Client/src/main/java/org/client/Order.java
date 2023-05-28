package org.client;

import org.common.fix.order.Side;
import org.common.symbols.Symbol;

public class Order {
    public int clientOrderId;
    public final Symbol symbol;
    public final float price;
    public int quantity;
    public final Side side;
    public int exchangeOrderID;

    public Order(int clientOrderId, Symbol symbol, float price, int quantity, Side side) {
        this.clientOrderId = clientOrderId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
    }

    public void setExchangeOrderID(int exchangeOrderID) {
        this.exchangeOrderID = exchangeOrderID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

package org.client;

import org.common.fix.order.Side;
import org.common.symbols.Symbol;

public class Order {
    static int crtClientOrderID = 0;
    public final String clientOrderID;
    public final Symbol symbol;
    public final float price;
    public final int quantity;
    public final Side side;
    public String exchangeOrderID;

    public Order(Symbol symbol, float price, int quantity, Side side) {
        crtClientOrderID++;
        this.clientOrderID = String.valueOf(crtClientOrderID);
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
    }

    public void setExchangeOrderID(String exchangeOrderID) {
        this.exchangeOrderID = exchangeOrderID;
    }
}

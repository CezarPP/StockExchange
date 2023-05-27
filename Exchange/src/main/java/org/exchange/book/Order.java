package org.exchange.book;

import org.common.fix.order.Side;
import org.common.symbols.Symbol;

public class Order {
    private int id;
    private final int clientOderId;
    private final int portId;
    private final Symbol symbol;
    private final float price;
    private int quantity;
    private final Side side;
    private final boolean isCancel;

    public Order(int id, int clientOderId, int portId, Symbol symbol, float price, int quantity, Side side) {
        this.id = id;
        this.clientOderId = clientOderId;
        this.portId = portId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.isCancel = false;
    }

    public Order(int id, int clientOderId, int portId, Symbol symbol, float price, int quantity, Side side, boolean isCancel) {
        this.id = id;
        this.clientOderId = clientOderId;
        this.portId = portId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.isCancel = isCancel;
    }

    public int getPortId() {
        return portId;
    }

    public int getClientOderId() {
        return clientOderId;
    }

    public int getClientId() {
        return portId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Side getSide() {
        return side;
    }

    public boolean isCancel() {
        return isCancel;
    }
}

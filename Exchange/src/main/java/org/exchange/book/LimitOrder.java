package org.exchange.book;

public class LimitOrder {
    private final int id;
    private int quantity;
    private LimitOrder nxt, prev;

    public LimitOrder(int id, int quantity, LimitOrder prev, LimitOrder nxt) {
        this.id = id;
        this.quantity = quantity;
        this.nxt = nxt;
        this.prev = prev;
    }

    public LimitOrder(Order order, LimitOrder prev, LimitOrder nxt) {
        this.id = order.id();
        this.quantity = order.quantity();
        this.prev = prev;
        this.nxt = nxt;
    }

    /**
     * @param quantity -> how much to decrease
     * @return returns the amount the quantity was actually decreased by
     */
    int decreaseQuantity(int quantity) {
        if (quantity > this.quantity) {
            int tmp = this.quantity;
            this.quantity = 0;
            return tmp;
        } else {
            this.quantity -= quantity;
            return quantity;
        }
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public LimitOrder getNxt() {
        return nxt;
    }

    public LimitOrder getPrev() {
        return prev;
    }

    public void setNxt(LimitOrder nxt) {
        this.nxt = nxt;
    }

    public void setPrev(LimitOrder prev) {
        this.prev = prev;
    }
}

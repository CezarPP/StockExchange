package org.exchange.book;

public class LimitOrder {
    private final int id;
    private int quantity;
    private LimitOrder nxt, prev;
    private final Limit parentLimit;

    public LimitOrder(int id, int quantity, LimitOrder prev, LimitOrder nxt, Limit parentLimit) {
        this.id = id;
        this.quantity = quantity;
        this.nxt = nxt;
        this.prev = prev;
        this.parentLimit = parentLimit;
    }

    public LimitOrder(Order order, LimitOrder prev, LimitOrder nxt, Limit parentLimit) {
        this.id = order.getId();
        this.quantity = order.getQuantity();
        this.prev = prev;
        this.nxt = nxt;
        this.parentLimit = parentLimit;
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

    public Limit getParentLimit() {
        return parentLimit;
    }
}

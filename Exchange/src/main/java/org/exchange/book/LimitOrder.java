package org.exchange.book;

public class LimitOrder {
    public final Order order;
    private LimitOrder nxt, prev;
    private final Limit parentLimit;

    public LimitOrder(Order order, LimitOrder prev, LimitOrder nxt, Limit parentLimit) {
        this.order = order;
        this.nxt = nxt;
        this.prev = prev;
        this.parentLimit = parentLimit;
    }

    /**
     * @param quantity -> how much to decrease
     * @return returns the amount the quantity was actually decreased by
     */
    int decreaseQuantity(int quantity) {
        if (quantity > this.order.getQuantity()) {
            int tmp = this.order.getQuantity();
            this.order.setQuantity(0);
            return tmp;
        } else {
            this.order.setQuantity(this.order.getQuantity() - quantity);
            return quantity;
        }
    }

    public int getId() {
        return order.getId();
    }

    public int getQuantity() {
        return order.getQuantity();
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

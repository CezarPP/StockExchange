package org.exchange;

import java.util.Iterator;

public class Limit implements Iterable<LimitOrder> {
    final int price;

    /**
     * head and tail of the linked list of limit orders
     */
    LimitOrder head, tail;

    Limit(int price) {
        this.price = price;
        head = tail = null;
    }

    Limit(int price, LimitOrder order) {
        this.price = price;
        head = tail = order;
    }

    void addOrder(Order order) {
        if (tail == null) {
            head = tail = new LimitOrder(order, null, null);
        } else {
            LimitOrder newLimitOrder = new LimitOrder(order, tail, null);
            tail.setNxt(newLimitOrder);
            tail = newLimitOrder;
        }
    }

    void removeOrder(LimitOrder order) {
        if (order.getPrev() != null) {
            order.getPrev().setNxt(order.getNxt());
        }
        if (order.getNxt() != null) {
            order.getNxt().setPrev(order.getPrev());
        }
    }

    LimitOrder getFirstOrder() {
        return head;
    }

    @Override
    public Iterator<LimitOrder> iterator() {
        return new Iterator<>() {
            private LimitOrder current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public LimitOrder next() {
                LimitOrder temp = current;
                current = current.getNxt();
                return temp;
            }
        };
    }
}
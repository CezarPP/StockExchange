package org.exchange.book;

import java.util.Iterator;

public class Limit implements Iterable<LimitOrder> {
    private final int price;

    /**
     * head and tail of the linked list of limit orders
     */
    private LimitOrder head, tail;

    private final Side side;

    Limit(int price, Side side) {
        this.price = price;
        head = tail = null;
        this.side = side;
    }

    Limit(int price, LimitOrder order, Side side) {
        this.price = price;
        head = tail = order;
        this.side = side;
    }

    LimitOrder addOrder(Order order) {
        if (tail == null) {
            head = tail = new LimitOrder(order, null, null, this);
        } else {
            LimitOrder newLimitOrder = new LimitOrder(order, tail, null, this);
            tail.setNxt(newLimitOrder);
            tail = newLimitOrder;
        }
        return tail;
    }

    public boolean isEmpty() {
        return (head == null);
    }

    void removeOrder(LimitOrder order) {
        if (order.getPrev() != null) {
            order.getPrev().setNxt(order.getNxt());
        }
        if (order.getNxt() != null) {
            order.getNxt().setPrev(order.getPrev());
        }
        if (order == head) {
            head = order.getNxt();
        }
        if (head == null)
            head = tail;
    }

    void removeFirstOrder() {
        head = head.getNxt();
        if (head == null)
            tail = null;
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

    public Side getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }
}
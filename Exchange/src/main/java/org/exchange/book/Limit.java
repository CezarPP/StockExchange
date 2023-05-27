package org.exchange.book;

import org.common.fix.market_data.MarketDataEntry;
import org.common.fix.market_data.MarketDataEntryType;

import java.util.Iterator;

public class Limit implements Iterable<LimitOrder> {
    private final float price;

    /**
     * head and tail of the linked list of limit orders
     */
    private LimitOrder head, tail;

    private final Side side;

    public Limit(float price, Side side) {
        this.price = price;
        head = tail = null;
        this.side = side;
    }

    public Limit(int price, LimitOrder order, Side side) {
        this.price = price;
        head = tail = order;
        this.side = side;
    }

    public LimitOrder addOrder(Order order) {
        assert (order.side() == this.side);
        assert (order.price() == this.price);
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

    public void removeOrder(LimitOrder order) {
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
            tail = null;
    }

    public void removeFirstOrder() {
        head = head.getNxt();
        if (head == null)
            tail = null;
    }

    public LimitOrder getFirstOrder() {
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

    /**
     * Computes the aggregate of the limit
     *
     * @param position -> the position in the order book
     * @return -> a MarketDataEntry for fix
     */
    public MarketDataEntry getMarketDataEntry(int position) {
        int quantity = 0, nrOrders = 0;
        for (LimitOrder limitOrder : this) {
            quantity += limitOrder.getQuantity();
            nrOrders++;
        }
        if (side == Side.BUY) {
            return new MarketDataEntry(MarketDataEntryType.BID, price, quantity, nrOrders, position);
        }
        return new MarketDataEntry(MarketDataEntryType.OFFER, price, quantity, nrOrders, position);
    }

    public Side getSide() {
        return side;
    }

    public float getPrice() {
        return price;
    }
}
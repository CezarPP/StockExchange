package org.exchange.book;

import org.common.fix.order.OrderStatus;
import org.common.fix.order.Side;
import org.common.symbols.Symbol;
import org.exchange.broadcast.BroadcastSender;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OrderBook extends Thread implements OrderBookInterface {
    private final Symbol symbol;
    private final LimitsMap<Float, Limit> askLimits, bidLimits;
    private final HashMap<Integer, LimitOrder> orders;
    static int orderId = 0;
    static int execId = 0;
    private final Queue<Order> queue = new ConcurrentLinkedQueue<>();

    OrderBook(Symbol symbol) {
        this.symbol = symbol;
        askLimits = new LimitsTreeMap<>(); // ascending
        bidLimits = new LimitsTreeMap<Float, Limit>(Collections.reverseOrder()); // descending
        orders = new HashMap<>();
    }

    public synchronized int addToQueue(Order order) {
        if (!order.isCancel()) {
            orderId++;
            order.setId(orderId);
            queue.add(order);
            return orderId;
        }
        queue.add(order);
        return 0;
    }

    private Limit getFirstLimit(LimitsMap<Float, Limit> limitTree) {
        var firstEntry = limitTree.firstEntry();
        if (firstEntry == null)
            return null;
        return firstEntry.getValue();
    }

    private void addNewSingleOrderToMap(Order order, LimitsMap<Float, Limit> limitTree, Side side) {
        int initialQty = order.getQuantity();
        match(order);

        if (order.getQuantity() == 0) {
            BroadcastSender
                    .sendOrderTrade(order, ++execId, OrderStatus.FILLED);
            return;
        } else if (order.getQuantity() != initialQty) {
            BroadcastSender
                    .sendOrderTrade(order, ++execId, OrderStatus.PARTIALLY_FILLED);
        }
        Limit limit = getLimit(order.getPrice(), limitTree);
        if (limit == null) {
            limit = new Limit(order.getPrice(), side);
            addLimit(limitTree, limit);
        }
        orders.put(order.getId(), limit.addOrder(order));
    }

    @Override
    public void addNewSingleOrder(Order order) {
        assert (order.getSymbol() == this.getSymbol());
        BroadcastSender
                .sendOrderReceiveConfirmation(order, ++execId);
        if (order.getSide() == Side.BUY) {
            addNewSingleOrderToMap(order, bidLimits, Side.BUY);
        } else {
            addNewSingleOrderToMap(order, askLimits, Side.SELL);
        }
    }

    @Override
    public void cancelOrder(int orderID) {
        LimitOrder order = orders.get(orderID);
        Limit parentLimit = order.getParentLimit();
        parentLimit.removeOrder(order);
        if (parentLimit.isEmpty()) {
            if (parentLimit.getSide() == Side.BUY)
                bidLimits.remove(parentLimit.getPrice());
            else
                askLimits.remove(parentLimit.getPrice());
        }
        // TODO(send client message)
    }

    @Override
    public void match(Order order) {
        if (order.getSide() == Side.BUY)
            matchBuyOrder(order);
        else
            matchSellOrder(order);
    }

    /**
     * @param order -> order
     */
    private void matchBuyOrder(Order order) {
        int remainingQuantity = order.getQuantity();
        Limit firstLimit = getFirstLimit(askLimits);

        while (remainingQuantity > 0 &&
                firstLimit != null && firstLimit.getPrice() <= order.getPrice()) {
            for (LimitOrder crtMarketableOrder = firstLimit.getFirstOrder();
                 remainingQuantity > 0 && crtMarketableOrder != null;
                 crtMarketableOrder = firstLimit.getFirstOrder()) {

                remainingQuantity -= crtMarketableOrder.decreaseQuantity(remainingQuantity);
                if (crtMarketableOrder.getQuantity() == 0) {
                    BroadcastSender
                            .sendOrderTrade(crtMarketableOrder.order, ++execId, OrderStatus.FILLED);
                    firstLimit.removeFirstOrder();
                } else {
                    BroadcastSender
                            .sendOrderTrade(crtMarketableOrder.order, ++execId, OrderStatus.PARTIALLY_FILLED);
                }
            }
            if (firstLimit.getFirstOrder() == null)
                removeLimit(askLimits, firstLimit.getPrice());
            firstLimit = getFirstLimit(askLimits);
        }
        order.setQuantity(remainingQuantity);
    }

    private void matchSellOrder(Order order) {
        int remainingQuantity = order.getQuantity();
        Limit firstLimit = getFirstLimit(bidLimits);

        while (remainingQuantity > 0 &&
                firstLimit != null && firstLimit.getPrice() >= order.getPrice()) {

            for (LimitOrder crtMarketableOrder = firstLimit.getFirstOrder();
                 remainingQuantity > 0 && crtMarketableOrder != null;
                 crtMarketableOrder = firstLimit.getFirstOrder()) {

                remainingQuantity -= crtMarketableOrder.decreaseQuantity(remainingQuantity);
                if (crtMarketableOrder.getQuantity() == 0) {
                    BroadcastSender
                            .sendOrderTrade(crtMarketableOrder.order, ++execId, OrderStatus.FILLED);
                    firstLimit.removeFirstOrder();
                } else {
                    BroadcastSender
                            .sendOrderTrade(crtMarketableOrder.order, ++execId, OrderStatus.PARTIALLY_FILLED);
                }
            }
            if (firstLimit.getFirstOrder() == null)
                removeLimit(bidLimits, firstLimit.getPrice());
            firstLimit = getFirstLimit(bidLimits);
        }
        order.setQuantity(remainingQuantity);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    private Limit getLimit(Float price, LimitsMap<Float, Limit> limitTree) {
        return limitTree.get(price);
    }

    private void addLimit(LimitsMap<Float, Limit> limits, Limit limit) {
        limits.put(limit.getPrice(), limit);
    }

    private void removeLimit(LimitsMap<Float, Limit> limits, float limitPrice) {
        limits.remove(limitPrice);
    }

    public Limit getFirstBuyLimit() {
        return getFirstLimit(bidLimits);
    }

    public Limit getFirstSellLimit() {
        return getFirstLimit(askLimits);
    }


    @Override
    public List<Limit> getFirstSellEntries(int cntEntries) {
        return askLimits.getFirstN(cntEntries);
    }

    @Override
    public List<Limit> getFirstBuyEntries(int cntEntries) {
        return bidLimits.getFirstN(cntEntries);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (queue.isEmpty())
                    continue;
                Order order = queue.poll();
                assert order != null;
                if (order.isCancel()) {
                    this.cancelOrder(order.getId());
                } else {
                    this.addNewSingleOrder(order);
                }
            } catch (Exception e) {
                System.out.println("Error waiting for element in orderBook");
                throw new RuntimeException(e);
            }
        }
    }
}

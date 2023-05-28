package org.exchange.book;

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
        int remainingQuantity = match(order);
        if (remainingQuantity == 0)
            return;
        order.setQuantity(remainingQuantity);
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
        if (order.getSide() == Side.BUY) {
            addNewSingleOrderToMap(order, bidLimits, Side.BUY);
        } else {
            addNewSingleOrderToMap(order, askLimits, Side.SELL);
        }
/*        SimpleServer
                .getPortForClient(order.getClientId())
                .fixEnginePort
                .sendOrderReceiveConfirmation(order, ++execId);*/
        BroadcastSender
                .sendOrderReceiveConfirmation(order, ++execId);
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
    public int match(Order order) {
        return (order.getSide() == Side.BUY) ? matchBuyOrder(order) : matchSellOrder(order);
    }

    /**
     * @param order -> order
     * @return -> remaining quantity
     */
    private int matchBuyOrder(Order order) {
        // TODO(send client messages)

        int remainingQuantity = order.getQuantity();
        Limit firstLimit = getFirstLimit(askLimits);
        while (remainingQuantity > 0 &&
                firstLimit != null && firstLimit.getPrice() <= order.getPrice()) {
            for (LimitOrder crtMarketableOrder = firstLimit.getFirstOrder();
                 remainingQuantity > 0 && crtMarketableOrder != null;
                 crtMarketableOrder = firstLimit.getFirstOrder()) {

                remainingQuantity -= crtMarketableOrder.decreaseQuantity(remainingQuantity);
                if (crtMarketableOrder.getQuantity() == 0)
                    firstLimit.removeFirstOrder();
            }
            if (firstLimit.getFirstOrder() == null)
                removeLimit(askLimits, firstLimit.getPrice());
            firstLimit = getFirstLimit(askLimits);
        }

        return remainingQuantity;
    }

    private int matchSellOrder(Order order) {
        //TODO(send client messages)
        int remainingQuantity = order.getQuantity();
        Limit firstLimit = getFirstLimit(bidLimits);
        while (remainingQuantity > 0 &&
                firstLimit != null && firstLimit.getPrice() >= order.getPrice()) {
            for (LimitOrder crtMarketableOrder = firstLimit.getFirstOrder();
                 remainingQuantity > 0 && crtMarketableOrder != null;
                 crtMarketableOrder = firstLimit.getFirstOrder()) {

                remainingQuantity -= crtMarketableOrder.decreaseQuantity(remainingQuantity);
                if (crtMarketableOrder.getQuantity() == 0)
                    firstLimit.removeFirstOrder();
            }
            if (firstLimit.getFirstOrder() == null)
                removeLimit(bidLimits, firstLimit.getPrice());
            firstLimit = getFirstLimit(bidLimits);
        }

        return remainingQuantity;
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

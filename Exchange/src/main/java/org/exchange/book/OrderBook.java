package org.exchange.book;

import org.common.symbols.Symbol;

import java.util.*;

public class OrderBook implements OrderBookInterface {
    private final Symbol symbol;
    private final LimitsMap<Integer, Limit> askLimits, bidLimits;
    private final HashMap<Integer, LimitOrder> orders;

    OrderBook(Symbol symbol) {
        this.symbol = symbol;
        askLimits = new LimitsTreeMap<>(); // ascending
        bidLimits = new LimitsTreeMap<Integer, Limit>(Collections.reverseOrder()); // descending
        orders = new HashMap<>();
    }

    private Limit getFirstLimit(LimitsMap<Integer, Limit> limitTree) {
        var firstEntry = limitTree.firstEntry();
        if (firstEntry == null)
            return null;
        return firstEntry.getValue();
    }

    private void addNewSingleOrderToMap(Order _order, LimitsMap<Integer, Limit> limitTree, Side side) {
        int remainingQuantity = match(_order);
        if (remainingQuantity == 0)
            return;
        Order order = new Order(_order.id(), _order.symbol(), _order.price(), remainingQuantity, _order.side());
        Limit limit = getLimit(order.price(), limitTree);
        if (limit == null) {
            limit = new Limit(order.price(), side);
            addLimit(limitTree, limit);
        }
        orders.put(order.id(), limit.addOrder(order));
    }

    @Override
    public void addNewSingleOrder(Order order) {
        assert (order.symbol() == this.getSymbol());
        if (order.side() == Side.BUY) {
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
    public int match(Order order) {
        return (order.side() == Side.BUY) ? matchBuyOrder(order) : matchSellOrder(order);
    }

    /**
     * @param order -> order
     * @return -> remaining quantity
     */
    private int matchBuyOrder(Order order) {
        // TODO(send client messages)

        int remainingQuantity = order.quantity();
        Limit firstLimit = getFirstLimit(askLimits);
        while (remainingQuantity > 0 &&
                firstLimit != null && firstLimit.getPrice() <= order.price()) {
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
        int remainingQuantity = order.quantity();
        Limit firstLimit = getFirstLimit(bidLimits);
        while (remainingQuantity > 0 &&
                firstLimit != null && firstLimit.getPrice() >= order.price()) {
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

    private Limit getLimit(Integer price, LimitsMap<Integer, Limit> limitTree) {
        return limitTree.get(price);
    }

    private void addLimit(LimitsMap<Integer, Limit> limits, Limit limit) {
        limits.put(limit.getPrice(), limit);
    }

    private void removeLimit(LimitsMap<Integer, Limit> limits, int limitPrice) {
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
}

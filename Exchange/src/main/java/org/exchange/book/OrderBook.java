package org.exchange.book;

import org.common.symbols.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class OrderBook implements OrderBookInterface {
    private final Symbol symbol;
    private final TreeMap<Integer, Limit> askLimits, bidLimits;
    private final HashMap<Integer, LimitOrder> orders;

    OrderBook(Symbol symbol) {
        this.symbol = symbol;
        askLimits = new TreeMap<>();
        bidLimits = new TreeMap<>();
        orders = new HashMap<>();
    }

    public Symbol getSymbol() {
        return symbol;
    }

    private Limit getLimit(Integer price, TreeMap<Integer, Limit> limitTree) {
        return limitTree.get(price);
    }

    private Limit getFirstLimit(TreeMap<Integer, Limit> limitTree) {
        Map.Entry<Integer, Limit> firstEntry = limitTree.firstEntry();
        if (firstEntry == null)
            return null;
        return firstEntry.getValue();
    }

    void addLimit(TreeMap<Integer, Limit> limits, Limit limit) {
        limits.put(limit.price, limit);
    }

    private void addNewSingleOrderWithMap(Order order, TreeMap<Integer, Limit> limitTree) {
        int remainingQuantity = match(order);
        if (remainingQuantity == order.quantity())
            return;
        Limit limit = getLimit(order.price(), limitTree);
        if (limit == null) {
            limit = new Limit(order.price(), new LimitOrder(order, null, null));
            addLimit(limitTree, limit);
        } else {
            limit.addOrder(order);
        }
        // TODO(add to orders map)
    }

    @Override
    public void addNewSingleOrder(Order order) {
        if (order.side() == Side.BUY) {
            addNewSingleOrderWithMap(order, bidLimits);
        } else {
            addNewSingleOrderWithMap(order, askLimits);
        }
    }

    @Override
    public void cancelOrder(int orderID) {
        //TODO()
    }

    @Override
    public int match(Order order) {
        return (order.side() == Side.BUY) ? matchBuyOrder(order) : matchSellOrder(order);
    }

    private int matchBuyOrder(Order order) {
        //TODO()
        return 0;
/*        int remainingQuantity = order.quantity();
        Limit limit = getFirstLimit(askLimits);
        while(remainingQuantity > 0 &&
                limit != null && limit.price < order.price()) {

            while(remainingQuantity > 0 && limit.getFirstOrder() != null) {
                LimitOrder firstOrder = limit.getFirstOrder();
                
            }

            limit = getFirstLimit(askLimits);
        }

            return order.quantity() - remainingQuantity;*/
    }

    private int matchSellOrder(Order order) {
        //TODO()
        return 0;
    }
}

package org.common.fix.body;

import org.common.fix.FixMessage;
import org.common.fix.order.*;
import org.common.symbols.Symbol;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * All fields are required
 */
public class FixBodyCancel implements FixBody {
    /**
     * 41 -> OrigClOrdID -> ClOrdID <11> of the previous order
     */
    public String origClientOrderID;

    /**
     * 37 -> OrderID -> Unique identifier of most recent order as assigned by sell-side (broker, exchange, ECN)
     */

    public String orderID;

    /**
     * 11 -> Assigned by the institution
     */
    public String clientOrderID;

    /**
     * 55 -> Symbol
     */
    public Symbol symbol;
    /**
     * 54 -> Side
     */
    public Side side;
    /**
     * 60 -> Time this order request was initiated/released by the trader, trading system, or intermediary.
     */
    public OffsetDateTime transactTime;
    /**
     * 38 -> OrderQty
     */
    public int orderQuantity;

    public FixBodyCancel(String origClientOrderID, String orderID, String clientOrderID, Symbol symbol, Side side, OffsetDateTime transactTime, int orderQuantity) {
        this.origClientOrderID = origClientOrderID;
        this.orderID = orderID;
        this.clientOrderID = clientOrderID;
        this.symbol = symbol;
        this.side = side;
        this.transactTime = transactTime;
        this.orderQuantity = orderQuantity;
    }

    @Override
    public String toString() {
        return "41=" + origClientOrderID + FixMessage.delimiter +
                "37=" + orderID + FixMessage.delimiter +
                "11=" + clientOrderID + FixMessage.delimiter +
                "55=" + symbol + FixMessage.delimiter +
                "54=" + side + FixMessage.delimiter +
                "60=" + transactTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + FixMessage.delimiter +
                "38=" + orderQuantity + FixMessage.delimiter;
    }

    public static FixBodyCancel fromString(String str) {
        Map<String, String> map = new HashMap<>();
        String[] parts = str.split(FixMessage.delimiter);
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("FixBodyCancel is invalid");
            }
            map.put(keyValue[0], keyValue[1]);
        }

        return new FixBodyCancel(
                map.get("41"),
                map.get("37"),
                map.get("11"),
                Symbol.fromValue(map.get("55")),
                Side.fromValue(map.get("54").charAt(0)),
                OffsetDateTime.parse(map.get("60"), DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                Integer.parseInt(map.get("38"))
        );
    }
}



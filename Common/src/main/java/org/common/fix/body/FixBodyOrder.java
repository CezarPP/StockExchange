package org.common.fix.body;

import org.common.fix.FixMessage;
import org.common.fix.order.*;
import org.common.symbols.Symbol;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FixBodyOrder implements FixBody {
    /**
     * 11 -> Required -> Buy orders -> assigned by the institution
     */
    public String clientOrderID;

    /**
     * 55 -> Required -> Buy and Cancel orders
     */
    public Symbol symbol;
    /**
     * 54 -> Required -> Buy and Cancel orders
     */
    public Side side;
    /**
     * 60 -> Required -> Time this order request was initiated/released by the trader, trading system, or intermediary.
     */
    public OffsetDateTime transactTime;
    /**
     * 40 -> Required -> Buy orders
     * Only use Limit
     */
    public OrderType orderType;
    /**
     * 38 -> Required -> Buy and Cancel orders
     */
    public int orderQuantity;

    /**
     * 423 -> PriceType -> int -> Code to represent the price type.
     */
    PriceType priceType;

    /**
     * 44 -> Price -> float -> Required for limit orders
     */
    public float price;

    public FixBodyOrder(String clientOrderID, Symbol symbol, Side side, OffsetDateTime transactTime, OrderType orderType, int orderQuantity, PriceType priceType, float price) {
        this.clientOrderID = clientOrderID;
        this.symbol = symbol;
        this.side = side;
        this.transactTime = transactTime;
        this.orderType = orderType;
        this.orderQuantity = orderQuantity;
        this.priceType = priceType;
        this.price = price;
    }

    @Override
    public String toString() {
        return "11=" + clientOrderID + FixMessage.delimiter +
                "55=" + symbol + FixMessage.delimiter +
                "54=" + side + FixMessage.delimiter +
                "60=" + transactTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + FixMessage.delimiter +
                "40=" + orderType + FixMessage.delimiter +
                "38=" + orderQuantity + FixMessage.delimiter +
                "423=" + priceType + FixMessage.delimiter +
                "44=" + price + FixMessage.delimiter;
    }

    public static FixBodyOrder fromString(String str) {
        Map<String, String> map = new HashMap<>();
        String[] parts = str.split(FixMessage.delimiter);
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("FixBodyOrder string is not valid");
            }
            map.put(keyValue[0], keyValue[1]);
        }

        return new FixBodyOrder(
                map.get("11"),
                Symbol.fromValue(map.get("55")),
                Side.fromValue(map.get("54").charAt(0)),
                OffsetDateTime.parse(map.get("60"), DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                OrderType.fromValue(map.get("40").charAt(0)),
                Integer.parseInt(map.get("38")),
                PriceType.fromValue(Integer.parseInt(map.get("423"))),
                Float.parseFloat(map.get("44"))
        );
    }
}



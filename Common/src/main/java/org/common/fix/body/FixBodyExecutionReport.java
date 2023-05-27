package org.common.fix.body;

import org.common.fix.FixMessage;
import org.common.fix.order.Side;
import org.common.fix.order.ExecType;
import org.common.fix.order.OrderStatus;
import org.common.symbols.Symbol;

public class FixBodyExecutionReport implements FixBody {
    /**
     * 37 -> OrderID -> as assigned by the exchange
     */
    public String orderID;

    /**
     * 41 -> Required -> Cancel orders
     * ClOrdID of the previous non-rejected order (NOT the initial order of the day) when canceling or replacing an order.
     */
    public String origClientOrderID;

    /**
     * 17 -> ExecID	-> String -> Unique identifier of execution message as assigned by exchange
     */
    public String execId;

    /**
     * 150 -> ExecType -> char -> Describes the purpose of the execution report
     */
    public ExecType execType;

    /**
     * 39 -> OrdStatus -> String -> Describes the current state of a CHAIN of orders
     */
    public OrderStatus orderStatus;

    /**
     * 55 -> Symbol -> String
     */
    public Symbol symbol;

    /**
     * 54 -> Side -> char
     */
    public Side side;

    /**
     * 44 -> Price -> float -> Required if specified on the order
     */
    public float price;

    /**
     * 151 -> LeavesQty	-> Quantity open for further execution, can be 0 for Canceled, DoneForTheDay, Expired, Calculated, or Rejected
     */
    public int leavesQuantity;

    /**
     * 14 -> CumQty	-> Currently executed quantity for chain of orders
     */
    public int cumQty;

    /**
     * 6 -> AvgPx -> Calculated average price of all fills on this order
     */
    public float avgPrice;

    public FixBodyExecutionReport() {

    }

    public FixBodyExecutionReport(String orderID, String origClientOrderID, String execId, ExecType execType, OrderStatus orderStatus, Symbol symbol, Side side, float price, int leavesQuantity, int cumQty, float avgPrice) {
        this.orderID = orderID;
        this.origClientOrderID = origClientOrderID;
        this.execId = execId;
        this.execType = execType;
        this.orderStatus = orderStatus;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.leavesQuantity = leavesQuantity;
        this.cumQty = cumQty;
        this.avgPrice = avgPrice;
    }

    @Override
    public String toString() {
        return "37=" + orderID + FixMessage.delimiter +
                "41=" + origClientOrderID + FixMessage.delimiter +
                "17=" + execId + FixMessage.delimiter +
                "150=" + execType + FixMessage.delimiter +
                "39=" + orderStatus + FixMessage.delimiter +
                "55=" + symbol + FixMessage.delimiter +
                "54=" + side + FixMessage.delimiter +
                "44=" + price + FixMessage.delimiter +
                "151=" + leavesQuantity + FixMessage.delimiter +
                "14=" + cumQty + FixMessage.delimiter +
                "6=" + avgPrice + FixMessage.delimiter;
    }

    public static FixBodyExecutionReport fromString(String s) {
        String[] fields = s.split(FixMessage.delimiter);
        FixBodyExecutionReport report = new FixBodyExecutionReport();

        for (String field : fields) {
            String[] keyValue = field.split("=");
            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "37" -> report.orderID = value;
                case "41" -> report.origClientOrderID = value;
                case "17" -> report.execId = value;
                case "150" -> report.execType = ExecType.fromValue(value.charAt(0));
                case "39" -> report.orderStatus = OrderStatus.fromValue(value.charAt(0));
                case "55" -> report.symbol = Symbol.fromValue(value);
                case "54" -> report.side = Side.fromValue(value.charAt(0));
                case "44" -> report.price = Float.parseFloat(value);
                case "151" -> report.leavesQuantity = Integer.parseInt(value);
                case "14" -> report.cumQty = Integer.parseInt(value);
                case "6" -> report.avgPrice = Float.parseFloat(value);
                default -> throw new IllegalArgumentException("Unknown key exec report: " + key);
            }
        }
        return report;
    }
}

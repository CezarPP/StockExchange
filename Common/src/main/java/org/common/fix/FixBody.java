package org.common.fix;

import java.time.OffsetDateTime;

public class FixBody {
    /**
     * 11 -> Required -> Buy orders -> assigned by the institution
     */
    String clientOrderID;

    /**
     * 41 -> Required  -> Cancel orders
     * ClOrdID of the previous non-rejected order (NOT the initial order of the day) when canceling or replacing an order.
     */
    String origClientOrderID;

    /**
     * Required -> Confirmations, Execution reports -> Unique identifier for Order as assigned by exchange
     */
    String orderID;
    /**
     * TODO()
     */
    OrderStatus orderStatus;
    /**
     * TODO()
     */
    String execID;
    /**
     * //TODO()
     */
    ExecTransType execTransType;
    /**
     * TODO() for server responses
     */
    ExecType execType;
    /**
     * 55 -> Required -> Buy and Cancel orders
     */
    String symbol;
    /**
     * 54 -> Required -> Buy and Cancel orders
     */
    Side side;
    /**
     * 60 -> Required -> Time this order request was initiated/released by the trader, trading system, or intermediary.
     */
    OffsetDateTime transactTime;
    /**
     * 40 -> Required -> Buy orders
     * Only use Limit
     */
    OrderType orderType;
    /**
     * 38 -> Required -> Buy and Cancel orders
     */
    int orderQuantity;
    /**
     * TODO()
     */
    int price;

    public FixBody() {
        //TODO()
    }

    @Override
    public String toString() {
        //TODO();
        return "";
    }

}


enum OrderStatus {
    New("0"),
    PartiallyFilled("1"),
    Filled("2"),
    DoneForDay("3"),
    Canceled("4"),
    Replaced("5"),
    PendingCancel("6"),
    Stopped("7"),
    Rejected("8"),
    Suspended("9"),
    PendingNew("A"),
    Calculated("B"),
    Expired("C"),
    AcceptedForBidding("D"),
    PendingReplace("E");
    final public String label;

    OrderStatus(String label) {
        this.label = label;
    }
}

enum ExecTransType {
    New("0"),
    Cancel("1"),
    Correct("2"),
    Status("3");
    public final String label;

    ExecTransType(String label) {
        this.label = label;
    }
}

enum ExecType {
    New("0"),
    DoneForDay("3"),
    Canceled("4"),
    Replaced("5"),
    PendingCancel("6"),
    Stopped("7"),
    Rejected("8"),
    Suspended("9"),
    PendingNew("A"),
    Calculated("B"),
    Expired("C"),
    Restated("D"),
    PendingReplace("E"),
    Trade("F"),
    TradeCorrect("G"),
    TradeCancel("H"),
    OrderStatus("I");
    public final String label;

    ExecType(String label) {
        this.label = label;
    }
}

enum Side {
    Buy("1"),
    Sell("2");
    public final String label;

    Side(String label) {
        this.label = label;
    }
}

enum OrderType {
    Market("1"),
    Limit("2"),
    Stop("3"),
    StopLimit("4"),
    WithOrWithout("6"),
    LimitOrBetter("7"),
    LimitWithOrWithout("8"),
    OnBasis("9"),
    PreviouslyQuoted("D"),
    PreviouslyIndicated("E"),
    ForexSwap("G"),
    Funari("I"),
    MarketIfTouched("J"),
    MarketWithLeftoverAsLimit("K"),
    PreviousFundValuationPoint("L"),
    NextFundValuationPoint("M"),
    Pegged("P");

    final public String label;

    OrderType(String label) {
        this.label = label;
    }
}
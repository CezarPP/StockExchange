package org.common.fix.header;

public enum MessageType {
    HeartBeat("0"),
    TestRequest("1"),
    ResendRequest("2"),
    Reject("3"),
    SequenceReset("4"),
    Logout("5"),
    IndicationOfInterest("6"),
    Advertisement("7"),
    ExecutionReport("8"),
    OrderCancelReject("9"),
    Logon("A"),
    News("B"),
    Email("C"),
    NewOrderSingle("D"),
    NewOrderList("E"),
    OrderCancelRequest("F"),
    OrderCancelReplaceRequest("G"),
    OrderStatusRequest("H"),
    Allocation("J"),
    ListCancelRequest("K"),
    ListExecute("L"),
    ListStatusRequest("M"),
    ListStatus("N"),
    AllocationAck("P"),
    DontKnowTrade("Q"),
    QuoteRequest("R"),
    Quote("S"),
    SettlementInstructions("T"),
    MarketDataRequest("V"),
    MarketDataSnapshotFullRefresh("W"),
    MarketDataIncrementRefresh("X"),
    MarketDataRquestReject("Y"),
    QuoteCancel("Z"),
    QuoteStatusReject("a"),
    QuoteAcknowledgement("b"),
    SecurityDefinitionRequest("c"),
    SecurityDefinition("d"),
    SecurityStatusRequest("e"),
    SecurityStatus("f"),
    TradingSessionStatusRequest("g"),
    TradingSessionStatus("h"),
    MassQuote("i"),
    BusinessMessageReject("j"),
    BidRequest("k"),
    BidResponse("l"),
    ListStrikePrice("m");
    public final String label;

    MessageType(String label) {
        this.label = label;
    }

    public static MessageType fromValue(String value) {
        for (MessageType messageType : values()) {
            if (messageType.label.equals(value)) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Unknown MessageType: " + value);
    }

    @Override
    public String toString() {
        return this.label;
    }
}

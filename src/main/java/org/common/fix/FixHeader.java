package org.common.fix;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * All fields are required
 */
public class FixHeader {

    /**
     * 8 -> BeginString -> FIX.4.4 -> Always unencrypted, must be first field in message
     */
    public BeginString beginString;

    /**
     * 9 -> BodyLength -> int -> Always unencrypted, must be second field in message
     */
    public int bodyLength;
    /**
     * 35 -> MsgType -> MessageType -> Always unencrypted, must be third field in message
     */
    public MessageType messageType;

    /**
     * 49 -> SenderCompID -> String -> Always unencrypted
     */
    public String senderCompID;
    /**
     * 56 -> TargetCompID -> String -> Always unencrypted
     */
    public String targetCompID;
    /**
     * 34 -> MsgSeqNum -> int -> Can be embedded within encrypted data section
     */
    public int messageSeqNum;
    /**
     * 52 -> SendingTime -> OffsetDateTime -> Can be embedded within encrypted data section.
     */
    OffsetDateTime sendingTime;

    private String formattedSendingTime() {
        return sendingTime.format(DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss"));
    }

    public FixHeader(BeginString beginString, int bodyLength, MessageType messageType, String senderCompID,
                     String targetCompID, int messageSeqNum, OffsetDateTime sendingTime) {
        this.beginString = beginString;
        this.bodyLength = bodyLength;
        this.messageType = messageType;
        this.senderCompID = senderCompID;
        this.targetCompID = targetCompID;
        this.messageSeqNum = messageSeqNum;
        this.sendingTime = sendingTime;
    }

    @Override
    public String toString() {
        return "8=" + beginString + FixMessage.delimiter +
                "9=" + bodyLength + FixMessage.delimiter +
                "35=" + messageType + FixMessage.delimiter +
                "49=" + senderCompID + FixMessage.delimiter +
                "56=" + targetCompID + FixMessage.delimiter +
                "34=" + messageSeqNum + FixMessage.delimiter +
                "52=" + formattedSendingTime() + FixMessage.delimiter;
    }
}

enum BeginString {
    Fix_4_4("FIX.4.4");
    public final String label;

    BeginString(String label) {
        this.label = label;
    }
}

enum MessageType {
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
}
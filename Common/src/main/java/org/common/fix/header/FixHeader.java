package org.common.fix.header;

import org.common.fix.FixMessage;

import java.time.OffsetDateTime;
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

    public static FixHeader fromString(String str) {
        String[] parts = str.split(FixMessage.delimiter);

        BeginString beginString = null;
        int bodyLength = -1;
        MessageType messageType = null;
        String senderCompID = null;
        String targetCompID = null;
        int messageSeqNum = 0;
        OffsetDateTime sendingTime = null;

        for (String part : parts) {
            String[] keyValue = part.split("=");
            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "8" -> beginString = BeginString.Fix_4_4;
                case "9" -> bodyLength = Integer.parseInt(value);
                case "35" -> messageType = MessageType.fromValue(value);
                case "49" -> senderCompID = value;
                case "56" -> targetCompID = value;
                case "34" -> messageSeqNum = Integer.parseInt(value);
                case "52" ->
                        sendingTime = OffsetDateTime.parse(value, DateTimeFormatter.ofPattern("uuuuMMdd-HH:mm:ss"));
                default -> throw new IllegalArgumentException("Unknown field in header: " + key);
            }
        }

        if (beginString == null || messageType == null || senderCompID == null
                || targetCompID == null || sendingTime == null || messageSeqNum == 0 || bodyLength < 0) {
            throw new IllegalArgumentException("Required fields missing in header");
        }

        return new FixHeader(beginString, bodyLength, messageType, senderCompID, targetCompID, messageSeqNum, sendingTime);
    }

}


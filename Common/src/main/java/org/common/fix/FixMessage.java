package org.common.fix;

import org.common.fix.body.*;
import org.common.fix.header.FixHeader;
import org.common.fix.header.MessageType;

public record FixMessage(FixHeader header, FixBody body, FixTrailer trailer) {
    public static final String delimiter = "\001";

    @Override
    public String toString() {
        return header.toString() + body.toString() + trailer.toString();
    }

    public static FixMessage fromString(String str) {
        String[] parts = str.split(delimiter);

        // Construct header
        StringBuilder headerStringBuilder = new StringBuilder();
        int i = 0;
        while (!parts[i].startsWith("52")) { // Until we hit the sending time field
            headerStringBuilder.append(parts[i]).append(delimiter);
            i++;
        }

        headerStringBuilder.append(parts[i]).append(delimiter);
        i++;

        // Construct body
        StringBuilder bodyStringBuilder = new StringBuilder();
        // Until we hit the checksum field
        while (!parts[i].startsWith("10")) {
            bodyStringBuilder.append(parts[i]).append(delimiter);
            i++;
        }

        // Last part is the trailer
        String trailerString = parts[i] + delimiter;

        FixHeader header = FixHeader.fromString(headerStringBuilder.toString());
        FixBody body;
        if (header.messageType == MessageType.Logon)
            body = FixBodyLogin.fromString(bodyStringBuilder.toString());
        else if (header.messageType == MessageType.Logout)
            body = FixBodyLogout.fromString(bodyStringBuilder.toString());
        else if (header.messageType == MessageType.NewOrderSingle)
            body = FixBodyOrder.fromString(bodyStringBuilder.toString());
        else if (header.messageType == MessageType.MarketDataRequest)
            body = FixBodyRequest.fromString(bodyStringBuilder.toString());
        else if (header.messageType == MessageType.MarketDataSnapshotFullRefresh) {
            body = FixBodyMarketData.fromString(bodyStringBuilder.toString());
        } else if (header.messageType == MessageType.ExecutionReport) {
            body = FixBodyExecutionReport.fromString(bodyStringBuilder.toString());
        } else if (header.messageType == MessageType.OrderCancelRequest) {
            body = FixBodyCancel.fromString(bodyStringBuilder.toString());
        } else if (header.messageType == MessageType.Reject) {
            body = FixBodyReject.fromString(bodyStringBuilder.toString());
        } else {
            throw new IllegalArgumentException("Unknown message type");
        }

        FixTrailer trailer = FixTrailer.fromString(trailerString);
        if (!FixTrailer.verifyChecksum(header, body, trailer)) {
            throw new IllegalArgumentException("Invalid checksum in FIX message: " + str);
        }

        return new FixMessage(header, body, trailer);
    }

}



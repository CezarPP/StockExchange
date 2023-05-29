package org.exchange.broadcast;

public class MessagePair {
    public final int broadcastId;
    public final String message;

    public MessagePair(int broadcastId, String message) {
        this.broadcastId = broadcastId;
        this.message = message;
    }
}

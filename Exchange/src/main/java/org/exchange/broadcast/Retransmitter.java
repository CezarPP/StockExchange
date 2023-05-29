package org.exchange.broadcast;

import java.net.DatagramSocket;

import static org.exchange.broadcast.BroadcastListener.getNextPacket;

public class Retransmitter extends Thread {
    private static final int PORT = 4445;

    int expectedBroadcastId = 1;

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (true) {
                MessagePair messagePair = getNextPacket(socket);
                assert messagePair != null;

                if (messagePair.broadcastId != expectedBroadcastId) {
                    BroadcastSender
                            .resendBroadcasts(expectedBroadcastId);
                    while (getNextPacket(socket).broadcastId != expectedBroadcastId) {
                    }
                }
                expectedBroadcastId++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

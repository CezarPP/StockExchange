package org.exchange.logger;

import org.common.fix.FixMessage;
import org.exchange.broadcast.MessagePair;

import java.net.DatagramSocket;

import static org.exchange.broadcast.BroadcastListener.getNextPacket;

public class LoggerListener extends Thread {
    private static final int PORT = 4445;

    private final Logger logger;
    private int expectedBroadcastId = 1;

    LoggerListener(Logger logger) {
        this.logger = logger;
    }

    public void run() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (true) {
                MessagePair messagePair = getNextPacket(socket);
                assert messagePair != null;

                while (messagePair.broadcastId != expectedBroadcastId)
                    messagePair = getNextPacket(socket);
                expectedBroadcastId++;

                logger.insertMessage(FixMessage.fromString(messagePair.message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


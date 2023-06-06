package org.exchange.logger;

import org.common.fix.FixMessage;
import org.exchange.broadcast.BroadcastUtil;
import org.exchange.broadcast.MessagePair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class LoggerListener extends Thread {
    private static final int PORT = 4445;
    private final Logger logger;
    private int expectedBroadcastId = 1;

    MulticastSocket socket;

    LoggerListener(Logger logger) {
        this.logger = logger;
        try {
            socket = new MulticastSocket(PORT);
            InetAddress groupAddress = InetAddress.getByName("224.0.0.1");
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

            socket.joinGroup(new InetSocketAddress(groupAddress, PORT), networkInterface);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            MessagePair messagePair = BroadcastUtil.getNextPacket(socket);
            assert messagePair != null;

            while (messagePair.broadcastId != expectedBroadcastId)
                messagePair = BroadcastUtil.getNextPacket(socket);
            expectedBroadcastId++;

            logger.insertMessage(FixMessage.fromString(messagePair.message));
        }
    }
}


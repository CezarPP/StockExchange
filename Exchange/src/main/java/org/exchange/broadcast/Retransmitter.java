package org.exchange.broadcast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Retransmitter extends Thread {
    private static final int PORT = 4445;

    int expectedBroadcastId = 1;

    MulticastSocket socket;

    public Retransmitter() {
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

            if (messagePair.broadcastId != expectedBroadcastId) {
                BroadcastSender
                        .resendBroadcasts(expectedBroadcastId);
                while (BroadcastUtil.getNextPacket(socket).broadcastId != expectedBroadcastId) {
                }
            }
            expectedBroadcastId++;
        }
    }
}

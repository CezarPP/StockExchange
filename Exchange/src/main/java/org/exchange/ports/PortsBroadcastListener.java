package org.exchange.ports;

import org.common.fix.FixMessage;
import org.exchange.broadcast.BroadcastUtil;
import org.exchange.broadcast.MessagePair;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class PortsBroadcastListener extends Thread {
    static private final int PORT = 4445;
    Map<String, FixEnginePort> clientCompIdToEngine = new HashMap<>();
    private int expectedBroadcastId = 1;

    MulticastSocket socket;

    public void addClient(String clientCompId, FixEnginePort fixEnginePort) {
        clientCompIdToEngine.put(clientCompId, fixEnginePort);
    }

    PortsBroadcastListener() {
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

            while (messagePair.broadcastId != expectedBroadcastId) {
                messagePair = BroadcastUtil.getNextPacket(socket);
            }
            expectedBroadcastId++;

            FixMessage message = FixMessage.fromString(messagePair.message);

            FixEnginePort fixEnginePort = clientCompIdToEngine.get(message.header().targetCompID);
            assert fixEnginePort != null;
            fixEnginePort.send(message);
        }
    }
}

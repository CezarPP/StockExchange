package org.exchange.ports;

import org.common.fix.FixMessage;
import org.exchange.broadcast.MessagePair;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import static org.exchange.broadcast.BroadcastListener.getNextPacket;

public class PortsBroadcastListener extends Thread {
    static private final int PORT = 4445;
    Map<String, FixEnginePort> clientCompIdToEngine = new HashMap<>();
    private int expectedBroadcastId = 1;

    public void addClient(String clientCompId, FixEnginePort fixEnginePort) {
        clientCompIdToEngine.put(clientCompId, fixEnginePort);
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (true) {
                MessagePair messagePair = getNextPacket(socket);

                while (messagePair.broadcastId != expectedBroadcastId) {
                    messagePair = getNextPacket(socket);
                }
                expectedBroadcastId++;

                FixMessage message = FixMessage.fromString(messagePair.message);

                FixEnginePort fixEnginePort = clientCompIdToEngine.get(message.header().targetCompID);
                assert fixEnginePort != null;
                fixEnginePort.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

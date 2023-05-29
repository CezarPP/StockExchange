package org.exchange.ports;

import org.common.fix.FixMessage;
import org.exchange.broadcast.MessagePair;

import java.net.DatagramSocket;
import java.util.Objects;

import static org.exchange.broadcast.BroadcastListener.getNextPacket;

public class PortBroadcastListener extends Thread {
    static private final int PORT = 4445;
    private final int clientId;
    private final String clientCompId;
    private final FixEnginePort fixEnginePort;
    private int expectedBroadcastId = 1;

    PortBroadcastListener(int clientId, String clientCompId, FixEnginePort fixEnginePort) {
        this.clientId = clientId;
        this.clientCompId = clientCompId;
        this.fixEnginePort = fixEnginePort;
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
                if (!Objects.equals(message.header().targetCompID, clientCompId))
                    continue;
/*
                MessageType messageType = message.header().messageType;
                if (messageType == MessageType.ExecutionReport) {
                    FixBodyExecutionReport report = FixBodyExecutionReport.fromString(message.body().toString());
                    System.out.println("Fix port received exec report for orderId" + report.orderID);
                } else if (messageType == MessageType.OrderCancelReject) {

                } else {
                    throw new IllegalArgumentException("Post broadcast listener found unknown message type" + messageType);
                }*/
                fixEnginePort.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

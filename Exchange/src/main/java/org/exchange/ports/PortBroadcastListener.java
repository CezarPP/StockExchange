package org.exchange.ports;

import org.common.fix.FixMessage;
import org.common.fix.body.FixBodyExecutionReport;
import org.common.fix.header.MessageType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Objects;

public class PortBroadcastListener extends Thread {
    static private final int PORT = 4445;
    private final int clientId;
    private final String clientCompId;

    private final FixEnginePort fixEnginePort;

    PortBroadcastListener(int clientId, String clientCompId, FixEnginePort fixEnginePort) {
        this.clientId = clientId;
        this.clientCompId = clientCompId;
        this.fixEnginePort = fixEnginePort;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (true) {
                byte[] buffer = new byte[1024];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String fixMessage = new String(packet.getData(), 0, packet.getLength());
                FixMessage message = FixMessage.fromString(fixMessage);
                if (!Objects.equals(message.header().targetCompID, clientCompId))
                    continue;

                MessageType messageType = message.header().messageType;
                if(messageType == MessageType.ExecutionReport) {
                    FixBodyExecutionReport report = FixBodyExecutionReport.fromString(message.body().toString());
                    System.out.println("Fix port received exec report for orderId" + report.orderID);
                    // TODO(check execId)
                    // If execId is in order, forward message to client
                } else if(messageType == MessageType.OrderCancelReject) {

                } else {
                    throw new IllegalArgumentException("Post broadcast listener found unknown message type" + messageType);
                }
                fixEnginePort.send(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

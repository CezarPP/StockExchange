package org.exchange.broadcast;

import org.common.fix.FixMessage;
import org.common.fix.FixTrailer;
import org.common.fix.body.FixBody;
import org.common.fix.body.FixBodyExecutionReport;
import org.common.fix.header.BeginString;
import org.common.fix.header.FixHeader;
import org.common.fix.header.MessageType;
import org.common.fix.order.ExecType;
import org.common.fix.order.OrderStatus;
import org.exchange.book.Order;
import org.exchange.ports.SimpleServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.OffsetDateTime;

public class BroadcastSender {
    private static final int PORT = 4445;
    private static boolean isActive = true;

    public static void sendBroadcast(String message) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] buffer = message.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), PORT);
            socket.send(packet);
            socket.close();
        } catch (IOException exception) {
            System.out.println("Error sending broadcast");
            System.exit(-1);
        }
    }

    public static void setIsActive(boolean isActive) {
        BroadcastSender.isActive = isActive;
    }

    public static void sendOrderReceiveConfirmation(Order order, int execId) {
        if (!isActive)
            return;

        FixBodyExecutionReport fixBodyExecutionReport =
                new FixBodyExecutionReport(Integer.toString(order.getId()), Integer.toString(order.getClientOderId()),
                        Integer.toString(execId), ExecType.NEW, OrderStatus.NEW, order.getSymbol(),
                        order.getSide(), order.getPrice(), order.getQuantity(), 0, 0);
        String clientCompId = SimpleServer.getPortForClient(order.getClientId()).clientCompId;
        int seqNo = ++SimpleServer.getPortForClient(order.getClientId()).fixEnginePort.crtSeqNr;
        sendBodyExecReport(fixBodyExecutionReport, clientCompId, seqNo);
    }


    // Basically fill or partial fill
    public static void sendOrderTrade(Order order, int execId, OrderStatus orderStatus) {
        if (!isActive)
            return;

        FixBodyExecutionReport fixBodyExecutionReport =
                new FixBodyExecutionReport(Integer.toString(order.getId()), Integer.toString(order.getClientOderId()),
                        Integer.toString(execId), ExecType.TRADE, orderStatus, order.getSymbol(),
                        order.getSide(), order.getPrice(), order.getQuantity(), 0, 0);

        String clientCompId = SimpleServer.getPortForClient(order.getClientId()).clientCompId;
        int seqNo = ++SimpleServer.getPortForClient(order.getClientId()).fixEnginePort.crtSeqNr;
        sendBodyExecReport(fixBodyExecutionReport, clientCompId, seqNo);
    }

    private static void sendBodyExecReport(FixBody fixBody, String clientCompId, int seqNo) {
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(),
                MessageType.ExecutionReport, "Exchange SRL",
                clientCompId, seqNo, OffsetDateTime.now());
        sendBroadcast(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)).toString());
    }
}


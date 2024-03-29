package org.exchange.broadcast;

import org.common.fix.FixMessage;
import org.common.fix.FixTrailer;
import org.common.fix.body.FixBody;
import org.common.fix.body.FixBodyCancelReject;
import org.common.fix.body.FixBodyExecutionReport;
import org.common.fix.cancel.CxlRejReason;
import org.common.fix.cancel.CxlRejResponseTo;
import org.common.fix.header.BeginString;
import org.common.fix.header.FixHeader;
import org.common.fix.header.MessageType;
import org.common.fix.order.ExecType;
import org.common.fix.order.OrderStatus;
import org.exchange.book.Order;
import org.exchange.ports.SimpleServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class BroadcastSender {
    private static final int PORT = 4445;
    private static boolean isActive = true;
    private static final List<byte[]> messages = new ArrayList<>();
    private static int broadcastId = 0;

    private static int getNewBroadcastId() {
        return ++broadcastId;
    }

    public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(a);
            outputStream.write(b);
        } catch (IOException exception) {
            System.out.println("Error concatenating byte arrays " + exception.getMessage());
        }
        return outputStream.toByteArray();
    }

    /**
     * Resend all messages starting from broadcastId
     *
     * @param broadcastId the index to start sending from
     */
    public static void resendBroadcasts(int broadcastId) {
        for (int i = broadcastId - 1; i < messages.size(); i++)
            resendBroadcast(messages.get(i));
    }

    public static void resendBroadcast(byte[] broadcast) {
        try {
            MulticastSocket socket = new MulticastSocket();
            InetAddress group = InetAddress.getByName("224.0.0.1");

            DatagramPacket packet =
                    new DatagramPacket(broadcast, broadcast.length,
                            group, PORT);

            socket.send(packet);
            socket.close();
        } catch (IOException exception) {
            System.out.println("Error sending broadcast");
            System.exit(-1);
        }
    }

    public static void sendBroadcast(String message) {
        try {
            MulticastSocket socket = new MulticastSocket();
            InetAddress group = InetAddress.getByName("224.0.0.1");


            int broadcastId = getNewBroadcastId();
            byte[] broadcastIdBuf = ByteBuffer.allocate(4).putInt(broadcastId).array();
            byte[] messageBytes = message.getBytes();
            byte[] buffer = concatenateByteArrays(broadcastIdBuf, messageBytes);

            DatagramPacket packet =
                    new DatagramPacket(buffer, buffer.length,
                            group, PORT);

            messages.add(buffer);
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
        sendBody(fixBodyExecutionReport, clientCompId, seqNo, MessageType.ExecutionReport);
    }

    public static void sendOrderCanceled(Order order, int execId) {
        if (!isActive)
            return;

        FixBodyExecutionReport fixBodyExecutionReport =
                new FixBodyExecutionReport(Integer.toString(order.getId()), Integer.toString(order.getClientOderId()),
                        Integer.toString(execId), ExecType.CANCELED, OrderStatus.CANCELED, order.getSymbol(),
                        order.getSide(), order.getPrice(), order.getQuantity(), 0, 0);

        String clientCompId = SimpleServer.getPortForClient(order.getClientId()).clientCompId;
        int seqNo = ++SimpleServer.getPortForClient(order.getClientId()).fixEnginePort.crtSeqNr;
        sendBody(fixBodyExecutionReport, clientCompId, seqNo, MessageType.ExecutionReport);
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
        sendBody(fixBodyExecutionReport, clientCompId, seqNo, MessageType.ExecutionReport);
    }

    private static void sendBody(FixBody fixBody, String clientCompId, int seqNo, MessageType messageType) {
        FixHeader fixHeader =
                new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(),
                        messageType, "Exchange SRL",
                        clientCompId, seqNo, OffsetDateTime.now());
        sendBroadcast(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)).toString());
    }

    public static void sendOrderCancelReject(Order cancelOrder) {
        if (!isActive)
            return;

        FixBodyCancelReject fixBodyCancelReject =
                new FixBodyCancelReject(String.valueOf(cancelOrder.getId()), String.valueOf(cancelOrder.getClientOderId()),
                        "123", OrderStatus.FILLED,
                        CxlRejResponseTo.ORDER_CANCEL_REQUEST, CxlRejReason.TOO_LATE_TO_CANCEL);
        String clientCompId = SimpleServer.getPortForClient(cancelOrder.getClientId()).clientCompId;
        int seqNo = ++SimpleServer.getPortForClient(cancelOrder.getClientId()).fixEnginePort.crtSeqNr;
        sendBody(fixBodyCancelReject, clientCompId, seqNo, MessageType.OrderCancelReject);
    }
}


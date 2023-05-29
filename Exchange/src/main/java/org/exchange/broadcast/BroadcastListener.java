package org.exchange.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class BroadcastListener {
    public static MessagePair getNextPacket(DatagramSocket socket) {
        try {
            byte[] buffer = new byte[1024];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            // extract the broadcast ID from the first 4 bytes
            byte[] broadcastIdBuf = Arrays.copyOfRange(buffer, 0, 4);
            int broadcastId = ByteBuffer.wrap(broadcastIdBuf).getInt();

            String message = new String(packet.getData(), 4, packet.getLength());

            return new MessagePair(broadcastId, message);
        } catch (IOException exception) {
            System.out.println("Error receiving broadcast data " + exception.getMessage());
            System.exit(-1);
        }
        return null;
    }
}

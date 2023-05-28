package org.exchange.book;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastSender {
    private static final int PORT = 4445;

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
}


package org.exchange.logger;

import org.common.fix.body.FixBodyExecutionReport;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LoggerListener extends Thread {
    private static final int PORT = 4445;

    public void run() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            while (true) {
                byte[] buffer = new byte[1024];

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                FixBodyExecutionReport report = FixBodyExecutionReport.fromString(message);
                System.out.println("Received report: " + report);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


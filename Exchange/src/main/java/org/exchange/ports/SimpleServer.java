package org.exchange.ports;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
    public static final int PORT = 8100;

    static public void startSimpleServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("Waiting for a client ...");
                Socket socket = serverSocket.accept();
                System.out.println("Starting client thread ...");
                new Port(socket).start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
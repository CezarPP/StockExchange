package org.exchange.ports;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class SimpleServer {
    public static final int PORT = 8100;

    // clientId -> Port
    static Map<Integer, Port> clientToPort = new TreeMap<>();

    static public void startSimpleServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("Waiting for a client ...");
                Socket socket = serverSocket.accept();
                System.out.println("Starting client thread ...");
                int clientId = Port.getNewClientId();
                Port port = new Port(socket, clientId);
                clientToPort.put(clientId, port);
                port.start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    static public Port getPortForClient(int clientId) {
        return clientToPort.get(clientId);
    }
}
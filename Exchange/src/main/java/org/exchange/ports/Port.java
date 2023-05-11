package org.exchange.ports;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Port extends Thread {
    private final Socket socket;

    private ServerWriter writer;
    int clientId;

    Port(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            writer = new ServerWriter(out);

            boolean logged = clientLoggedIn();
            if (!logged)
                this.interrupt();

            Thread readThread = new ServerReadThread(in);
            readThread.start();
            readThread.join();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    boolean clientLoggedIn() {
        // TODO()
        return true;
    }
}

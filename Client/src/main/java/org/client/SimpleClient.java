package org.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleClient {

    public static void main(String[] args) {
        new SimpleClient().startClient();
    }

    public void startClient() {
        final String serverAddress = "127.0.0.1";
        final int PORT = 8100;

        try (Socket socket = new Socket(serverAddress, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Thread writeThread = new WriteClientThread(out);
            Thread readThread = new ReadClientThread(in);

            writeThread.start();
            readThread.start();

            writeThread.join();
            readThread.join();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
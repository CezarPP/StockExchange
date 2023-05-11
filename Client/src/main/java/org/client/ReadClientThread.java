package org.client;

import java.io.BufferedReader;

public class ReadClientThread extends Thread {
    private final BufferedReader in;

    ReadClientThread(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            // TODO(send commands to interface)
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}

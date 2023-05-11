package org.exchange.ports;

import java.io.BufferedReader;

public class ServerReadThread extends Thread {
    private final BufferedReader in;
    ServerReadThread(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {

    }
}

package org.exchange.ports;

import java.net.Socket;

public class Port extends Thread {
    private final Socket socket;

    int clientId;

    Port(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        boolean logged = clientLoggedIn();
        if (!logged)
            this.interrupt();
        // TODO()
    }


    boolean clientLoggedIn() {
        // TODO()
        return true;
    }
}

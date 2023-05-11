package org.exchange.ports;

import org.common.fix.FixMessage;

import java.io.PrintWriter;
import java.util.Objects;

public class ServerWriter {
    private final PrintWriter out;

    ServerWriter(PrintWriter out) {
        this.out = out;
    }

    void sendMessage(FixMessage message) {
        out.println(message.toString());
        out.flush();
    }
}

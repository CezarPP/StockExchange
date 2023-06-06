package org.exchange;

import org.exchange.broadcast.Retransmitter;
import org.exchange.logger.Logger;
import org.exchange.ports.SimpleServer;

public class Main {
    public static void main(String[] args) {
        Logger logger = new Logger();
        Retransmitter retransmitter = new Retransmitter();
        logger.start();
        retransmitter.start();
        SimpleServer.startSimpleServer();
    }
}
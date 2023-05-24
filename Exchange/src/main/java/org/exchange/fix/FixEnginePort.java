package org.exchange.fix;

import org.common.fix.FixMessage;
import org.common.fix.FixTrailer;
import org.common.fix.body.FixBodyLogin;
import org.common.fix.body.FixBodyLogout;
import org.common.fix.body.FixBodyOrder;
import org.common.fix.header.BeginString;
import org.common.fix.header.FixHeader;
import org.common.fix.header.MessageType;
import org.common.fix.login.EncryptMethod;
import org.common.fix.order.OrderType;
import org.common.fix.order.Side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class FixEngineServer {
    final String clientOrderID = "Cezar";
    final String senderCompID = "Cezar SRL";
    final String targetCompID = "Exchange SRL";
    final String username = "Cezar";
    final static int marketDepth = 20;
    int crtSeqNr = 0;
    int orderID = 0;
    private final BufferedReader in;
    private final PrintWriter out;

    FixEngineServer(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void start() {
        // TODO(start read write threads)
    }

    private void sendLogin() {
        crtSeqNr++;

        FixBodyLogin fixBody = new FixBodyLogin(EncryptMethod.NONE_OTHER, 1000, username);
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.Logon,
                senderCompID, targetCompID, crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    public void sendMarketDataForSymbol() {
        crtSeqNr++;
        // TODO()
    }

    public void logout() {
        crtSeqNr++;

        FixBodyLogout fixBody = new FixBodyLogout();
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.Logout,
                senderCompID, targetCompID, crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    private void send(FixMessage fixMessage) {
        out.println(fixMessage);
        out.flush();
    }
}

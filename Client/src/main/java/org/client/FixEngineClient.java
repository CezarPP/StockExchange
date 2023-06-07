package org.client;

import org.common.fix.FixMessage;
import org.common.fix.FixTrailer;
import org.common.fix.body.*;
import org.common.fix.header.BeginString;
import org.common.fix.header.FixHeader;
import org.common.fix.header.MessageType;
import org.common.fix.login.EncryptMethod;
import org.common.fix.market_data.MarketDataEntryType;
import org.common.fix.market_data.SubscriptionRequestType;
import org.common.fix.order.OrderType;
import org.common.fix.order.PriceType;
import org.common.symbols.Symbol;
import org.gui.StockExchangeClientFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class FixEngineClient {
    private static final Random random = new Random();
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 8100;
    public String senderCompID;
    final String targetCompID = "Exchange SRL";
    final String username = "Cezar";
    final static int marketDepth = 20;

    private int clientOrderID = 0;
    int crtSeqNr = 0;
    int reqID = 0;
    public Map<Integer, Order> ordersSent = new TreeMap<>();
    private BufferedReader in;
    private PrintWriter out;

    FixEngineClient(StockExchangeClientFrame frame) {
        try {
            senderCompID = "Cezar SRL" + random.nextInt(500000);
            System.out.println("Sender comp id is " + senderCompID);
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.in = in;
            this.out = out;
            sendLogin();
            waitLoginResponse();
            new ReadClientThread(in, frame, this).start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public synchronized int getNewClientOrderId() {
        return ++clientOrderID;
    }

    public void sendNewSingleOrderLimit(Order order) {
        crtSeqNr++;

        FixBodyOrder fixBody =
                new FixBodyOrder(Integer.toString(clientOrderID),
                        order.symbol, order.side, OffsetDateTime.now(ZoneOffset.UTC),
                        OrderType.LIMIT, order.quantity, PriceType.PER_UNIT, order.price);

        FixHeader fixHeader =
                new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.NewOrderSingle,
                        senderCompID, targetCompID, crtSeqNr, OffsetDateTime.now());

        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    static float truncatePrice(float price) {
        int temp = (int) (price * 100);
        return (float) temp / 100;
    }

    public void sendCancelOrder(Order order) {
        crtSeqNr++;

        Order cancelOrder = new Order(getNewClientOrderId(), order.symbol, truncatePrice(order.price), order.quantity, order.side);
        FixBodyCancel fixBody =
                new FixBodyCancel(Integer.toString(order.clientOrderId), Integer.toString(order.exchangeOrderID),
                        Integer.toString(cancelOrder.clientOrderId), cancelOrder.symbol, cancelOrder.side,
                        OffsetDateTime.now(), cancelOrder.quantity);

        FixHeader fixHeader =
                new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.OrderCancelRequest,
                        senderCompID, targetCompID, crtSeqNr, OffsetDateTime.now());

        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    private void sendLogin() {
        crtSeqNr++;

        FixBodyLogin fixBody = new FixBodyLogin(EncryptMethod.NONE_OTHER, 1000, username);
        FixHeader fixHeader =
                new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.Logon,
                        senderCompID, targetCompID, crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    private void waitLoginResponse() {
        try {
            in.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logout() {
        crtSeqNr++;

        FixBodyLogout fixBody = new FixBodyLogout();
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.Logout,
                senderCompID, targetCompID, crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    public void requestMarketDataForSymbol(Symbol symbol) {
        crtSeqNr++;
        reqID++;

        FixBodyRequest fixBody = new FixBodyRequest(reqID, SubscriptionRequestType.SNAPSHOT,
                marketDepth, List.of(MarketDataEntryType.BID, MarketDataEntryType.OFFER), List.of(symbol.getSymbol()));
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.MarketDataRequest,
                senderCompID, targetCompID, crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    private void send(FixMessage fixMessage) {
        System.out.println("Client sending fix message " + fixMessage);
        out.println(fixMessage);
        out.flush();
    }

}

package org.client;

import org.common.fix.*;
import org.common.fix.body.*;
import org.common.fix.header.BeginString;
import org.common.fix.header.FixHeader;
import org.common.fix.header.MessageType;
import org.common.fix.login.EncryptMethod;
import org.common.fix.order.OrderType;
import org.common.fix.order.PriceType;
import org.common.fix.order.Side;
import org.common.fix.market_data.MarketDataEntryType;
import org.common.fix.market_data.SubscriptionRequestType;
import org.common.symbols.Symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

public class FixEngineClient {
    final String clientOrderID = "Cezar";
    final String senderCompID = "Cezar SRL";
    final String targetCompID = "Exchange SRL";
    final String username = "Cezar";
    final static int marketDepth = 20;
    int crtSeqNr = 0;
    int reqID = 0;

    Map<String, Order> ordersSent;

    private final BufferedReader in;
    private final PrintWriter out;

    FixEngineClient(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void start() {
        sendLogin();
        waitLoginResponse();
        // TODO(start read write threads)
    }

    public void sendNewSingleOrderLimit(Order order) {
        crtSeqNr++;

        FixBodyOrder fixBody =
                new FixBodyOrder(this.clientOrderID + crtSeqNr,
                        order.symbol, order.side, OffsetDateTime.now(ZoneOffset.UTC),
                        OrderType.LIMIT, order.quantity, PriceType.PER_UNIT, order.price);

        FixHeader fixHeader =
                new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.NewOrderSingle,
                        senderCompID, targetCompID, crtSeqNr, OffsetDateTime.now());

        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
        waitOrderResponse(order);
    }

    public void sendCancelOrder(String orderID) {
        crtSeqNr++;
        Order order = ordersSent.get(orderID);
        Order cancelOrder = new Order(order.symbol, order.price, order.quantity, order.side);
        FixBodyCancel fixBody =
                new FixBodyCancel(order.clientOrderID, order.exchangeOrderID,
                        cancelOrder.clientOrderID, cancelOrder.symbol, cancelOrder.side,
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

    /**
     * wait for the confirmation of a received order
     */
    private void waitOrderResponse(Order order) {
        try {
            String confirmation = in.readLine();
            FixMessage fixMessage = FixMessage.fromString(confirmation);
            if (fixMessage.body() instanceof FixBodyExecutionReport fixBody) {
                order.setExchangeOrderID(fixBody.orderID);
            } else {
                System.out.println("Message is not order response");
            }
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
        out.println(fixMessage);
        out.flush();
    }

}

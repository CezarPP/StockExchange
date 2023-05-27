package org.exchange.ports;

import org.common.fix.FixMessage;
import org.common.fix.body.FixBodyCancel;
import org.common.fix.body.FixBodyOrder;
import org.common.fix.header.MessageType;
import org.common.symbols.Symbol;
import org.exchange.book.Order;
import org.exchange.book.OrderBook;
import org.exchange.book.OrderBookFactory;
import org.exchange.book.Side;
import org.exchange.fix.FixEnginePort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Port extends Thread {
    public FixEnginePort fixEnginePort;
    public int clientId;
    private static int staticClientId;
    private BufferedReader in;


    synchronized static int getNewClientId() {
        staticClientId++;
        return staticClientId;
    }

    public Port(Socket socket) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            fixEnginePort = new FixEnginePort(this.in, out);

            clientId = getNewClientId();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Port received request " + request);
                FixMessage fixMessage = FixMessage.fromString(request);
                if (fixMessage.header().messageType == MessageType.MarketDataRequest) {
                    fixEnginePort.sendMarketData(fixMessage);
                } else if (fixMessage.header().messageType == MessageType.NewOrderSingle) {
                    handleNewSingleOrder(fixMessage);
                } else if (fixMessage.header().messageType == MessageType.OrderCancelRequest) {
                    handleOrderCancel(fixMessage);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void handleNewSingleOrder(FixMessage fixMessage) {
        FixBodyOrder fixBodyOrder = FixBodyOrder.fromString(fixMessage.body().toString());
        Symbol symbol = fixBodyOrder.symbol;
        OrderBook orderBook = OrderBookFactory.getOrderBook(symbol);
        int qty = fixBodyOrder.orderQuantity;
        float price = fixBodyOrder.price;
        Order order = new Order(1, symbol, price, qty,
                (fixBodyOrder.side.label == '1') ? Side.BUY : Side.SELL, false);
        int orderId = orderBook.addToQueue(order);
        // TODO(return orderId to client)
    }

    private void handleOrderCancel(FixMessage fixMessage) {
        FixBodyCancel fixBodyOrder = FixBodyCancel.fromString(fixMessage.body().toString());
        Symbol symbol = fixBodyOrder.symbol;
        OrderBook orderBook = OrderBookFactory.getOrderBook(symbol);
        Order order = new Order(Integer.parseInt(fixBodyOrder.orderID), fixBodyOrder.symbol, 0,
                fixBodyOrder.orderQuantity, (fixBodyOrder.side.label == '1') ? Side.BUY : Side.SELL, true);
        orderBook.addToQueue(order);
    }
}

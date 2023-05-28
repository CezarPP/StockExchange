package org.exchange.ports;

import org.common.fix.FixMessage;
import org.common.fix.body.FixBodyCancel;
import org.common.fix.body.FixBodyOrder;
import org.common.fix.header.MessageType;
import org.common.symbols.Symbol;
import org.exchange.book.Order;
import org.exchange.book.OrderBook;
import org.exchange.book.OrderBookFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class Port extends Thread {
    public FixEnginePort fixEnginePort;

    private PortBroadcastListener portBroadcastListener;
    public int clientId;
    private static int staticClientId;
    private BufferedReader in;

    public String clientCompId = "Cezar SRL"; // TODO(send in login)


    // Map of outstanding orders of this client
    // orderId -> Order
    Map<Integer, Order> ordersMap;

    synchronized public static int getNewClientId() {
        staticClientId++;
        return staticClientId;
    }

    public Port(Socket socket, int clientId) {
        this.ordersMap = new TreeMap<>();
        this.clientId = clientId;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            fixEnginePort = new FixEnginePort(this.in, out, clientCompId);
            this.portBroadcastListener = new PortBroadcastListener(clientId, clientCompId, fixEnginePort);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        portBroadcastListener.start();
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
        Order order = new Order(1, Integer.parseInt(fixBodyOrder.clientOrderID), clientId, symbol, price, qty,
                fixBodyOrder.side, false);
        int orderId = orderBook.addToQueue(order);
        ordersMap.put(orderId, order);
    }

    private void handleOrderCancel(FixMessage fixMessage) {
        FixBodyCancel fixBodyOrder = FixBodyCancel.fromString(fixMessage.body().toString());
        Symbol symbol = fixBodyOrder.symbol;
        OrderBook orderBook = OrderBookFactory.getOrderBook(symbol);
        Order order = new Order(Integer.parseInt(fixBodyOrder.orderID), Integer.parseInt(fixBodyOrder.clientOrderID),
                clientId, fixBodyOrder.symbol, 0, fixBodyOrder.orderQuantity, fixBodyOrder.side, true);
        orderBook.addToQueue(order);
    }
}

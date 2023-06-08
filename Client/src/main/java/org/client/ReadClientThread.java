package org.client;

import org.common.fix.FixMessage;
import org.common.fix.body.FixBodyCancelReject;
import org.common.fix.body.FixBodyExecutionReport;
import org.common.fix.body.FixBodyMarketData;
import org.common.fix.cancel.CxlRejReason;
import org.common.fix.header.MessageType;
import org.common.fix.market_data.MarketDataEntry;
import org.common.fix.market_data.MarketDataEntryType;
import org.common.fix.order.ExecType;
import org.common.fix.order.OrderStatus;
import org.gui.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;

public class ReadClientThread extends Thread {
    private final BufferedReader in;
    private final StockExchangeClientFrame frame;
    private final UserOrdersPanel userOrdersPanel;
    private final FixEngineClient fixEngineClient;

    ReadClientThread(BufferedReader in, StockExchangeClientFrame frame, FixEngineClient fixEngineClient) {
        this.in = in;
        this.frame = frame;
        this.userOrdersPanel = UserOrdersPanelFactory.getUserOrdersPanel();
        this.fixEngineClient = fixEngineClient;
    }

    @Override
    public void run() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                FixMessage fixMessage = FixMessage.fromString(response);
                System.out.println("Client received message " + response + " with type " + fixMessage.header().messageType);
                if (fixMessage.header().messageType == MessageType.MarketDataSnapshotFullRefresh) {
                    addMarketDataToPanel(fixMessage, frame.getBidAskPanel());
                } else if (fixMessage.header().messageType == MessageType.ExecutionReport) {
                    handleExecutionReport(fixMessage);
                } else if (fixMessage.header().messageType == MessageType.OrderCancelReject) {
                    handleCancelReject(fixMessage);
                } else if (fixMessage.header().messageType == MessageType.Logout)
                    System.exit(0);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void addMarketDataToPanel(FixMessage fixMessage, BidAskPanel bidAskPanel) {
        bidAskPanel.removeAllAsks();
        bidAskPanel.removeAllBids();
        FixBodyMarketData fixBody = FixBodyMarketData.fromString(fixMessage.body().toString());

        bidAskPanel.removeAllBids();
        bidAskPanel.removeAllAsks();
        for (MarketDataEntry marketDataEntry : fixBody.marketDataEntries) {
            PanelOrder panelOrder = new PanelOrder(marketDataEntry.getMdEntryPositionNo(),
                    marketDataEntry.getPrice(), marketDataEntry.getQuantity());
            if (marketDataEntry.getMarketDataEntryType() == MarketDataEntryType.BID) {
                bidAskPanel.addBid(panelOrder);
            } else {
                bidAskPanel.addAsk(panelOrder);
            }
        }
    }

    private void handleExecutionReport(FixMessage fixMessage) {
        FixBodyExecutionReport fixBodyExecutionReport = FixBodyExecutionReport.fromString(fixMessage.body().toString());
        System.out.println("Client read thread received exec report for orderId" + fixBodyExecutionReport.orderID
                + " with client orderId: " + fixBodyExecutionReport.origClientOrderID);
        if (fixBodyExecutionReport.execType == ExecType.NEW) {
            // Order confirmation
            handleExecReportConfirmation(fixBodyExecutionReport);
        } else if (fixBodyExecutionReport.execType == ExecType.TRADE) {
            // Fill or partial fill
            handleExecReportFill(fixBodyExecutionReport);
        } else if (fixBodyExecutionReport.execType == ExecType.CANCELED) {
            // Cancel confirmation
            handleExecReportCancel(fixBodyExecutionReport);
        } else {
            throw new IllegalArgumentException("Not implemented execType for execution report");
        }
    }

    private void handleExecReportFill(FixBodyExecutionReport fixBodyExecutionReport) {
        int orderId = Integer.parseInt(fixBodyExecutionReport.orderID);
        OrderStatus orderStatus = fixBodyExecutionReport.orderStatus;

        Order order = fixEngineClient.ordersSent.get(orderId);
        assert order != null;
        if (fixBodyExecutionReport.leavesQuantity == 0) {
            fixEngineClient.ordersSent.remove(orderId);
        } else {
            order.setQuantity(fixBodyExecutionReport.leavesQuantity);
        }

        if (orderStatus == OrderStatus.FILLED) {
            userOrdersPanel.removeOrder(orderId);
        } else if (orderStatus == OrderStatus.PARTIALLY_FILLED) {
            userOrdersPanel.decreaseQuantity(orderId, fixBodyExecutionReport.leavesQuantity);
        } else {
            throw new IllegalArgumentException("Not implemented orderStatus for execution report");
        }
    }

    static float truncatePrice(float price) {
        int temp = (int) (price * 100);
        return (float) temp / 100;
    }

    private void handleExecReportConfirmation(FixBodyExecutionReport fixBodyExecutionReport) {
        int clientOrderId = Integer.parseInt(fixBodyExecutionReport.origClientOrderID);
        int orderId = Integer.parseInt(fixBodyExecutionReport.orderID);
        userOrdersPanel.confirmOrder(clientOrderId, orderId);
        Order order = new Order(Integer.parseInt(fixBodyExecutionReport.origClientOrderID),
                fixBodyExecutionReport.symbol, truncatePrice(fixBodyExecutionReport.price),
                fixBodyExecutionReport.leavesQuantity, fixBodyExecutionReport.side);
        order.setExchangeOrderID(orderId);
        fixEngineClient.ordersSent.put(orderId, order);
    }

    private void handleExecReportCancel(FixBodyExecutionReport fixBodyExecutionReport) {
        // Canceled successful
        int orderId = Integer.parseInt(fixBodyExecutionReport.orderID);
        OrderStatus orderStatus = fixBodyExecutionReport.orderStatus;
        assert orderStatus == OrderStatus.CANCELED;

        Order order = fixEngineClient.ordersSent.get(orderId);
        assert order != null;
        fixEngineClient.ordersSent.remove(orderId);

        userOrdersPanel.removeOrder(orderId);
        String message = "Order with id " + orderId + " has been successfully canceled";
        JOptionPane.showMessageDialog(frame, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleCancelReject(FixMessage fixMessage) {
        FixBodyCancelReject fixBodyCancelReject = FixBodyCancelReject.fromString(fixMessage.body().toString());
        StringBuilder message = new StringBuilder("Cancel for client order with id: " + fixBodyCancelReject.clOrderId);
        if (fixBodyCancelReject.cxlRejReason == CxlRejReason.TOO_LATE_TO_CANCEL)
            message.append(" because it was already filled");
        else if (fixBodyCancelReject.cxlRejReason == CxlRejReason.UNKNOWN_ORDER)
            message.append(" because the order is unknown to the exchange");
        JOptionPane.showMessageDialog(frame, message.toString(), "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}

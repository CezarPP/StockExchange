package org.client;

import org.common.fix.FixMessage;
import org.common.fix.body.FixBodyExecutionReport;
import org.common.fix.body.FixBodyMarketData;
import org.common.fix.header.MessageType;
import org.common.fix.market_data.MarketDataEntry;
import org.common.fix.market_data.MarketDataEntryType;
import org.common.fix.order.ExecType;
import org.gui.*;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadClientThread extends Thread {
    private final BufferedReader in;
    private final StockExchangeClientFrame frame;

    private final UserOrdersPanel userOrdersPanel;

    ReadClientThread(BufferedReader in, StockExchangeClientFrame frame) {
        this.in = in;
        this.frame = frame;
        this.userOrdersPanel = UserOrdersPanelFactory.getUserOrdersPanel();
    }

    @Override
    public void run() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Client received message " + response);
                FixMessage fixMessage = FixMessage.fromString(response);
                if (fixMessage.header().messageType == MessageType.MarketDataSnapshotFullRefresh) {
                    addMarketDataToPanel(fixMessage, frame.getBidAskPanel());
                } else if (fixMessage.header().messageType == MessageType.ExecutionReport) {
                    handleExecutionReport(fixMessage);
                }
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
        if (fixBodyExecutionReport.execType == ExecType.NEW) {
            // Order confirmation
            int clientOrderId = Integer.parseInt(fixBodyExecutionReport.origClientOrderID);
            int orderId = Integer.parseInt(fixBodyExecutionReport.orderID);
            userOrdersPanel.confirmOrder(clientOrderId, orderId);
        } else {
            throw new IllegalArgumentException("Not implemented execType for execution report");
        }
    }
}

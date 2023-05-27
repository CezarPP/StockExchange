package org.client;

import org.common.fix.FixMessage;
import org.common.fix.body.FixBody;
import org.common.fix.body.FixBodyMarketData;
import org.common.fix.header.MessageType;
import org.common.fix.market_data.MarketDataEntry;
import org.common.fix.market_data.MarketDataEntryType;
import org.gui.BidAskPanel;
import org.gui.PanelOrder;
import org.gui.StockExchangeClientFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReadClientThread extends Thread {
    private final BufferedReader in;
    private final StockExchangeClientFrame frame;

    ReadClientThread(BufferedReader in, StockExchangeClientFrame frame) {
        this.in = in;
        this.frame = frame;
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
        List<PanelOrder> bids = new ArrayList<>();
        List<PanelOrder> asks = new ArrayList<>();
        for (MarketDataEntry marketDataEntry : fixBody.marketDataEntries) {
            PanelOrder panelOrder = new PanelOrder(marketDataEntry.getMdEntryPositionNo(),
                    marketDataEntry.getPrice(), marketDataEntry.getQuantity());
            if (marketDataEntry.getMarketDataEntryType() == MarketDataEntryType.BID) {
                bidAskPanel.addBid(panelOrder);
            } else {
                bidAskPanel.addAsk(panelOrder);
            }
        }
        /*
        bids.sort(Comparator.comparingInt(PanelOrder::id));
        asks.sort(Comparator.comparingInt(PanelOrder::id));
        for (PanelOrder panelOrder : bids)
            bidAskPanel.addBid(panelOrder);
        for (PanelOrder panelOrder : bids)
            bidAskPanel.addAsk(panelOrder);*/
    }
}

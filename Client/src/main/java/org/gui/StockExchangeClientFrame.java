package org.gui;

import org.client.FixEngineClient;
import org.common.symbols.Symbol;

import javax.swing.*;
import java.awt.*;

public class StockExchangeClientFrame extends JFrame {
    private BidAskPanel bidAskPanel = null;

    public StockExchangeClientFrame() {
        setTitle("Stock Exchange Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLayout(new BorderLayout());
    }

    public BidAskPanel getBidAskPanel() {
        return bidAskPanel;
    }

    public void setBidAskPanel(Symbol symbol) {
        if (this.bidAskPanel != null)
            this.remove(this.bidAskPanel);
        BidAskPanel bidAskPanel = new BidAskPanel(symbol);
        this.bidAskPanel = bidAskPanel;
        this.add(bidAskPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }
}
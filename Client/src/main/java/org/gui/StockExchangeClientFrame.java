package org.gui;

import org.common.symbols.Symbol;

import javax.swing.*;
import java.awt.*;

public class StockExchangeClientFrame extends JFrame {
    private BidAskPanel bidAskPanel = null;
    private final UserOrdersPanel userOrdersPanel;
    private JSplitPane splitPane = null;

    public StockExchangeClientFrame() {
        setTitle("Stock Exchange Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLayout(new BorderLayout());

        userOrdersPanel = UserOrdersPanelFactory.getUserOrdersPanel();
    }

    public BidAskPanel getBidAskPanel() {
        return bidAskPanel;
    }

    public void setBidAskPanel(Symbol symbol) {
        if (this.bidAskPanel != null)
            this.remove(splitPane);
        BidAskPanel bidAskPanel = new BidAskPanel(symbol);
        this.bidAskPanel = bidAskPanel;

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bidAskPanel, userOrdersPanel);
        splitPane.setOneTouchExpandable(true);
        this.add(splitPane, BorderLayout.CENTER);
        this.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            // set the divider location only after all pending Swing events are processed
            splitPane.setDividerLocation(0.5);
        });
    }

    public UserOrdersPanel getUserOrdersPanel() {
        return userOrdersPanel;
    }
}
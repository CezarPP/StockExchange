package org.client;

import javax.swing.*;
import java.awt.*;

public class StockExchangeClientFrame extends JFrame {

    public StockExchangeClientFrame() {
        setTitle("Stock Exchange Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLayout(new BorderLayout());

        add(new NewSingleOrderPanel(), BorderLayout.NORTH);
        BidAskPanel bidAskPanel = new BidAskPanel();
        bidAskPanel.addBid(1, 123.0, 3123);
        bidAskPanel.addAsk(2, 130.0, 2313);
        add(bidAskPanel, BorderLayout.CENTER);

    }
}
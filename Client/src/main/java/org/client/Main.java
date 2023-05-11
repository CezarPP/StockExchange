package org.client;

import org.gui.StockExchangeClientFrame;

public class Main {
    public static void main(String[] args) {
        StockExchangeClientFrame frame = new StockExchangeClientFrame();
        frame.setVisible(true);
        new SimpleClient().startClient();
    }
}

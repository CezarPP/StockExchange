package org.client;

import org.gui.NewSingleOrderPanel;
import org.gui.StockExchangeClientFrame;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        StockExchangeClientFrame frame = new StockExchangeClientFrame();
        FixEngineClient fixEngine = new FixEngineClient(frame);
        frame.add(new NewSingleOrderPanel(frame, fixEngine), BorderLayout.NORTH);
        frame.setVisible(true);
    }
}

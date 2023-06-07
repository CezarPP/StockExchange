package org.gui;

import org.client.FixEngineClient;
import org.client.Order;
import org.client.RequestTimer;
import org.client.TimerObserver;
import org.common.fix.order.Side;
import org.common.symbols.Symbol;

import javax.swing.*;
import java.awt.*;

public class NewSingleOrderPanel extends JPanel implements TimerObserver {
    final JComboBox<Symbol> stockDropdown;
    final StockExchangeClientFrame frame;
    final FixEngineClient fixEngine;

    final UserOrdersPanel userOrdersPanel;
    RequestTimer requestTimer;

    static float truncatePrice(float price) {
        int temp = (int) (price * 100);
        return (float) temp / 100;
    }

    public NewSingleOrderPanel(StockExchangeClientFrame frame, FixEngineClient fixEngine) {
        this.userOrdersPanel = UserOrdersPanelFactory.getUserOrdersPanel();
        this.frame = frame;
        this.fixEngine = fixEngine;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("New Single Order"));

        // Stock dropdown
        stockDropdown = new JComboBox<>(Symbol.values());
        this.add(stockDropdown);
        stockDropdown.addActionListener(e -> changeStock());
        changeStock();

        // Quantity label input and label
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel quantityLabel = new JLabel("Quantity");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        this.add(quantityPanel);

        // Price label and input
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel priceLabel = new JLabel("Price");
        JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(0.01, 0.01, 1000000.0, 0.01));
        priceSpinner.setEditor(new JSpinner.NumberEditor(priceSpinner, "0.00"));
        pricePanel.add(priceLabel);
        pricePanel.add(priceSpinner);
        this.add(pricePanel);

        // Buy and sell buttons
        JPanel buySellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup buttonGroup = new ButtonGroup();
        JToggleButton buyButton = new JToggleButton("Buy");
        JToggleButton sellButton = new JToggleButton("Sell");
        buttonGroup.add(buyButton);
        buttonGroup.add(sellButton);
        buySellPanel.add(buyButton);
        buySellPanel.add(sellButton);
        this.add(buySellPanel);

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            String orderIdStr = JOptionPane.showInputDialog("Enter the order ID to cancel:");
            if (orderIdStr != null) {
                try {
                    int orderId = Integer.parseInt(orderIdStr);
                    Order order = fixEngine.ordersSent.get(orderId);
                    if (order == null) {
                        JOptionPane.showMessageDialog(null, "You have no outstanding orders with this id");
                        return;
                    }
                    fixEngine.sendCancelOrder(order);
                    JOptionPane.showMessageDialog(null, "Cancel request has been sent");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid order ID entered.");
                }
            }
        });
        buySellPanel.add(cancelButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    null, "Are you sure you want to close the app?",
                    "Exit Confirmation", JOptionPane.YES_NO_OPTION
            );
            if (confirm == 0) {
                System.exit(0);
            }
        });
        buySellPanel.add(exitButton);

        this.add(buySellPanel);

        // Submit button
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            Symbol symbol = (Symbol) stockDropdown.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();
            // Making it float from the start causes an error
            double price = (Double) priceSpinner.getValue();
            boolean isBuy = buyButton.isSelected();

            Order order = new Order(fixEngine.getNewClientOrderId(), symbol, truncatePrice((float) price), quantity, (isBuy) ? Side.BUY : Side.SELL);
            userOrdersPanel.addOrder(order);
            fixEngine.sendNewSingleOrderLimit(order);

            JOptionPane.showMessageDialog(frame, "Order submitted", "Information", JOptionPane.INFORMATION_MESSAGE);

            // Reset the buttons, spinners and to their default state
            quantitySpinner.setValue(1);
            priceSpinner.setValue(0.01);
            buttonGroup.clearSelection();
        });
        submitPanel.add(submitButton);
        this.add(submitPanel);

        requestTimer = new RequestTimer(this);
        requestTimer.start();
    }

    Symbol getStock() {
        return (Symbol) stockDropdown.getSelectedItem();
    }

    void changeStock() {
        Symbol symbol = this.getStock();
        frame.setBidAskPanel(symbol);
        fixEngine.requestMarketDataForSymbol(symbol);
    }

    @Override
    public void requestMarketData() {
        Symbol symbol = getStock();
        this.fixEngine.requestMarketDataForSymbol(symbol);
    }
}
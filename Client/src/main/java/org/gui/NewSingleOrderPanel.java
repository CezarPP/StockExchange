package org.gui;

import org.common.symbols.SymbolDOW;

import javax.swing.*;
import java.awt.*;

public class NewSingleOrderPanel extends JPanel {

    public NewSingleOrderPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("New Single Order"));

        // Stock dropdown
        JComboBox<SymbolDOW> stockDropdown = new JComboBox<>(SymbolDOW.values());
        add(stockDropdown);

        // Quantity label input and label
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel quantityLabel = new JLabel("Quantity");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantitySpinner);
        add(quantityPanel);

        // Price label and input
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel priceLabel = new JLabel("Price");
        JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(0.01, 0.01, 1000000.0, 0.01));
        priceSpinner.setEditor(new JSpinner.NumberEditor(priceSpinner, "0.00"));
        pricePanel.add(priceLabel);
        pricePanel.add(priceSpinner);
        add(pricePanel);

        // Buy and sell buttons
        JPanel buySellPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup buttonGroup = new ButtonGroup();
        JToggleButton buyButton = new JToggleButton("Buy");
        JToggleButton sellButton = new JToggleButton("Sell");
        buttonGroup.add(buyButton);
        buttonGroup.add(sellButton);
        buySellPanel.add(buyButton);
        buySellPanel.add(sellButton);
        add(buySellPanel);

        // Submit button
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Submit");
        submitPanel.add(submitButton);
        add(submitPanel);
    }
}
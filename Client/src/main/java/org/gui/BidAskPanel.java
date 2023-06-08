package org.gui;

import org.common.symbols.Symbol;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * There should be one panel for each stock
 */
public class BidAskPanel extends JPanel {
    private final DefaultTableModel bidTableModel;
    private final DefaultTableModel askTableModel;
    public Symbol symbol;

    public BidAskPanel(Symbol symbol) {
        this.symbol = symbol;
        setLayout(new GridLayout(1, 2));
        setBorder(BorderFactory.createTitledBorder("Bid / Ask"));

        // Bid table
        String[] bidColumnNames = {"ID", "Price", "Quantity"};
        bidTableModel = new DefaultTableModel(bidColumnNames, 0);
        JTable bidTable = new JTable(bidTableModel);
        JScrollPane bidScrollPane = new JScrollPane(bidTable);
        add(bidScrollPane);

        // Ask table
        askTableModel = new DefaultTableModel(bidColumnNames, 0);
        JTable askTable = new JTable(askTableModel);
        JScrollPane askScrollPane = new JScrollPane(askTable);
        add(askScrollPane);
    }

    public void addBid(PanelOrder panelOrder) {
        String formattedPrice = String.format("%.2f", panelOrder.price());
        addOrUpdate(bidTableModel, panelOrder.id(), formattedPrice, panelOrder.quantity());
        setVisible(true);
    }

    public void addAsk(PanelOrder panelOrder) {
        String formattedPrice = String.format("%.2f", panelOrder.price());
        addOrUpdate(askTableModel, panelOrder.id(), formattedPrice, panelOrder.quantity());
        setVisible(true);
    }

    private void addOrUpdate(DefaultTableModel tableModel, int id, String price, int quantity) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(price)) {
                tableModel.setValueAt((int) tableModel.getValueAt(i, 2) + quantity, i, 2);
                return;
            }
        }
        Object[] rowData = {id, price, quantity};
        tableModel.addRow(rowData);
        setVisible(true);
    }

    public void removeAllBids() {
        bidTableModel.setRowCount(0);
        setVisible(true);
    }

    public void removeAllAsks() {
        askTableModel.setRowCount(0);
        setVisible(true);
    }
}
package org.gui;

import org.common.symbols.Symbol;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.Vector;

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
        addOrUpdate(bidTableModel, panelOrder.id(), panelOrder.price(), panelOrder.quantity());
        sortTable(bidTableModel, (o1, o2) -> Double.compare((double) ((Vector<?>) o2).get(1), (double) ((Vector<?>) o1).get(1)));
        setVisible(true);
    }

    public void addAsk(PanelOrder panelOrder) {
        addOrUpdate(askTableModel, panelOrder.id(), panelOrder.price(), panelOrder.quantity());
        sortTable(askTableModel, Comparator.comparingDouble(o -> (double) ((Vector<?>) o).get(1)));
        setVisible(true);
    }

    private void addOrUpdate(DefaultTableModel tableModel, int id, double price, int quantity) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((double) tableModel.getValueAt(i, 1) == price) {
                tableModel.setValueAt((int) tableModel.getValueAt(i, 2) + quantity, i, 2);
                return;
            }
        }
        Object[] rowData = {id, price, quantity};
        tableModel.addRow(rowData);
        setVisible(true);
    }

    private void sortTable(DefaultTableModel tableModel, Comparator<Object> comparator) {
        tableModel.getDataVector().sort(comparator);
        tableModel.fireTableDataChanged();
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
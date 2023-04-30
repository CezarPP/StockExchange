package org.client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.Vector;

public class BidAskPanel extends JPanel {
    private final DefaultTableModel bidTableModel;
    private final DefaultTableModel askTableModel;

    public BidAskPanel() {
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

    public void addBid(int id, double price, int quantity) {
        addOrUpdate(bidTableModel, id, price, quantity);
        sortTable(bidTableModel, (o1, o2) -> Double.compare((double) ((Vector<?>) o2).get(1), (double) ((Vector<?>) o1).get(1)));
    }

    public void addAsk(int id, double price, int quantity) {
        addOrUpdate(askTableModel, id, price, quantity);
        sortTable(askTableModel, Comparator.comparingDouble(o -> (double) ((Vector<?>) o).get(1)));
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
    }

    private void sortTable(DefaultTableModel tableModel, Comparator<Object> comparator) {
        tableModel.getDataVector().sort(comparator);
        tableModel.fireTableDataChanged();
    }
}
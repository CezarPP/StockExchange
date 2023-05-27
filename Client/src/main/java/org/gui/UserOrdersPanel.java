package org.gui;

import org.common.symbols.Symbol;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserOrdersPanel extends JPanel {
    private final DefaultTableModel ordersTableModel;

    public UserOrdersPanel() {
        setLayout(new GridLayout(1, 1));
        setBorder(BorderFactory.createTitledBorder("User Orders"));

        // User Orders table
        String[] ordersColumnNames = {"Order ID", "Symbol", "Price", "Quantity", "Side"};
        ordersTableModel = new DefaultTableModel(ordersColumnNames, 0);
        JTable ordersTable = new JTable(ordersTableModel);
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        add(ordersScrollPane);
    }

    public void addOrder(int orderId, Symbol symbol, double price, int quantity, String side) {
        Object[] rowData = {orderId, symbol, price, quantity, side};
        ordersTableModel.addRow(rowData);
    }
}


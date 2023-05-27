package org.gui;

import org.client.Order;
import org.common.fix.order.Side;
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

    public void addOrder(Order order) {
        Object[] rowData = {order.exchangeOrderID, order.symbol,
                order.price, order.quantity, (order.side == Side.BUY) ? "BID" : "ASK"};
        ordersTableModel.addRow(rowData);
    }

    private int findRowByOrderId(int orderId) {
        for (int i = 0; i < ordersTableModel.getRowCount(); i++) {
            if ((int) ordersTableModel.getValueAt(i, 0) == orderId) {
                return i;
            }
        }
        return -1;
    }


    public void removeOrder(int orderId) {
        int row = findRowByOrderId(orderId);
        if (row != -1) {
            ordersTableModel.removeRow(row);
        }
    }

    public void decreaseQuantity(int orderId, int quantity) {
        int row = findRowByOrderId(orderId);
        if (row != -1) {
            int currentQuantity = (int) ordersTableModel.getValueAt(row, 2);
            int newQuantity = currentQuantity - quantity;
            if (newQuantity <= 0) {
                ordersTableModel.removeRow(row);
            } else {
                ordersTableModel.setValueAt(newQuantity, row, 2);
            }
        }
    }
}


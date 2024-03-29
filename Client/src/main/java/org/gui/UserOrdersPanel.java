package org.gui;

import org.client.Order;
import org.common.fix.order.Side;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserOrdersPanel extends JPanel {
    private final DefaultTableModel ordersTableModel;

    public UserOrdersPanel() {
        setLayout(new GridLayout(1, 1));
        setBorder(BorderFactory.createTitledBorder("User Orders"));

        // User Orders table
        String[] ordersColumnNames = {"Order ID", "Client order ID", "Symbol", "Price", "Quantity", "Side", "Confirmed"};
        ordersTableModel = new DefaultTableModel(ordersColumnNames, 0);
        JTable ordersTable = new JTable(ordersTableModel);
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        add(ordersScrollPane);
    }

    public void addOrder(Order order) {
        String formattedPrice = String.format("%.2f", order.price);
        Object[] rowData = {order.exchangeOrderID, order.clientOrderId, order.symbol,
                formattedPrice, order.quantity, (order.side == Side.BUY) ? "BID" : "ASK", "Not confirmed"};
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

    private int findRowByClientOrderId(int clientOrderId) {
        for (int i = 0; i < ordersTableModel.getRowCount(); i++) {
            if ((int) ordersTableModel.getValueAt(i, 1) == clientOrderId) {
                return i;
            }
        }
        return -1;
    }

    public void confirmOrder(int clientOrderId, int orderId) {
        int row = findRowByClientOrderId(clientOrderId);
        if (row != -1) {
            ordersTableModel.setValueAt("Confirmed", row, 6);
            ordersTableModel.setValueAt(orderId, row, 0);
        }
    }

    public void removeOrder(int orderId) {
        int row = findRowByOrderId(orderId);
        if (row != -1) {
            ordersTableModel.removeRow(row);
        }
    }

    public void decreaseQuantity(int orderId, int newQuantity) {
        int row = findRowByOrderId(orderId);
        if (row != -1) {
            if (newQuantity <= 0) {
                ordersTableModel.removeRow(row);
            } else {
                ordersTableModel.setValueAt(newQuantity, row, 4);
            }
        }
    }
}


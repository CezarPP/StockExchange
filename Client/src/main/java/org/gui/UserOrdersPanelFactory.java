package org.gui;

public class UserOrdersPanelFactory {
    private static UserOrdersPanel userOrdersPanel = null;

    public static UserOrdersPanel getUserOrdersPanel() {
        if (userOrdersPanel == null) {
            userOrdersPanel = new UserOrdersPanel();
        }
        return userOrdersPanel;
    }

}

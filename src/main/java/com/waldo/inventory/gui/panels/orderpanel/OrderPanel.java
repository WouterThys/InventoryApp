package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.DbManager;

import java.util.List;

public class OrderPanel extends OrderPanelLayout {

    public OrderPanel() {
        List<Order> orders = DbManager.db().getOrders();
        if (orders != null) {

        }
    }
}

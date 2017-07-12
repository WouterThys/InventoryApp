package com.waldo.inventory.gui.dialogs.orderitemdialog;


import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderItemDialog extends OrderItemDialogLayout {

    private Item itemToOrder;
    private List<Item> itemsToOrderList;
    private boolean orderList = false;

    public OrderItemDialog(Application application, String title, Item itemToOrder) {
        super(application, title);

        this.itemToOrder = itemToOrder;
        this.orderList = false;

        DbManager.db().addOnOrdersChangedListener(this);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    public OrderItemDialog(Application application, String title, List<Item> itemsToOrder) {
        super(application, title);

        this.itemsToOrderList = itemsToOrder;
        this.orderList = true;

        DbManager.db().addOnOrdersChangedListener(this);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    private boolean verify() {
        if (orderCb.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(OrderItemDialog.this, "Selected an order", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    protected void onOK() {
        if (verify()) {
            // Add item(s) to list
            if (orderList) {
                application.addItemsToOrder(itemsToOrderList, (Order) orderCb.getSelectedItem());
            } else {
                itemsToOrderList = new ArrayList<>(1);
                itemsToOrderList.add(itemToOrder);
                application.addItemsToOrder(itemsToOrderList, (Order) orderCb.getSelectedItem());
            }

            // Close
            DbManager.db().removeOnOrdersChangedListener(this);
            super.onOK();
        }
    }

    @Override
    protected void onCancel() {
        DbManager.db().removeOnOrdersChangedListener(this);
        super.onCancel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OrdersDialog dialog = new OrdersDialog(this, "New order", false);
        if (dialog.showDialog() == IDialog.OK) {
            Order newOrder = dialog.getOrder();

            newOrder.save();
        }
    }

    @Override
    public void onAdded(Order order) {
        updateComponents(order);
    }

    @Override
    public void onUpdated(Order newOrder, Order oldOrder) {} // Should not happen

    @Override
    public void onDeleted(Order order) {} // Should not happen
}

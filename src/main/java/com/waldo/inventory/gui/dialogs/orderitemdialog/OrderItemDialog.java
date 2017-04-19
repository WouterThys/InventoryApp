package com.waldo.inventory.gui.dialogs.orderitemdialog;


import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class OrderItemDialog extends OrderItemDialogLayout {

    public int showDialog() {
        setLocationRelativeTo(application);
        pack();
        setMinimumSize(getSize());
        setVisible(true);
        return dialogResult;
    }

    private Item itemToOrder;

    public OrderItemDialog(Application application, String title, Item itemToOrder) {
        super(application, title);

        this.itemToOrder = itemToOrder;

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
            // Add item to list
            application.addItemToOrder(itemToOrder, (Order) orderCb.getSelectedItem());

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

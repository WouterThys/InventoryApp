package com.waldo.inventory.gui.dialogs.orderitemdialog;


import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDialog extends OrderItemDialogLayout {

    private Item itemToOrder;
    private List<Item> itemsToOrderList;
    private boolean orderList = false;
    private boolean createOnConfirm = true;

    public OrderItemDialog(Application application, String title, Item itemToOrder, boolean createOnConfirm) {
        super(application, title);

        this.itemToOrder = itemToOrder;
        this.orderList = false;
        this.createOnConfirm = createOnConfirm;

        DbManager.db().addOnOrdersChangedListener(this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public OrderItemDialog(Application application, String title, List<Item> itemsToOrder, boolean createOnConfirm) {
        super(application, title);

        this.itemsToOrderList = itemsToOrder;
        this.orderList = true;
        this.createOnConfirm = createOnConfirm;

        DbManager.db().addOnOrdersChangedListener(this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public Order getSelectedOrder() {
        return (Order) orderCb.getSelectedItem();
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
            if (createOnConfirm) {
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
            }
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
        OrdersDialog dialog = new OrdersDialog(this, "New order", new Order(), false);
        if (dialog.showDialog() == IDialog.OK) {
            Order newOrder = dialog.getOrder();
            newOrder.save();
        }
    }

    @Override
    public void onInserted(Order order) {
        updateComponents(order);
    }

    @Override
    public void onUpdated(Order newOrder) {} // Should not happen

    @Override
    public void onDeleted(Order order) {} // Should not happen

    @Override
    public void onCacheCleared() {
        updateComponents();
    }
}

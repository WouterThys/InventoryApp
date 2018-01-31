package com.waldo.inventory.gui.dialogs.orderitemdialog;


import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import com.waldo.utils.icomponents.*;

public class OrderItemDialog extends OrderItemDialogLayout implements CacheChangedListener<Order> {


    private final Application application;
    private Item itemToOrder;
    private List<Item> itemsToOrderList;
    private boolean orderList = false;
    private boolean createOnConfirm = true;

    public OrderItemDialog(Application parent, String title, Item itemToOrder, boolean createOnConfirm) {
        super(parent, title);
        this.application = parent;
        this.itemToOrder = itemToOrder;
        this.orderList = false;
        this.createOnConfirm = createOnConfirm;

        addCacheListener(Order.class,this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public OrderItemDialog(Application parent, String title, List<Item> itemsToOrder, boolean createOnConfirm) {
        super(parent, title);
        this.application = parent;
        this.itemsToOrderList = itemsToOrder;
        this.orderList = true;
        this.createOnConfirm = createOnConfirm;

        addCacheListener(Order.class,this);

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
            }
            super.onOK();
        }
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

package com.waldo.inventory.gui.dialogs.addtoorderdialog;


import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.PendingOrder;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.editordersdialog.EditOrdersDialog;
import com.waldo.inventory.gui.dialogs.pendingordersdialog.PendingOrdersCacheDialog;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AddToOrderCacheDialog extends AddToOrderCacheDialogLayout implements CacheChangedListener<Order> {

    private final Application application;
    private Item itemToOrder;
    private List<Item> itemsToOrderList;
    private boolean orderList;
    private boolean createOnConfirm;

    public AddToOrderCacheDialog(Application parent, String title, Item itemToOrder, boolean createOnConfirm, boolean pendingOption) {
        super(parent, title, pendingOption, Statics.DistributorType.Items);
        this.application = parent;
        this.itemToOrder = itemToOrder;
        this.orderList = false;
        this.createOnConfirm = createOnConfirm;

        addCacheListener(Order.class, this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public AddToOrderCacheDialog(Application parent, String title, List<Item> itemsToOrder, boolean createOnConfirm, boolean pendingOption) {
        super(parent, title, pendingOption, Statics.DistributorType.Items);
        this.application = parent;
        this.itemsToOrderList = itemsToOrder;
        this.orderList = true;
        this.createOnConfirm = createOnConfirm;

        addCacheListener(Order.class, this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public Order getSelectedOrder() {
        return (Order) orderCb.getSelectedItem();
    }

    @Override
    void addToOrder() {
        if (orderCb.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(AddToOrderCacheDialog.this, "Selected an order", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
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

    @Override
    void addToPending() {
        if (distributorCb.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(AddToOrderCacheDialog.this, "Selected a distributor", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Distributor distributor = (Distributor) distributorCb.getSelectedItem();
        List<Item> itemsToPending = new ArrayList<>();
        if (orderList) {
            itemsToPending.addAll(itemsToOrderList);
        } else {
            itemsToPending.add(itemToOrder);
        }

        List<PendingOrder> pendingOrders = new ArrayList<>();
        for (Item item : itemsToPending) {
            PendingOrder pendingOrder = new PendingOrder(item, distributor);
            pendingOrders.add(pendingOrder);
        }

        super.onOK();

        PendingOrdersCacheDialog dialog = new PendingOrdersCacheDialog(application, "Pending orders", pendingOrders);
        dialog.showDialog();
    }

    @Override
    void addNewOrder() {
        EditOrdersDialog dialog = new EditOrdersDialog(this, new Order(), distributorType,false);
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
    public void onUpdated(Order newOrder) {
    } // Should not happen

    @Override
    public void onDeleted(Order order) {
    } // Should not happen

    @Override
    public void onCacheCleared() {
        updateComponents();
    }
}
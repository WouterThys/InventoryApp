package com.waldo.inventory.gui.dialogs.addtoorderdialog;


import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.dialogs.editordersdialog.EditOrdersDialog;
import com.waldo.inventory.managers.OrderManager;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddToOrderDialog<T extends Orderable> extends AddToOrderDialogLayout<T> {

    private T orderLine;
    private List<T> orderLineList;
    private boolean orderList;
    private boolean createOnConfirm;

    public AddToOrderDialog(Window parent, T orderLine, boolean createOnConfirm) {
        super(parent, "Order " + orderLine);
        this.orderLine = orderLine;
        this.orderList = false;
        this.createOnConfirm = createOnConfirm;

        if (orderLine instanceof Item) {
            distributorType = Statics.DistributorType.Items;
            addCacheListener(ItemOrder.class, new CacheChangedListener<ItemOrder>() {
                @Override
                public void onInserted(ItemOrder object) {
                    updateComponents(object);
                }

                @Override
                public void onUpdated(ItemOrder object) {

                }

                @Override
                public void onDeleted(ItemOrder object) {

                }

                @Override
                public void onCacheCleared() {

                }
            });
        } else {
            distributorType = Statics.DistributorType.Pcbs;
            addCacheListener(PcbOrder.class, new CacheChangedListener<PcbOrder>() {
                @Override
                public void onInserted(PcbOrder object) {
                    updateComponents(object);
                }

                @Override
                public void onUpdated(PcbOrder object) {

                }

                @Override
                public void onDeleted(PcbOrder object) {

                }

                @Override
                public void onCacheCleared() {

                }
            });
        }

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public AddToOrderDialog(Window parent, List<T> itemsToOrder, boolean createOnConfirm) {
        super(parent, "Order " + itemsToOrder.size() + " items");
        this.orderLineList = itemsToOrder;
        this.orderList = true;
        this.createOnConfirm = createOnConfirm;

        if (orderLine instanceof Item) {
            distributorType = Statics.DistributorType.Items;
            addCacheListener(ItemOrder.class, new CacheChangedListener<ItemOrder>() {
                @Override
                public void onInserted(ItemOrder object) {
                    updateComponents(object);
                }

                @Override
                public void onUpdated(ItemOrder object) {

                }

                @Override
                public void onDeleted(ItemOrder object) {

                }

                @Override
                public void onCacheCleared() {

                }
            });
        } else {
            distributorType = Statics.DistributorType.Pcbs;
            addCacheListener(PcbOrder.class, new CacheChangedListener<PcbOrder>() {
                @Override
                public void onInserted(PcbOrder object) {
                    updateComponents(object);
                }

                @Override
                public void onUpdated(PcbOrder object) {

                }

                @Override
                public void onDeleted(PcbOrder object) {

                }

                @Override
                public void onCacheCleared() {

                }
            });
        }

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public AbstractOrder<T> getSelectedOrder() {
        return (AbstractOrder<T>) orderCb.getSelectedItem();
    }

    @Override
    protected void onOK() {
        if (orderCb.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(AddToOrderDialog.this, "Selected an order", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (createOnConfirm) {
            // Add item(s) to list
            if (orderList) {
                OrderManager.addLinesToOrder(orderLineList, (AbstractOrder<T>) orderCb.getSelectedItem());
            } else {
                OrderManager.addLineToOrder(orderLine, (AbstractOrder<T>) orderCb.getSelectedItem());
            }
        }
        super.onOK();
    }

    @Override
    void addNewOrder() {

        AbstractOrder<T> order;
        if (distributorType.equals(Statics.DistributorType.Items)) {
            order = (AbstractOrder<T>) new ItemOrder();
        } else {
            order = (AbstractOrder<T>) new PcbOrder();
        }

        EditOrdersDialog dialog = new EditOrdersDialog(this,order, distributorType,false);
        if (dialog.showDialog() == IDialog.OK) {
            order.save();
        }

    }
}

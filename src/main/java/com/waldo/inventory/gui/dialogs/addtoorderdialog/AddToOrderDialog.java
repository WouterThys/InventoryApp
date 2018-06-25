package com.waldo.inventory.gui.dialogs.addtoorderdialog;


import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.dialogs.editordersdialog.EditOrdersDialog;
import com.waldo.inventory.managers.OrderManager;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddToOrderDialog extends AddToOrderDialogLayout implements CacheChangedListener<ItemOrder> {

    private Item itemToOrder;
    private List<Item> itemsToOrderList;
    private boolean orderList;
    private boolean createOnConfirm;

    public AddToOrderDialog(Window parent, Item itemToOrder, boolean createOnConfirm) {
        super(parent, "Order " + itemToOrder, Statics.DistributorType.Items);
        this.itemToOrder = itemToOrder;
        this.orderList = false;
        this.createOnConfirm = createOnConfirm;

        addCacheListener(ItemOrder.class, this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public AddToOrderDialog(Window parent, List<Item> itemsToOrder, boolean createOnConfirm) {
        super(parent, "Order " + itemsToOrder.size() + " items", Statics.DistributorType.Items);
        this.itemsToOrderList = itemsToOrder;
        this.orderList = true;
        this.createOnConfirm = createOnConfirm;

        addCacheListener(ItemOrder.class, this);

        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    public ItemOrder getSelectedOrder() {
        return (ItemOrder) orderCb.getSelectedItem();
    }

    @Override
    void addToOrder() {
        if (orderCb.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(AddToOrderDialog.this, "Selected an order", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (createOnConfirm) {
            // Add item(s) to list
            if (orderList) {
                OrderManager.addItemsToOrder(itemsToOrderList, (ItemOrder) orderCb.getSelectedItem());
            } else {
                itemsToOrderList = new ArrayList<>(1);
                itemsToOrderList.add(itemToOrder);
                OrderManager.addItemsToOrder(itemsToOrderList, (ItemOrder) orderCb.getSelectedItem());
            }
        }
        super.onOK();
    }

    @Override
    void addNewOrder() {
        EditOrdersDialog dialog = new EditOrdersDialog(this, new ItemOrder(), distributorType,false);
        if (dialog.showDialog() == IDialog.OK) {
            ItemOrder newItemOrder = dialog.getOrder();
            newItemOrder.save();
        }
    }

    @Override
    public void onInserted(ItemOrder itemOrder) {
        updateComponents(itemOrder);
    }

    @Override
    public void onUpdated(ItemOrder newItemOrder) {
    } // Should not happen

    @Override
    public void onDeleted(ItemOrder itemOrder) {
    } // Should not happen

    @Override
    public void onCacheCleared() {
        updateComponents();
    }
}

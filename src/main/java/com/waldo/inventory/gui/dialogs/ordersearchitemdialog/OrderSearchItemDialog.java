package com.waldo.inventory.gui.dialogs.ordersearchitemdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.ArrayList;
import java.util.List;

public class OrderSearchItemDialog extends OrderSearchItemDialogLayout {

    private Item itemToOrder;

    public OrderSearchItemDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    private boolean verify() {
        if (itemToOrder == null) {
            JOptionPane.showMessageDialog(this, "Select an item to order", "No item", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public Item getItemToOrder() {
        return itemToOrder;
    }

    @Override
    protected void onOK() {
        if (verify()) {
            super.onOK();
        }
    }

    //
    // SEARCH LISTENER
    //
    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        itemToOrder = null;
        List<Item> foundItems = new ArrayList<>(foundObjects.size());
        for (DbObject object : foundObjects) {
            foundItems.add((Item)object);
        }
        resultTableModel.setItemList(foundItems);
    }

    @Override
    public void onSearchCleared() {

    }

    //
    // LIST SELECTION LISTENER
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            int row = resultTable.getSelectedRow();
            if (row >= 0) {
                itemToOrder = resultTableModel.getItem(row);
            }
        }
    }
}

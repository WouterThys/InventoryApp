package com.waldo.inventory.gui.dialogs.ordersearchitemdialog;

import com.waldo.inventory.classes.dbclasses.Item;
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
        updateComponents();
    }

    private boolean verify() {
        if (itemToOrder == null) {
            JOptionPane.showMessageDialog(this, "Select an item to order", "No item", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public List<Item> getItemsToOrder() {
        List<Item> itemsToOrder = new ArrayList<>(); // TODO: select multiple items, with something like from left to right column
        itemsToOrder.add(itemToOrder);
        return itemsToOrder;
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
    public void onObjectsFound(List<Item> foundObjects) {
        itemToOrder = null;
        resultTableModel.setItemList(foundObjects);
    }

    @Override
    public void onSearchCleared() {

    }

    @Override
    public void onNextSearchObject(Item next) {

    }

    @Override
    public void onPreviousSearchObject(Item previous) {

    }

    //
    // LIST SELECTION LISTENER
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            int row = resultTable.getSelectedRow();
            if (row >= 0) {
                //itemToOrder = resultTableModel.getItem(row);
                itemToOrder = (Item) resultTable.getModel().getValueAt(resultTable.convertRowIndexToModel(row), 0);
            }
        }
    }
}

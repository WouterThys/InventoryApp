package com.waldo.inventory.gui.dialogs.pendingordersdialog;

import com.waldo.inventory.classes.dbclasses.PendingOrder;
import com.waldo.inventory.database.interfaces.CacheChangedListener;

import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class PendingOrdersDialog extends PendingOrdersDialogLayout implements CacheChangedListener<PendingOrder> {

    public PendingOrdersDialog(Window window, String title) {
        super(window, title);

        initializeComponents();
        initializeLayouts();

        addCacheListener(PendingOrder.class, this);

        updateComponents();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selectedPendingOrder = pendingOrderTable.getSelectedItem();
        }
    }

    @Override
    public void onInserted(PendingOrder pendingOrder) {
        tableModel.addItem(pendingOrder);
        tableModel.updateTable();
    }

    @Override
    public void onUpdated(PendingOrder pendingOrder) {
        tableModel.updateTable();
    }

    @Override
    public void onDeleted(PendingOrder pendingOrder) {
        tableModel.removeItem(pendingOrder);
        tableModel.updateTable();
    }

    @Override
    public void onCacheCleared() {

    }
}

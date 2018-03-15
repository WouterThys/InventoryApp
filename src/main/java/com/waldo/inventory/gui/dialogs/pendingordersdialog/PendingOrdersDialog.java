package com.waldo.inventory.gui.dialogs.pendingordersdialog;

import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.PendingOrder;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.editpendingorderdialog.EditPendingOrderDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class PendingOrdersDialog extends PendingOrdersDialogLayout implements CacheChangedListener<PendingOrder> {

    public PendingOrdersDialog(Application application, String title) {
        this(application, title, null);
    }

    public PendingOrdersDialog(Application application, String title, List<PendingOrder> pendingOrderList) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        addCacheListener(PendingOrder.class, this);
        updateComponents();

        if (pendingOrderList != null) {
            for (PendingOrder po : pendingOrderList) {
                PendingOrder p = SearchManager.sm().findPendingOrderByItemAndDistributor(po.getItemId(), po.getDistributorId());
                if (p== null) {
                    po.save();
                }
            }
        }
    }

    @Override
    void orderItems(List<PendingOrder> pendingOrders) {
        if (pendingOrders != null && pendingOrders.size() > 0) {
            // Get distributor
            List<Item> itemsToOrder = new ArrayList<>();
            for (PendingOrder po : pendingOrders) {
                itemsToOrder.add(po.getItem());
            }

            OrderItemDialog dialog = new OrderItemDialog(application, "Order", itemsToOrder, true, false);
            if (dialog.showDialog() == IDialog.OK) {
                for (PendingOrder po : pendingOrders) {
                    po.delete();
                }
            }
        }
    }

    //
    // List selection
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selectedPendingOrder = pendingOrderTable.getSelectedItem();
            updateEnabledComponents();
        }
    }

    //
    // Cache changed
    //
    @Override
    public void onInserted(PendingOrder pendingOrder) {
        tableModel.addItem(pendingOrder);
        pendingOrderTable.selectItem(pendingOrder);
    }

    @Override
    public void onUpdated(PendingOrder pendingOrder) {
        tableModel.updateTable();
    }

    @Override
    public void onDeleted(PendingOrder pendingOrder) {
        tableModel.removeItem(pendingOrder);
        selectedPendingOrder = null;
    }

    @Override
    public void onCacheCleared() {

    }

    public void addPendingOrders(Item item, Distributor distributor) {

    }

    private void removePendingOrders(List<PendingOrder> pendingOrders) {
        String message = "";
        boolean delete = true;

        if (pendingOrders.size() == 1) {
            message = "Delete " + pendingOrders.get(0) + "?";
        } else if (pendingOrders.size() > 1) {
            message = "Delete " + pendingOrders.size() + " selected items?";
        } else {
            delete = false;
        }

        delete &= JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                this,
                message,
                "Delete",
                JOptionPane.YES_NO_OPTION);

        if (delete) {
            for (PendingOrder po : pendingOrders) {
                po.delete();
            }
        }
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        cache().getPendingOrders().clear();
        updateComponents();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        PendingOrder pendingOrder = new PendingOrder();
        EditPendingOrderDialog dialog = new EditPendingOrderDialog(this, "Add", pendingOrder);
        if (dialog.showDialog() == IDialog.OK) {
           pendingOrder.save();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        List<PendingOrder> selectedItems = getSelectedPendingOrders();
        if (selectedItems != null) {
            removePendingOrders(selectedItems);
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        EditPendingOrderDialog dialog = new EditPendingOrderDialog(this, "Add", selectedPendingOrder);
        if (dialog.showDialog() == IDialog.OK) {
            selectedPendingOrder.save();
        }
    }
}

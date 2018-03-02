package com.waldo.inventory.gui.dialogs.editpendingorderdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.PendingOrder;

import javax.swing.*;
import java.awt.*;

import static com.waldo.inventory.managers.SearchManager.sm;

public class EditPendingOrderDialog extends EditPendingOrderDialogLayout {


    public EditPendingOrderDialog(Window window, String title, PendingOrder pendingOrder) {
        super(window, title, pendingOrder);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

    @Override
    protected void onOK() {
        PendingOrder foundOrder = sm().findPendingOrderByItemAndDistributor(pendingOrder.getItemId(), pendingOrder.getDistributorId());
        if (foundOrder != null && (pendingOrder.getId() > DbObject.UNKNOWN_ID || foundOrder.getId() != pendingOrder.getId())) {
            JOptionPane.showMessageDialog(
                    EditPendingOrderDialog.this,
                    "Pending item already included for this distributor..",
                    "Already included",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            super.onOK();
        }
    }
}

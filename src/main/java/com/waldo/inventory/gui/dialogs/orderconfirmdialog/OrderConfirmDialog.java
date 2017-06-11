package com.waldo.inventory.gui.dialogs.orderconfirmdialog;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.Application;

public class OrderConfirmDialog extends OrderConfirmDialogLayout {

    public OrderConfirmDialog(Application application, String title, Order order) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(order);
    }

    @Override
    protected void onOK() {
        if (order != null) {
            order.setTrackingNumber(trackingNrTf.getText());
            order.setOrderReference(referenceTf.getText());
            order.save();
        }

        dialogResult = OK;
        dispose();
    }
}

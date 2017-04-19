package com.waldo.inventory.gui.dialogs.ordersdialog;


import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.Application;

import javax.swing.*;

public class OrdersDialog extends OrdersDialogLayout {

    public int showDialog() {
        setLocationRelativeTo(application);
        pack();
        setMinimumSize(getSize());
        setVisible(true);
        return dialogResult;
    }

    private Order order;

    public OrdersDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    public Order getOrder() {
        return order;
    }


    private boolean verify() {
        String name = nameField.getText();
        if (name != null && !name.isEmpty()) {
            return true;
        } else {
            nameField.setError("Name can't be empty..");
            return false;
        }
    }

    @Override
    protected void onOK() {
        if (verify()) {
            order = new Order();
            order.setName(nameField.getText());
            order.setDistributor((Distributor) distributorCb.getSelectedItem());

            dialogResult = OK;
            dispose();
        }
    }
}

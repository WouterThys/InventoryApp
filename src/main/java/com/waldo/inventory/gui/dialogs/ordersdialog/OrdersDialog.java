package com.waldo.inventory.gui.dialogs.ordersdialog;


import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;

public class OrdersDialog extends OrdersDialogLayout {

    public int showDialog() {
        setLocationRelativeTo(application);
        pack();
        setMinimumSize(getSize());
        setVisible(true);
        return dialogResult;
    }

    private Order order;

    public OrdersDialog(Application application, String title, boolean showDates) {
        super(application, title, showDates);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    public OrdersDialog(Dialog dialog, String title, boolean showDates) {
        super(dialog, title, showDates);

        initializeComponents();
        initializeLayouts();
        updateComponents(null);
    }

    public Order getOrder() {
        return order;
    }


    private boolean verify() {
        boolean ok = true;
        String name = nameField.getText();
        if (name == null || name.isEmpty()) {
            nameField.setError("Name can't be empty..");
            ok = false;
        }

        if (showDates && isOrderedCb.isSelected()) {
            Date ordered = (Date) orderedDatePicker.getModel().getValue();
            Date received = (Date) receivedDatePicker.getModel().getValue();

            if (ordered == null) {
                JOptionPane.showMessageDialog(this, "Fill in ordered date you fool!", "Error", JOptionPane.ERROR_MESSAGE);
                ok = false;
                if (ordered.after(new Date(System.currentTimeMillis()))) {
                    JOptionPane.showMessageDialog(this, "Fill in date before today retard!", "Error", JOptionPane.ERROR_MESSAGE);
                    ok = false;
                }
            }

            if (received == null) {
                JOptionPane.showMessageDialog(this, "Fill in received date you fool!", "Error", JOptionPane.ERROR_MESSAGE);
                ok = false;
            }

            if (ordered != null && received != null) {
                if (ordered.after(received)) {
                    JOptionPane.showMessageDialog(this, "Fill in received after orderd date you fool!", "Error", JOptionPane.ERROR_MESSAGE);
                    ok = false;
                }
            }
        }

        return ok;
    }

    @Override
    protected void onOK() {
        if (verify()) {
            order = new Order();
            order.setName(nameField.getText());
            order.setDistributor((Distributor) distributorCb.getSelectedItem());

            order.setDateOrdered((Date) orderedDatePicker.getModel().getValue());
            order.setDateReceived((Date) receivedDatePicker.getModel().getValue());

            dialogResult = OK;
            dispose();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCheckBox jcb  = (JCheckBox) e.getSource();
        enableDatePickers(jcb.isSelected());
    }
}

package com.waldo.inventory.gui.dialogs.ordersdialog;


import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;

import static com.waldo.inventory.managers.CacheManager.cache;

public class OrdersDialog extends OrdersDialogLayout {

    public OrdersDialog(Application application, String title, Order order, boolean showDates) {
        super(application, title, showDates);

        initializeComponents();
        initializeLayouts();
        updateComponents(order);
    }

    public OrdersDialog(Application application, String title, Order order) {
        super(application, title, false);

        initializeComponents();
        initializeLayouts();
        updateComponents(order);
    }

    public OrdersDialog(Dialog dialog, String title, Order order, boolean showDates) {
        super(dialog, title, showDates);

        initializeComponents();
        initializeLayouts();
        updateComponents(order);
    }

    public Order getOrder() {
        return order;
    }


    private boolean verify() {
        boolean ok = true;
        if (order.getName().isEmpty()) {
            nameField.setError("Name can't be empty..");
            ok = false;
        }

        if (order.getId() <= DbObject.UNKNOWN_ID) {
            for (Order o : cache().getOrders()) {
                if (o.getName().equals(order.getName())) {
                    nameField.setError("Name already exists in orders, select an other name..");
                    ok = false;
                }
            }
        }

        if (showDates && isOrderedCb.isSelected()) {
            Date ordered = (Date) orderedDatePicker.getModel().getValue();
            Date received = (Date) receivedDatePicker.getModel().getValue();

            if (ordered == null) {
                JOptionPane.showMessageDialog(this, "Fill in ordered date you fool!", "Error", JOptionPane.ERROR_MESSAGE);
                ok = false;
            } else {
                if (ordered.after(new Date(System.currentTimeMillis()))) {
                    JOptionPane.showMessageDialog(this, "Fill in date before today retard!", "Error", JOptionPane.ERROR_MESSAGE);
                    ok = false;
                }
            }

            if (received == null) {
                JOptionPane.showMessageDialog(this, "Fill in received date you fool!", "Error", JOptionPane.ERROR_MESSAGE);
                ok = false;
            } else {
                if (ordered != null && ordered.after(received)) {
                    JOptionPane.showMessageDialog(this, "Fill in received after ordered date you fool!", "Error", JOptionPane.ERROR_MESSAGE);
                    ok = false;
                }
            }
        }

        return ok;
    }

    @Override
    protected void onOK() {
        if (verify()) {
            if (showDates && isOrderedCb.isSelected()) {
                order.setDateOrdered((Date) orderedDatePicker.getModel().getValue());
                order.setDateReceived((Date) receivedDatePicker.getModel().getValue());
            }

            super.onOK();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCheckBox jcb  = (JCheckBox) e.getSource();
        enableDatePickers(jcb.isSelected());
    }

    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {

    }

    @Override
    public DbObject getGuiObject() {
        return order;
    }
}

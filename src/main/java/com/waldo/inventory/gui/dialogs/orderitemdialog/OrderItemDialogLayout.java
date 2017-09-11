package com.waldo.inventory.gui.dialogs.orderitemdialog;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class OrderItemDialogLayout extends IDialog implements
        ActionListener,
        DbObjectChangedListener<Order> {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel textLabel;
    private JButton addNewOrderButton;
    IComboBox<Order> orderCb;

    /*
    *                  CONSTRUCTOR
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderItemDialogLayout(Application application, String title) {
        super(application, title);
        showTitlePanel(false);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        textLabel = new ILabel("Select an order, or add a new one.");
        addNewOrderButton = new JButton("Add new");
        addNewOrderButton.addActionListener(this);
        orderCb = new IComboBox<>(DbManager.db().getOrders(), new Order.SortUnordered(), false);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Text
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        getContentPanel().add(textLabel, gbc);

        // Combobox
        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(orderCb, gbc);

        // Button
        gbc.gridx = 1; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        getContentPanel().add(addNewOrderButton, gbc);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            orderCb.setSelectedItem(object);
        }
    }
}

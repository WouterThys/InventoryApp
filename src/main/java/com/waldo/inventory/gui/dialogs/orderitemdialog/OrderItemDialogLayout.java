package com.waldo.inventory.gui.dialogs.orderitemdialog;

import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.utils.GuiUtils;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ILabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import static com.waldo.inventory.managers.CacheManager.cache;

abstract class OrderItemDialogLayout extends IDialog implements
        ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ILabel textLabel;
    private IActions.AddAction addNewOrderAction;
    IComboBox<Order> orderCb;

    private IActions.AddToPendingOrderAction addToPendingOrderAction;

    /*
    *                  CONSTRUCTOR
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    OrderItemDialogLayout(Window parent, String title) {
        super(parent, title);
        showTitlePanel(false);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        textLabel = new ILabel("Select an order, or add a new one.");

        addNewOrderAction = new IActions.AddAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderItemDialogLayout.this.actionPerformed(e);
            }
        };

        addToPendingOrderAction = new IActions.AddToPendingOrderAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        orderCb = new IComboBox<>();
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

        // Combo box
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
        getContentPanel().add(GuiUtils.createNewToolbar(addNewOrderAction, addToPendingOrderAction), gbc);

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        Vector<Order> orders = new Vector<>();
        for (Order o : cache().getOrders()) {
            if (!o.isUnknown() && !o.isOrdered()) {
                orders.addElement(o);
            }
        }
        orders.sort(new Order.SortUnordered());
        DefaultComboBoxModel<Order> orderCbModel = new DefaultComboBoxModel<>(orders);
        orderCb.setModel(orderCbModel);
        if (object.length != 0 && object[0] != null) {
            orderCb.setSelectedItem(object);
        }
    }
}

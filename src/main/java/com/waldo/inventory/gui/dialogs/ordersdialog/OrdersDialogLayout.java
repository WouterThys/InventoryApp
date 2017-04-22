package com.waldo.inventory.gui.dialogs.ordersdialog;


import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILabel;
import com.waldo.inventory.gui.components.ITextField;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.SqlDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class OrdersDialogLayout extends IDialog
        implements GuiInterface, ActionListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField nameField;
    JComboBox<Distributor> distributorCb;
    private DefaultComboBoxModel<Distributor> distributorCbModel;

    JCheckBox isOrderedCb;
    JDatePickerImpl orderedDatePicker;
    JDatePickerImpl receivedDatePicker;


     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    boolean showDates;

    /*
    *                  CONSTRUCTORS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public OrdersDialogLayout(Application application, String title, boolean showDates) {
        super(application, title);
        this.showDates = showDates;
        showTitlePanel(false);
    }

    public OrdersDialogLayout(Dialog dialog, String title, boolean showDates) {
        super(dialog, title);
        this.showDates = showDates;
        showTitlePanel(false);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    protected void enableDatePickers(boolean enable) {
        orderedDatePicker.getComponent(0).setEnabled(enable);
        orderedDatePicker.getComponent(1).setEnabled(enable);
        receivedDatePicker.getComponent(0).setEnabled(enable);
        receivedDatePicker.getComponent(1).setEnabled(enable);
    }


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        nameField = new ITextField("Order name");
        distributorCbModel = new DefaultComboBoxModel<>();
        distributorCb = new JComboBox<>(distributorCbModel);

        isOrderedCb = new JCheckBox("Is ordered");
        isOrderedCb.addActionListener(this);

        SqlDateModel model = new SqlDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        orderedDatePicker = new JDatePickerImpl(datePanel);

        model = new SqlDateModel();
        datePanel = new JDatePanelImpl(model);
        receivedDatePicker = new JDatePickerImpl(datePanel);

        enableDatePickers(false);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Name
        ILabel nameLabel = new ILabel("Name: ");
        nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        nameLabel.setVerticalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(nameField, gbc);

        // Distributor
        ILabel distributorLabel = new ILabel("Distributor: ");
        distributorLabel.setHorizontalAlignment(JLabel.RIGHT);
        distributorLabel.setVerticalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(distributorLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        getContentPanel().add(distributorCb, gbc);

        if (showDates) {
            // Date check box
            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.gridy = 2;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            isOrderedCb.setHorizontalAlignment(JLabel.RIGHT);
            isOrderedCb.setVerticalAlignment(JLabel.CENTER);
            getContentPanel().add(isOrderedCb, gbc);

            // Date ordered
            ILabel orderedLabel = new ILabel("Date ordered: ");
            orderedLabel.setHorizontalAlignment(JLabel.RIGHT);
            orderedLabel.setVerticalAlignment(JLabel.CENTER);
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.gridy = 3;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            getContentPanel().add(orderedLabel, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.gridy = 3;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            getContentPanel().add(orderedDatePicker, gbc);

            // Date received
            ILabel receivedLabel = new ILabel("Date received: ");
            receivedLabel.setHorizontalAlignment(JLabel.RIGHT);
            receivedLabel.setVerticalAlignment(JLabel.CENTER);
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.gridy = 4;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            getContentPanel().add(receivedLabel, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.gridy = 4;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            getContentPanel().add(receivedDatePicker, gbc);
        }

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    }

    @Override
    public void updateComponents(Object object) {
        distributorCbModel.removeAllElements();
        for (Distributor d : DbManager.db().getDistributors()) {
            if (d.getId() != DbObject.UNKNOWN_ID) {
                distributorCbModel.addElement(d);
            }
        }

        if (object != null) {
            nameField.setText(((Order) object).getName());
            distributorCb.setSelectedItem(((Order) object).getDistributor());
        }
    }
}

package com.waldo.inventory.gui.dialogs.ordersdialog;


import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Distributor;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
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
    IComboBox<Distributor> distributorCb;

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
    OrdersDialogLayout(Application application, String title, boolean showDates) {
        super(application, title);
        this.showDates = showDates;
        showTitlePanel(false);
    }

    OrdersDialogLayout(Dialog dialog, String title, boolean showDates) {
        super(dialog, title);
        this.showDates = showDates;
        showTitlePanel(false);
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void enableDatePickers(boolean enable) {
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
        distributorCb = new IComboBox<>(DbManager.db().getDistributors(), new DbObject.DbObjectNameComparator<>(), false);

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

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(getContentPanel());
        gbc.addLine("Name: ", nameField);
        gbc.addLine("Distributor: ", distributorCb);

        if (showDates) {
            // Date check box
            gbc.gridx = 1;gbc.weightx = 0;
            gbc.gridy = 2;gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            isOrderedCb.setHorizontalAlignment(JLabel.RIGHT);
            isOrderedCb.setVerticalAlignment(JLabel.CENTER);
            getContentPanel().add(isOrderedCb, gbc);

            // Date ordered
            gbc.addLine("Date ordered: ", orderedDatePicker);
            gbc.addLine("Date received: ", receivedDatePicker);
        }

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            nameField.setText(((Order) object).getName());
            distributorCb.setSelectedItem(((Order) object).getDistributor());
        }
    }
}

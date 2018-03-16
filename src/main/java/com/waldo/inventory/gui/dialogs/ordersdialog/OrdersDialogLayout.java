package com.waldo.inventory.gui.dialogs.ordersdialog;


import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.Application;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.IDialog;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.ITextField;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.SqlDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.waldo.inventory.managers.CacheManager.cache;

abstract class OrdersDialogLayout extends IDialog
        implements GuiUtils.GuiInterface, ActionListener, IEditedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ITextField nameField;
    private IComboBox<Distributor> distributorCb;

    JCheckBox isOrderedCb;
    JDatePickerImpl orderedDatePicker;
    JDatePickerImpl receivedDatePicker;


     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
     final boolean showDates;
    Order order;

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
        nameField.addEditedListener(this, "name");
        distributorCb = new IComboBox<>(cache().getDistributors(), new DbObjectNameComparator<>(), false);
        distributorCb.addEditedListener(this, "distributorId");

        isOrderedCb = new JCheckBox("Is ordered");
        isOrderedCb.addActionListener(this);

        SqlDateModel model = new SqlDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        orderedDatePicker = new JDatePickerImpl(datePanel);
        // TODO: with edited listener

        model = new SqlDateModel();
        datePanel = new JDatePanelImpl(model);
        receivedDatePicker = new JDatePickerImpl(datePanel);
        // TODO: with edited listener

        enableDatePickers(false);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new GridBagLayout());

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(getContentPanel());
        gbc.addLine("Name: ", nameField);
        gbc.addLine("Distributor: ", distributorCb);

        if (showDates) {
            // Date check box
            gbc.gridx = 1;gbc.weightx = 0;
            gbc.gridy += 1; gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            isOrderedCb.setHorizontalAlignment(JLabel.RIGHT);
            isOrderedCb.setVerticalAlignment(JLabel.CENTER);
            getContentPanel().add(isOrderedCb, gbc);

            // Date ordered
            gbc.gridx = 0;
            gbc.gridy += 1;
            gbc.addLine("Date ordered: ", orderedDatePicker);
            gbc.addLine("Date received: ", receivedDatePicker);
        }

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            order = (Order) object[0];
            nameField.setText(order.getName());
            distributorCb.setSelectedItem(order.getDistributor());
        }
    }
}

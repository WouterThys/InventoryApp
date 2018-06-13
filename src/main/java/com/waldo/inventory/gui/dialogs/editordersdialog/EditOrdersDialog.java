package com.waldo.inventory.gui.dialogs.editordersdialog;


import com.waldo.inventory.Utils.ComparatorUtils;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Distributor;
import com.waldo.inventory.classes.dbclasses.ItemOrder;
import com.waldo.inventory.gui.components.IObjectDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IComboBox;
import com.waldo.utils.icomponents.ITextField;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.SqlDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

public class EditOrdersDialog extends IObjectDialog<ItemOrder> implements ActionListener {

    private ITextField nameField;
    private IComboBox<Distributor> distributorCb;

    private JCheckBox isOrderedCb;
    private JDatePickerImpl orderedDatePicker;
    private JDatePickerImpl receivedDatePicker;

    private final boolean showDates;
    private final DistributorType type;

    public EditOrdersDialog(Window window, ItemOrder itemOrder, DistributorType type, boolean showDates) {
        super(window, "ItemOrder", itemOrder, ItemOrder.class);
        this.showDates = showDates;
        this.type = type;
        initializeComponents();
        initializeLayouts();
        updateComponents(itemOrder);
    }

    public ItemOrder getOrder() {
        return getObject();
    }

    private void enableDatePickers(boolean enable) {
        orderedDatePicker.getComponent(0).setEnabled(enable);
        orderedDatePicker.getComponent(1).setEnabled(enable);
        receivedDatePicker.getComponent(0).setEnabled(enable);
        receivedDatePicker.getComponent(1).setEnabled(enable);
    }

    @Override
    public VerifyState verify(ItemOrder toVerify) {
        VerifyState ok = VerifyState.Ok;

        String name = getOrder().getName();
        if (name.isEmpty()) {
            nameField.setError("Name can't be empty..");
            ok = VerifyState.Error;
        } else if (getOrder().getId() <= DbObject.UNKNOWN_ID) {
            ItemOrder foundItemOrder = SearchManager.sm().findOrderByName(name);
            if (foundItemOrder != null) {
                nameField.setError("Name already exists..");
                ok = VerifyState.Error;
            }
        }

        if (showDates && isOrderedCb.isSelected()) {
            Date ordered = (Date) orderedDatePicker.getModel().getValue();
            Date received = (Date) receivedDatePicker.getModel().getValue();

            if (ordered == null) {
                JOptionPane.showMessageDialog(this, "Fill in ordered date you fool!", "Error", JOptionPane.ERROR_MESSAGE);
                ok = VerifyState.Error;
            } else {
                if (ordered.after(new Date(System.currentTimeMillis()))) {
                    JOptionPane.showMessageDialog(this, "Fill in date before today retard!", "Error", JOptionPane.ERROR_MESSAGE);
                    ok = VerifyState.Error;
                }
            }

            if (received == null) {
                JOptionPane.showMessageDialog(this, "Fill in received date you fool!", "Error", JOptionPane.ERROR_MESSAGE);
                ok = VerifyState.Error;
            } else {
                if (ordered != null && ordered.after(received)) {
                    JOptionPane.showMessageDialog(this, "Fill in received after ordered date you fool!", "Error", JOptionPane.ERROR_MESSAGE);
                    ok = VerifyState.Error;
                }
            }
        }

        return ok;
    }

    @Override
    public void initializeComponents() {
        nameField = new ITextField("ItemOrder name");
        nameField.addEditedListener(this, "name");

        distributorCb = new IComboBox<>(SearchManager.sm().findDistributorsByType(type), new ComparatorUtils.DbObjectNameComparator<>(), false);
        distributorCb.addEditedListener(this, "distributorId");

        isOrderedCb = new JCheckBox("Is ordered");
        isOrderedCb.addActionListener(this);

        SqlDateModel model = new SqlDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        orderedDatePicker = new JDatePickerImpl(datePanel);
        orderedDatePicker.addActionListener(this);

        model = new SqlDateModel();
        datePanel = new JDatePanelImpl(model);
        receivedDatePicker = new JDatePickerImpl(datePanel);
        receivedDatePicker.addActionListener(this);

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
    public void updateComponents(Object... objects) {
        if (getOrder() != null) {
            nameField.setText(getOrder().getName());
            distributorCb.setSelectedItem(getOrder().getDistributor());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(isOrderedCb)) {
            JCheckBox jcb = (JCheckBox) e.getSource();
            enableDatePickers(jcb.isSelected());
        } else if (e.getSource().equals(orderedDatePicker)) {
            getObject().setDateOrdered((Date) orderedDatePicker.getModel().getValue());
            onValueChanged(orderedDatePicker, "orderDate", null, null);
        } else if (e.getSource().equals(receivedDatePicker)) {
            getObject().setDateReceived((Date) receivedDatePicker.getModel().getValue());
            onValueChanged(receivedDatePicker, "receivedDate", null, null);
        }
    }
}

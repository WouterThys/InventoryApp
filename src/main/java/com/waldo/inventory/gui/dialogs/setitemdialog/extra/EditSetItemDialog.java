package com.waldo.inventory.gui.dialogs.setitemdialog.extra;

import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ISpinner;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledEditPanel;

import javax.swing.*;

public class EditSetItemDialog extends IDialog {

    private ITextField nameTextField;
    private ITextField valueTextField;
    private SpinnerNumberModel spinnerModel;
    private ISpinner amountSpinner;

    private SetItem setItem;

    public EditSetItemDialog(Application application, String title, SetItem setItem) {
        super(application, title);
        showTitlePanel(false);

        initializeComponents();
        initializeLayouts();
        updateComponents(setItem);
    }

    public SetItem getSetItem() {
        return setItem;
    }

    private boolean verify() {
        boolean ok = true;

        String name = nameTextField.getText();
        if (name == null || name.isEmpty()) {
            nameTextField.setError("Name can not be empty");
            ok = false;
        }

        String value = valueTextField.getText();
        if (value == null || value.isEmpty()) {
            nameTextField.setError("Value can not be empty");
            ok = false;
        }

        return ok;
    }

    @Override
    protected void onOK() {
        if (verify()) {
            setItem.setName(nameTextField.getText());
            setItem.setValue(valueTextField.getText());
            setItem.setAmount(spinnerModel.getNumber().intValue());
            super.onOK();
        }
    }

    @Override
    public void initializeComponents() {
        nameTextField = new ITextField("Name");
        valueTextField = new ITextField("Value");

        spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        amountSpinner = new ISpinner(spinnerModel);
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));

        // Add all
        getContentPanel().add(new ITitledEditPanel(
                "",
                new String[] {"Name: ", "Value: ", "Amount: "},
                new JComponent[] {nameTextField, valueTextField, amountSpinner}
        ));
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            setItem = (SetItem) object;

            nameTextField.setText(setItem.getName());
            valueTextField.setText(setItem.getValue());
            spinnerModel.setValue(setItem.getAmount());
        }
    }
}

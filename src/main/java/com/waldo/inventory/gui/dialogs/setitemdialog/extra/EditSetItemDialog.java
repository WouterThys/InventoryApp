package com.waldo.inventory.gui.dialogs.setitemdialog.extra;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.SetItem;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ISpinner;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledEditPanel;
import com.waldo.inventory.gui.dialogs.edititemlocationdialog.EditItemLocation;

import javax.swing.*;
import java.awt.*;

public class EditSetItemDialog extends IDialog {

    private ITextField nameTextField;
    private PanelUtils.IValuePanel valuePnl;
    private SpinnerNumberModel spinnerModel;
    private ISpinner amountSpinner;

    // Location
    private ITextField rowTf;
    private ITextField colTf;
    private JButton setLocationBtn;

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

    private void updateLocationFields(Location location) {
        if (location != null && !location.isUnknown()) {
            rowTf.setText(Statics.Alphabet[setItem.getLocation().getRow()]);
            colTf.setText(String.valueOf(setItem.getLocation().getCol()));
        } else {
            rowTf.clearText();
            colTf.clearText();
        }
    }

    private boolean verify() {
        boolean ok = true;

        String name = nameTextField.getText();
        if (name == null || name.isEmpty()) {
            nameTextField.setError("Name can not be empty");
            ok = false;
        }

        return ok;
    }

    @Override
    protected void onOK() {
        if (verify()) {
            setItem.setName(nameTextField.getText());
            setItem.setValue(valuePnl.getValue());
            setItem.setAmount(spinnerModel.getNumber().intValue());

            super.onOK();
        }
    }

    @Override
    public void initializeComponents() {
        nameTextField = new ITextField("Name");
        valuePnl = new PanelUtils.IValuePanel();

        spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        amountSpinner = new ISpinner(spinnerModel);

        colTf = new ITextField();
        colTf.setEnabled(false);
        rowTf = new ITextField();
        rowTf.setEnabled(false);
        setLocationBtn = new JButton("Set");
        setLocationBtn.addActionListener(e -> {
            EditItemLocation dialog = new EditItemLocation(application, "Location", setItem.getLocation());
            if (dialog.showDialog() == IDialog.OK) {
                Location newLoc = dialog.getItemLocation();
                if (newLoc != null) {
                    setItem.setLocationId(newLoc.getId());
                } else {
                    setItem.setLocationId(-1);
                }
                updateLocationFields(newLoc);
            }
        });
    }

    private JPanel locationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        gbc.gridx = 0; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(rowTf, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(colTf, gbc);

        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(setLocationBtn, gbc);

        return panel;
    }

    @Override
    public void initializeLayouts() {
        getContentPanel().setLayout(new BoxLayout(getContentPanel(), BoxLayout.Y_AXIS));

        // Add all
        getContentPanel().add(new ITitledEditPanel(
                "",
                new String[] {"Name: ", "Value: ", "Amount: ", "Location: "},
                new JComponent[] {nameTextField, valuePnl, amountSpinner, locationPanel()}
        ));
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            setItem = (SetItem) object[0];

            nameTextField.setText(setItem.getName());
            valuePnl.setValue(setItem.getValue());
            spinnerModel.setValue(setItem.getAmount());
            updateLocationFields(setItem.getLocation());
        }
    }
}

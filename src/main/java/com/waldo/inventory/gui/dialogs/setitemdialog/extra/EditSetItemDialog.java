package com.waldo.inventory.gui.dialogs.setitemdialog.extra;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Location;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.classes.SetItem;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ISpinner;
import com.waldo.inventory.gui.components.ITextField;
import com.waldo.inventory.gui.components.ITitledEditPanel;
import com.waldo.inventory.gui.dialogs.locationmapdialog.EditItemLocationDialog;

import javax.swing.*;
import java.awt.*;

public class EditSetItemDialog extends IDialog {

    private ITextField nameTextField;
    private ITextField valueTextField;
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

    private int getRow() {
        String rTxt = rowTf.getText();
        if (rTxt != null && !rTxt.isEmpty()) {
            return Statics.indexOfAlphabet(rTxt);
        }
        return -1;
    }

    private int getCol() {
        String cTxt = colTf.getText();
        if (cTxt != null && !cTxt.isEmpty()) {
            return Integer.valueOf(cTxt);
        }
        return  -1;
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
            setItem.getValue().setValue(Double.valueOf(valueTextField.getText()));
            setItem.setAmount(spinnerModel.getNumber().intValue());

            if (getCol() >=0 && getRow() >= 0) {
                long typeId = setItem.getItem().getLocation().getLocationTypeId();
                Location newLocation = SearchManager.sm().findLocation(typeId, getRow(), getCol());
                if (newLocation != null)  {
                    setItem.setLocationId(newLocation.getId());
                }
            }

            super.onOK();
        }
    }

    @Override
    public void initializeComponents() {
        nameTextField = new ITextField("Name");
        valueTextField = new ITextField("Value");

        spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        amountSpinner = new ISpinner(spinnerModel);

        colTf = new ITextField();
        colTf.setEnabled(false);
        rowTf = new ITextField();
        rowTf.setEnabled(false);
        setLocationBtn = new JButton("Set");
        setLocationBtn.addActionListener(e -> {
            LocationType locationType = setItem.getItem().getLocation().getLocationType();
            if (locationType != null && locationType.canBeSaved() && !locationType.isUnknown()) {
                EditItemLocationDialog dialog;
                dialog = new EditItemLocationDialog(application,
                        "Select",
                        setItem.getLocation());
                if (dialog.showDialog() == IDialog.OK) {
                    Location location = dialog.getItemLocation();
                    if (location != null) {
                        rowTf.setText(Statics.Alphabet[location.getRow()]);
                        colTf.setText(String.valueOf(location.getCol()));
                    } else {
                        setItem.setLocationId(DbObject.UNKNOWN_ID);
                    }
                }
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
                new JComponent[] {nameTextField, valueTextField, amountSpinner, locationPanel()}
        ));
    }

    @Override
    public void updateComponents(Object object) {
        if (object != null) {
            setItem = (SetItem) object;

            nameTextField.setText(setItem.getName());
            valueTextField.setText(String.valueOf(setItem.getValue().getValue()));
            spinnerModel.setValue(setItem.getAmount());

            Location location = setItem.getItem().getLocation();
            if (location != null) {
                if (setItem.getLocation() == null) {
                    setItem.setLocationId(DbObject.UNKNOWN_ID);
                } else {
                    rowTf.setText(Statics.Alphabet[setItem.getLocation().getRow()]);
                    colTf.setText(String.valueOf(setItem.getLocation().getCol()));
                }
                setLocationBtn.setEnabled(true);
            } else {
                setLocationBtn.setEnabled(false);
            }
        }
    }
}

package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Location;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.edititemlocationdialog.EditItemLocationDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

public class EditItemStockPanel extends JPanel implements GuiInterface {

    private static final String[] amountTypes = {"", "Max", "Min", "Exact", "Approximate"};

    private Item newItem;
    private Application application;

    // Listener
    private IEditedListener editedListener;

    private ISpinner amountSpinner;
    private JComboBox<String> amountTypeCb;

    private ITextField locationTypeTf;
    private ITextField rowTf;
    private ITextField colTf;
    private JButton setLocationBtn;

    public EditItemStockPanel(Application application, Item newItem, IEditedListener editedListener) {
        this.application = application;
        this.newItem = newItem;
        this.editedListener = editedListener;
    }

    private void updateLocationFields(Location location) {
        if (location != null && !location.isUnknown()) {
            rowTf.setText(Statics.Alphabet[location.getRow()]);
            colTf.setText(String.valueOf(location.getCol()));
            locationTypeTf.setText(location.getLocationType().getName());
        } else {
            rowTf.clearText();
            colTf.clearText();
            locationTypeTf.clearText();
        }
    }

    private JPanel createAmountPanel() {
        JPanel amountPanel = new JPanel(new GridBagLayout());

        // Border
        TitledBorder amountBorder = PanelUtils.createTitleBorder("Amount");

        // Labels
        ILabel amountLabel = new ILabel("Amount: ");
        amountLabel.setHorizontalAlignment(ILabel.RIGHT);
        amountLabel.setVerticalAlignment(ILabel.CENTER);

        ILabel amountTypeLabel = new ILabel("Type: ");
        amountTypeLabel.setHorizontalAlignment(ILabel.RIGHT);
        amountTypeLabel.setVerticalAlignment(ILabel.CENTER);

        // Grid bags
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // Amount type
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountPanel.add(amountTypeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountPanel.add(amountTypeCb, gbc);

        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountPanel.add(amountLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        amountPanel.add(amountSpinner, gbc);

        amountPanel.setBorder(amountBorder);

        return amountPanel;
    }

    private JPanel createLocationPanel() {
        JPanel locationPanel = new JPanel(new GridBagLayout());

        // Border
        TitledBorder amountBorder = PanelUtils.createTitleBorder("Location");

        // Grid bags
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);

        // - Name
        gbc.gridx = 0; gbc.weightx = 0;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        locationPanel.add(new ILabel("Location: ", ILabel.RIGHT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 0; gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        locationPanel.add(locationTypeTf, gbc);

        // - Row
        gbc.gridx = 1; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        locationPanel.add(new ILabel("Row: ", ILabel.LEFT), gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        locationPanel.add(rowTf, gbc);

        // - Column
        gbc.gridx = 2; gbc.weightx = 0;
        gbc.gridy = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        locationPanel.add(new ILabel("Column: ", ILabel.LEFT), gbc);

        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 2; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        locationPanel.add(colTf, gbc);

        // - Button

        gbc.gridx = 2; gbc.weightx = 1;
        gbc.gridy = 3; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        locationPanel.add(setLocationBtn, gbc);

        // Add
        locationPanel.setBorder(amountBorder);

        return locationPanel;
    }

    @Override
    public void initializeComponents() {
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        amountSpinner = new ISpinner(spinnerModel);
        amountSpinner.addEditedListener(editedListener, "amount");

        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>(amountTypes);
        amountTypeCb = new JComboBox<>(cbModel);
        amountTypeCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (editedListener != null) {
                    try {
                        DbObject guiObject = editedListener.getGuiObject();
                        if (guiObject != null) {
                            String newVal = String.valueOf(e.getItem());
                            Item i = (Item) guiObject;

                            String oldVal = String.valueOf(i.getAmountType());

                            editedListener.onValueChanged(amountTypeCb, "amountType", oldVal, newVal);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        rowTf = new ITextField();
        rowTf.setEnabled(false);
        colTf = new ITextField();
        colTf.setEnabled(false);

        locationTypeTf = new ITextField("Location");
        locationTypeTf.setEnabled(false);

        setLocationBtn = new JButton("Set");
        setLocationBtn.addActionListener(e -> {
                EditItemLocationDialog dialog;
                dialog = new EditItemLocationDialog(application,
                        "Select",
                        newItem.getLocation());
                if (dialog.showDialog() == IDialog.OK) {
                    Location newLocation = dialog.getItemLocation();
                    if (newLocation != null) {
                        newItem.setLocationId(newLocation.getId());
                        newLocation.updateItems();
                    } else {
                        newItem.setLocationId(DbObject.UNKNOWN_ID);
                    }
                    updateLocationFields(newLocation);
                    editedListener.onValueChanged(null, "", 0, 0);
                }

        });
    }

    @Override
    public void initializeLayouts() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(createAmountPanel());
        add(createLocationPanel());
    }

    @Override
    public void updateComponents(Object... object) {
        application.beginWait();
        try {
            if (newItem != null) {
                amountTypeCb.setSelectedIndex(newItem.getAmountType());
                amountSpinner.setValue(newItem.getAmount());

                updateLocationFields(newItem.getLocation());
            }
        } finally {
            application.endWait();
        }
    }
}

package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.Utils.Statics.ItemAmountTypes;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.edititemlocationdialog.EditItemLocation;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

public class EditItemStockPanel<T extends Item> extends JPanel implements GuiInterface {

    private final T selectedItem;
    private final Application application;

    // Listener
    private final IEditedListener editedListener;

    private ISpinner amountSpinner;
    private JComboBox<ItemAmountTypes> amountTypeCb;

    private ITextField locationTypeTf;
    private ITextField rowTf;
    private ITextField colTf;
    private IActions.EditAction editAction;
    private IActions.DeleteAction deleteAction;

    public EditItemStockPanel(Application application, @NotNull T selectedItem,@NotNull IEditedListener editedListener) {
        this.application = application;
        this.selectedItem = selectedItem;
        this.editedListener = editedListener;
    }

    public void setValuesForSet(Set set) {
        selectedItem.setAmountType(set.getAmountType());
        selectedItem.setAmount(set.getAmount());
        selectedItem.setLocationId(set.getLocationId());

        amountTypeCb.setSelectedItem(set.getAmountType());
        amountSpinner.setValue(set.getAmount());
        updateLocationFields(set.getLocation());
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
        TitledBorder amountBorder = GuiUtils.createTitleBorder("Amount");

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
        TitledBorder amountBorder = GuiUtils.createTitleBorder("Location");

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
        locationPanel.add(GuiUtils.createComponentWithActions(locationTypeTf, editAction, deleteAction), gbc);

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

        // Add
        locationPanel.setBorder(amountBorder);

        return locationPanel;
    }

    @Override
    public void initializeComponents() {
        SpinnerModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        amountSpinner = new ISpinner(spinnerModel);
        amountSpinner.addEditedListener(editedListener, "amount");

        amountTypeCb = new JComboBox<>(ItemAmountTypes.values());
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

        editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditItemLocation dialog;
                dialog = new EditItemLocation(application,
                        "Select",
                        selectedItem.getLocation());
                if (dialog.showDialog() == IDialog.OK) {
                    Location newLocation = dialog.getItemLocation();
                    if (newLocation != null) {
                        selectedItem.setLocationId(newLocation.getId());
                        newLocation.updateItems();
                    } else {
                        selectedItem.setLocationId(DbObject.UNKNOWN_ID);
                    }
                    updateLocationFields(newLocation);
                    editedListener.onValueChanged(
                            EditItemStockPanel.this,
                            "locationId",
                            0,
                            0);
                }
            }
        };

        deleteAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(
                        EditItemStockPanel.this,
                        "Are you sure you want to delete the location?",
                        "Delete location",
                        JOptionPane.YES_NO_OPTION);

                if (res == JOptionPane.YES_OPTION) {
                    selectedItem.setLocationId(-1);
                    updateLocationFields(null);
                    editedListener.onValueChanged(
                            EditItemStockPanel.this,
                            "locationId",
                            0,
                            -1);
                }
            }
        };
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
            if (selectedItem != null) {
                amountTypeCb.setSelectedItem(selectedItem.getAmountType());
                amountSpinner.setValue(selectedItem.getAmount());
                updateLocationFields(selectedItem.getLocation());
            }
        } finally {
            application.endWait();
        }
    }
}

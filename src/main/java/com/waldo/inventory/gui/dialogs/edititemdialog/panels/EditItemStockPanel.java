package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.sun.istack.internal.NotNull;
import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Location;
import com.waldo.inventory.classes.dbclasses.Set;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.ICacheDialog;
import com.waldo.inventory.gui.components.ILocationLabelPreview;
import com.waldo.inventory.gui.components.actions.IActions;
import com.waldo.inventory.gui.dialogs.edititemlocationdialog.EditItemLocation;
import com.waldo.utils.icomponents.IEditedListener;
import com.waldo.utils.icomponents.ILabel;
import com.waldo.utils.icomponents.ISpinner;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditItemStockPanel<T extends Item> extends JPanel implements GuiUtils.GuiInterface {

    private final T selectedItem;
    private final Window parent;

    // Listener
    private final IEditedListener editedListener;

    // Amounts
    private ISpinner amountSpinner;
    private ISpinner minimumSpinner;
    private ISpinner maximumSpinner;

    // Location
    private ITextField locationTypeTf;
    private ITextField rowTf;
    private ITextField colTf;
    private IActions.EditAction editAction;
    private IActions.DeleteAction deleteAction;

    // Printing
    private ILocationLabelPreview locationPrintable;


    public EditItemStockPanel(Window parent, @NotNull T selectedItem,@NotNull IEditedListener editedListener) {
        this.parent = parent;
        this.selectedItem = selectedItem;
        this.editedListener = editedListener;
    }

    public void setValuesForSet(Set set) {
        if (set != null) {
            selectedItem.setAmountType(set.getAmountType());
            selectedItem.setAmount(set.getAmount());
            selectedItem.setLocationId(set.getLocationId());

            amountSpinner.setValue(set.getAmount());
            minimumSpinner.setValue(set.getMinimum());
            maximumSpinner.setValue(set.getMaximum());
            updateLocationFields(set.getLocation());
        }
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

    private void onEditLocation() {
        EditItemLocation dialog;
        dialog = new EditItemLocation(parent,
                "Select",
                selectedItem.getLocation());
        if (dialog.showDialog() == ICacheDialog.OK) {
            Location newLocation = dialog.getItemLocation();
            if (newLocation != null) {
                selectedItem.setLocationId(newLocation.getId());
                newLocation.updateItemList();
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

    private void onDeleteLocation() {
        int res = JOptionPane.showConfirmDialog(
                EditItemStockPanel.this,
                "Are you sure you want to delete the location?",
                "Delete location",
                JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.YES_OPTION) {
            Location oldLocation = selectedItem.getLocation();
            if (oldLocation != null) {
                oldLocation.updateItemList();
            }
            selectedItem.setLocationId(-1);
            updateLocationFields(null);
            editedListener.onValueChanged(
                    EditItemStockPanel.this,
                    "locationId",
                    0,
                    -1);
        }
    }

    private JPanel createAmountPanel() {
        JPanel amountPanel = new JPanel();

        com.waldo.utils.GuiUtils.GridBagHelper gbc = new com.waldo.utils.GuiUtils.GridBagHelper(amountPanel);
        gbc.addLine("Minimum: ", minimumSpinner);
        gbc.addLine("Available: ", amountSpinner);
        gbc.addLine("Maximum: ", maximumSpinner);

        amountPanel.setBorder(GuiUtils.createInlineTitleBorder("Amounts"));

        return amountPanel;
    }

    private JPanel createLocationPanel() {
        JPanel locationPanel = new JPanel(new GridBagLayout());

        // Border
        Border amountBorder = GuiUtils.createInlineTitleBorder("Location");

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

    private JPanel createPrintPanel() {
        JPanel printPanel = new JPanel(new BorderLayout());

        printPanel.add(locationPrintable);


        printPanel.setBorder(GuiUtils.createInlineTitleBorder("Printing"));

        return printPanel;
    }

    @Override
    public void initializeComponents() {

        // Amounts
        SpinnerModel amountModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        amountSpinner = new ISpinner(amountModel);
        amountSpinner.addEditedListener(editedListener, "amount");

        SpinnerModel minimumModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
        minimumSpinner = new ISpinner(minimumModel);
        minimumSpinner.addEditedListener(editedListener, "minimum");

        SpinnerModel maximumModel = new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1);
        maximumSpinner = new ISpinner(maximumModel);
        maximumSpinner.addEditedListener(editedListener, "maximum");


        // Location
        rowTf = new ITextField();
        rowTf.setEnabled(false);
        colTf = new ITextField();
        colTf.setEnabled(false);

        locationTypeTf = new ITextField("Location");
        locationTypeTf.setEnabled(false);

        editAction = new IActions.EditAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onEditLocation());
            }
        };

        deleteAction = new IActions.DeleteAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> onDeleteLocation());
            }
        };

        // Print
        locationPrintable = new ILocationLabelPreview(null);

    }

    @Override
    public void initializeLayouts() {
        setLayout(new BorderLayout());

        JPanel stockPnl = new JPanel();
        stockPnl.setLayout(new BoxLayout(stockPnl, BoxLayout.Y_AXIS));

        stockPnl.add(createAmountPanel());
        stockPnl.add(createLocationPanel());
        stockPnl.add(createPrintPanel());

        add(stockPnl, BorderLayout.NORTH);
    }

    @Override
    public void updateComponents(Object... object) {
        Application.beginWait(EditItemStockPanel.this);
        try {
            if (selectedItem != null) {
                amountSpinner.setValue(selectedItem.getAmount());
                minimumSpinner.setValue(selectedItem.getMinimum());
                maximumSpinner.setValue(selectedItem.getMaximum());
                updateLocationFields(selectedItem.getLocation());
            }
        } finally {
            Application.endWait(EditItemStockPanel.this);
        }
    }
}

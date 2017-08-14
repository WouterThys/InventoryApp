package com.waldo.inventory.gui.dialogs.edititemdialog.panels;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Location;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.LogManager;
import com.waldo.inventory.database.SearchManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.GuiInterface;
import com.waldo.inventory.gui.components.*;
import com.waldo.inventory.gui.dialogs.locationmapdialog.LocationMapDialog;
import com.waldo.inventory.gui.dialogs.locationtypedialog.LocationTypeDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

public class EditItemStockPanel extends JPanel implements GuiInterface, IEditedListener {

    private static final LogManager LOG = LogManager.LOG(EditItemStockPanel.class);
    private static final String[] amountTypes = {"", "Max", "Min", "Exact", "Approximate"};

    private Item newItem;
    private Application application;
    private Location originalLocation;

    // Listener
    private IEditedListener mainEditedListener;

    private ISpinner amountSpinner;
    private JComboBox<String> amountTypeCb;

    private DefaultComboBoxModel<LocationType> locationTypeModel;
    private IComboBox<LocationType> locationTypeCb;
    private ITextField rowTf;
    private ITextField colTf;
    private JButton setLocationBtn;

    public EditItemStockPanel(Application application, Item newItem, IEditedListener editedListener) {
        this.application = application;
        this.newItem = newItem;
        this.mainEditedListener = editedListener;
    }

    private boolean checkLocationChange() {
        return !(originalLocation != null && originalLocation.equals(newItem.getLocation()));
    }

    private void checkLocation() {
        if (checkLocationChange()) {
            LocationType type = (LocationType) locationTypeCb.getSelectedItem();
            int row = getRow();
            int col = getCol();
            Location newLocation = SearchManager.sm().findLocation(type.getId(), row, col);
            if (newLocation != null) {
                long locId = newLocation.getId();
                newItem.setLocationId(locId);
                mainEditedListener.onValueChanged(null, "locationId", originalLocation.getId(), locId);
            }
        }
    }

    public void locationSaved(Location location) {
        originalLocation = location.createCopy();
    }

    private int getRow() {
        String rTxt = rowTf.getText();
        if (!rTxt.isEmpty()) {
            return Statics.indexOfAlphabet(rTxt);
        }
        return 0;
    }

    private int getCol() {
        String cTxt = colTf.getText();
        if (!cTxt.isEmpty()) {
            return Integer.valueOf(cTxt);
        }
        return 0;
    }


    private ActionListener createLocationTypeListener() {
        return e -> {
            LocationTypeDialog dialog = new LocationTypeDialog(application, "Locations");
            dialog.showDialog();
            updateLocationTypeCb();
        };
    }

    private void updateLocationTypeCb() {
        locationTypeModel.removeAllElements();
        for (LocationType locationType : DbManager.db().getLocationTypes()) {
            locationTypeModel.addElement(locationType);
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
        locationPanel.add(PanelUtils.createComboBoxWithButton(locationTypeCb, createLocationTypeListener()), gbc);

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
        amountSpinner.addEditedListener(mainEditedListener, "amount");

        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>(amountTypes);
        amountTypeCb = new JComboBox<>(cbModel);
        amountTypeCb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (mainEditedListener != null) {
                    try {
                        DbObject guiObject = mainEditedListener.getGuiObject();
                        if (guiObject != null) {
                            String newVal = String.valueOf(e.getItem());
                            Item i = (Item) guiObject;

                            String oldVal = String.valueOf(i.getAmountType());

                            mainEditedListener.onValueChanged(amountTypeCb, "amountType", oldVal, newVal);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        rowTf = new ITextField();
        rowTf.addEditedListener(this, "rowAsString");
        rowTf.setEnabled(false);
        colTf = new ITextField();
        colTf.addEditedListener(this, "col", int.class);
        colTf.setEnabled(false);

        locationTypeModel = new DefaultComboBoxModel<>();
        updateLocationTypeCb();
        locationTypeCb = new IComboBox<>(locationTypeModel);
        locationTypeCb.addEditedListener(this, "locationTypeId");

        setLocationBtn = new JButton("Set");
        setLocationBtn.addActionListener(e -> {
            LocationType locationType = (LocationType) locationTypeModel.getSelectedItem();
            if (locationType != null &&locationType.canBeSaved() && !locationType.isUnknown()) {
                if (locationType.getRows() > 0 && locationType.getColumns() > 0) {
                    LocationMapDialog dialog;
                    dialog = new LocationMapDialog(application,
                            "Select",
                            locationType,
                            newItem.getLocation().getRow(),
                            newItem.getLocation().getCol());

                    if (dialog.showDialog() == IDialog.OK) {
                        rowTf.setText(Statics.Alphabet[dialog.getRow()]);
                        colTf.setText(String.valueOf(dialog.getCol()));
                        checkLocation();
                    }
                }
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
    public void updateComponents(Object object) {
        application.beginWait();
        try {
            if (newItem != null) {
                amountTypeCb.setSelectedIndex(newItem.getAmountType());
                amountSpinner.setValue(newItem.getAmount());

                if (newItem.getLocation() != null) {
                    if (newItem.getLocation().getRow() >= 0 && newItem.getLocation().getCol() >= 0) {
                        rowTf.setText(Statics.Alphabet[newItem.getLocation().getRow()]);
                        colTf.setText(String.valueOf(newItem.getLocation().getCol()));
                    }
                } else {
                    newItem.setLocationId(DbObject.UNKNOWN_ID);
                    newItem.save();
                }
                originalLocation = newItem.getLocation().createCopy();
                locationTypeCb.setSelectedItem(originalLocation.getLocationType());
            }
        } finally {
            application.endWait();
        }
    }

    //
    // Location edited listeners
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        if (!application.isUpdating()) {
            checkLocation();
        }
    }

    @Override
    public DbObject getGuiObject() {
        return newItem.getLocation();
    }
}

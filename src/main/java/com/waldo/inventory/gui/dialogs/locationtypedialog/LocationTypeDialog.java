package com.waldo.inventory.gui.dialogs.locationtypedialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.LocationType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;
import com.waldo.inventory.gui.dialogs.customlocationdialog.CustomLocationDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class LocationTypeDialog extends LocationTypeDialogLayout {

    private boolean canClose = true;

    public LocationTypeDialog(Application application, String title) {
        super(application, title);

        initializeComponents();
        initializeLayouts();

        db().addOnLocationTypeChangedListener(this);

        updateWithFirstLocationType();
    }


    private void updateWithFirstLocationType() {
        if (db().getLocationTypes().size() > 1) {
            updateComponents(db().getLocationTypes().get(1)); // 0 is Unknown
            setDetails();
        } else {
            updateComponents();
        }
    }

    private void setDetails() {
        if (selectedLocationType != null) {
            detailName.setText(selectedLocationType.getName());
            ILocationMapPanel.setLocations(selectedLocationType.getLocations());
            ILocationMapPanel.setLocationsWithItemHighlighted(com.waldo.inventory.gui.components.ILocationMapPanel.GREEN);
        }
    }

    private void clearDetails() {
        detailName.setText("");
    }

    private void showSaveDialog(boolean closeAfter) {
        if (selectedLocationType != null) {
            String msg = selectedLocationType.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedLocationType.save();
                    originalLocationType = selectedLocationType.createCopy();
                    if (closeAfter) {
                        dispose();
                    }
                }
            }
        } else {
            if (closeAfter) {
                dialogResult = OK;
                dispose();
            }
        }
        canClose = true;
    }

    private boolean verify() {
        boolean ok = true;
        if (detailName.getText().isEmpty()) {
            detailName.setError("Name can't be empty");
            ok = false;
        }

        return ok;
    }

    private boolean checkChange() {
        return (selectedLocationType != null) && !(selectedLocationType.equals(originalLocationType));
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog(true);
        }

        if (canClose) {
            super.onOK();
        }
    }

    @Override
    protected void onCancel() {
        if (selectedLocationType != null && originalLocationType != null) {
            originalLocationType.createCopy(selectedLocationType);
            selectedLocationType.setCanBeSaved(true);
        }

        super.onCancel();
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            selectedLocationType.save();
            originalLocationType = selectedLocationType.createCopy();
            getButtonNeutral().setEnabled(false);
        }
    }

    //
    // Value edited
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
        ILocationMapPanel.setLocations(selectedLocationType.getLocations());
    }

    @Override
    public DbObject getGuiObject() {
        return selectedLocationType;
    }

    //
    // Location type changed
    //
    @Override
    public void onInserted(LocationType location) {
        updateComponents(location);
    }

    @Override
    public void onUpdated(LocationType location) {
        updateComponents(location);
    }

    @Override
    public void onDeleted(LocationType object) {
        updateWithFirstLocationType();
    }

    @Override
    public void onCacheCleared() {}

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        selectedLocationType = null;
        clearDetails();
        updateComponents(selectedLocationType);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        DbObjectDialog<LocationType> dialog = new DbObjectDialog<>(application, "New Location type", new LocationType());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            LocationType type = dialog.getDbObject();
            type.save();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedLocationType != null) {
            int res = JOptionPane.showConfirmDialog(LocationTypeDialog.this, "Are you sure you want to delete \"" + selectedLocationType.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedLocationType.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedLocationType != null) {
            DbObjectDialog<LocationType> dialog = new DbObjectDialog<>(application, "Update " + selectedLocationType.getName(), selectedLocationType);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedLocationType.save();
                originalLocationType = selectedLocationType.createCopy();
            }
        }
    }

    //
    // Search
    //
    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        LocationType found = (LocationType) foundObjects.get(0);
        locationTypeList.setSelectedValue(found, true);
    }

    @Override
    public void onSearchCleared() {
        locationTypeList.setSelectedValue(selectedLocationType, true);
    }

    @Override
    public void nextSearchObject(DbObject next) {
        locationTypeList.setSelectedValue(next, true);
    }

    @Override
    public void previousSearchObject(DbObject previous) {
        locationTypeList.setSelectedValue(previous, true);
    }

    //
    // List selection
    //
    @Override
    public void valueChanged(ListSelectionEvent ev) {
        if (!ev.getValueIsAdjusting() && !application.isUpdating()) {
            SwingUtilities.invokeLater(() -> {
                JList list = (JList) ev.getSource();
                Object selected = list.getSelectedValue();

                if (checkChange()) {
                    showSaveDialog(false);
                }
                getButtonNeutral().setEnabled(false);
                updateComponents(selected);
                if (selectedLocationType != null && !selectedLocationType.isUnknown()) {
                    setDetails();
                } else {
                    clearDetails();
                }
            });
        }
    }

    //
    // Custom  button press
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (selectedLocationType != null) {
            CustomLocationDialog dialog = new CustomLocationDialog(application, "Custom", selectedLocationType);
            if (dialog.showDialog() == IDialog.OK) {
                selectedLocationType.updateLocations();
                setDetails();
            }
        }
    }
}
package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class ManufacturersDialog extends ManufacturersDialogLayout {

    private boolean canClose = true;

    public ManufacturersDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();

        db().addOnManufacturerChangedListener(this);

        updateComponents(null);
    }

    @Override
    protected void onOK() {
        if (checkChange()) {
            canClose = false;
            showSaveDialog(true);
        }

        if (canClose) {
            dialogResult = OK;
            dispose();
        }
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            selectedManufacturer.save();
            originalManufacturer = selectedManufacturer.createCopy();
            getButtonNeutral().setEnabled(false);
        }

    }

    private void setDetails() {
        if (selectedManufacturer != null) {
            detailName.setText(selectedManufacturer.getName());
            detailWebsite.setText(selectedManufacturer.getWebsite());

            if (!selectedManufacturer.getIconPath().isEmpty()) {
                detailLogo.setIcon(selectedManufacturer.getIconPath(), 48,48);
            } else {
                detailLogo.setIcon(resourceManager.readImage("Common.UnknownIcon48"));
            }

            detailItemDefaultListModel.removeAllElements();
            for (Item item : db().getItemsForManufacturer(selectedManufacturer.getId())) {
                detailItemDefaultListModel.addElement(item);
            }
        }
    }

    private void clearDetails() {
        detailName.setText("");
        detailWebsite.setText("");
        detailLogo.setIcon((Icon) null);
        detailItemDefaultListModel.removeAllElements();
    }

    private void showSaveDialog(boolean closeAfter) {
        if (selectedManufacturer != null) {
            String msg = selectedManufacturer.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedManufacturer.setName(detailName.getText());
                    selectedManufacturer.setWebsite(detailWebsite.getText());
                    selectedManufacturer.save();
                    originalManufacturer = selectedManufacturer.createCopy();
                    if (closeAfter) {
                        dialogResult = OK;
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

        // Valid website??
        //...
        return ok;
    }

    private boolean checkChange() {
        return (selectedManufacturer != null) && !(selectedManufacturer.equals(originalManufacturer));
    }

    //
    // Update listener
    //

    @Override
    public void updateComponents(Object object) {
        try {
            application.beginWait();
            // Get all menus
            manufacturerDefaultListModel.removeAllElements();
            for (Manufacturer m : db().getManufacturers()) {
                if (!m.isUnknown()) {
                    manufacturerDefaultListModel.addElement(m);
                }
            }

            selectedManufacturer = (Manufacturer) object;
            updateEnabledComponents();

            if (selectedManufacturer != null) {
                originalManufacturer = selectedManufacturer.createCopy();
                manufacturerList.setSelectedValue(selectedManufacturer, true);
                setDetails();
            } else {
                originalManufacturer = null;
            }
        } finally {
            application.endWait();
        }
    }


    //
    // Search listener
    //

    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        Manufacturer mFound = (Manufacturer) foundObjects.get(0);
        manufacturerList.setSelectedValue(mFound, true);
    }

    @Override
    public void onSearchCleared() {
        manufacturerList.setSelectedValue(selectedManufacturer, true);
    }

    //
    // Manufacturer listener
    //
    @Override
    public void onAdded(Manufacturer manufacturer) {
        updateComponents(manufacturer);
    }

    @Override
    public void onUpdated(Manufacturer newManufacturer, Manufacturer oldManufacturer) {
        updateComponents(newManufacturer);
    }

    @Override
    public void onDeleted(Manufacturer manufacturer) {
        updateComponents(null);
    }


    //
    // List selection listener
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            JList list = (JList) e.getSource();
            Object selected = list.getSelectedValue();

            if (checkChange()) {
                showSaveDialog(false);
            }
            getButtonNeutral().setEnabled(false);
            updateComponents(selected);
            if (selectedManufacturer != null && !selectedManufacturer.isUnknown()) {
                setDetails();
            } else {
                clearDetails();
            }
        }
    }

    //
    // Tool bar
    //

    @Override
    public void onToolBarRefresh() {
        updateComponents(null);
    }

    @Override
    public void onToolBarAdd() {
        DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(application, "New Manufacturer", new Manufacturer());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            Manufacturer m = dialog.getDbObject();
            m.save();
        }
    }

    @Override
    public void onToolBarDelete() {
        if (selectedManufacturer != null) {
            int res = JOptionPane.showConfirmDialog(ManufacturersDialog.this, "Are you sure you want to delete \"" + selectedManufacturer.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedManufacturer.delete();
                selectedManufacturer = null;
                originalManufacturer = null;
            }
        }
    }

    @Override
    public void onToolBarEdit() {
        if (selectedManufacturer != null) {
            DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(application, "Update " + selectedManufacturer.getName(), selectedManufacturer);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedManufacturer.save();
                originalManufacturer = selectedManufacturer.createCopy();
            }
        }
    }

    //
    // Web site changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        return selectedManufacturer;
    }
}
package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class ManufacturersDialog extends ManufacturersDialogLayout {

    public static int showDialog(Application parent) {
        ManufacturersDialog md = new ManufacturersDialog(parent, "Manufacturers");
        md.setLocationRelativeTo(parent);
        md.pack();
        md.setMinimumSize(md.getSize());
        md.setVisible(true);
        return md.dialogResult;
    }

    private boolean canClose = true;

    private ManufacturersDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();

        db().addOnManufacturerChangedListener(this);

        updateComponents(null);
    }

    @Override
    protected void onOK() {
        if (detailWebsite.isEdited()) {
            canClose = false;
            showSaveDialog();
        }

        if (canClose) {
            dialogResult = OK;
            dispose();
        }
    }

    private void setDetails() {
        if (selectedManufacturer != null) {
            detailName.setTextBeforeEdit(selectedManufacturer.getName());
            detailWebsite.setTextBeforeEdit(selectedManufacturer.getWebsite());

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

    private void showSaveDialog() {
        if (selectedManufacturer != null) {
            String msg = selectedManufacturer.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedManufacturer.setName(detailName.getText());
                    selectedManufacturer.setWebsite(detailWebsite.getText());
                    selectedManufacturer.save();
                    dispose();
                }
            }
        } else {
            dispose();
        }
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
                manufacturerList.setSelectedValue(selectedManufacturer, true);
                setDetails();
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
            //selectedManufacturer = (Manufacturer) list.getSelectedValue();
            updateComponents(list.getSelectedValue());
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
            }
        }
    }

    @Override
    public void onToolBarEdit() {
        if (selectedManufacturer != null) {
            DbObjectDialog<Manufacturer> dialog = new DbObjectDialog<>(application, "Update " + selectedManufacturer.getName(), selectedManufacturer);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedManufacturer.save();
            }
        }
    }
}
package com.waldo.inventory.gui.dialogs.manufacturerdialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Manufacturer;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
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
        initActions();

        db().addOnManufacturerChangedListener(this);

        updateComponents(null);
    }

    private void initActions() {
        manufacturerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JList list = (JList) e.getSource();
                selectedManufacturer = (Manufacturer) list.getSelectedValue();
                if (selectedManufacturer != null && selectedManufacturer.getId() != DbObject.UNKNOWN_ID) {
                    setDetails();
                } else {
                    clearDetails();
                }
            }
        });
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

    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        Manufacturer mFound = (Manufacturer) foundObjects.get(0);
        manufacturerList.setSelectedValue(mFound, true);
    }

    @Override
    public void onSearchCleared() {
        manufacturerList.setSelectedValue(selectedManufacturer, true);
    }

    @Override
    public void onAdded(Manufacturer manufacturer) {
        updateComponents(null);
    }

    @Override
    public void onUpdated(Manufacturer newManufacturer, Manufacturer oldManufacturer) {
        updateComponents(null);
    }

    @Override
    public void onDeleted(Manufacturer manufacturer) {
        updateComponents(null);
    }
}

package com.waldo.inventory.gui.dialogs.packagedialog;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Package;
import com.waldo.inventory.classes.PackageType;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.List;

public class PackageTypeTypeDialog extends PackageTypeDialogLayout {

    private boolean canClose = true;

    public PackageTypeTypeDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();

        DbManager.db().addOnPackageTypeChangedListener(this);
        updateComponents(null);
    }

    private void setDetails() {
        if (selectedPackageType != null) {
            detailName.setTextBeforeEdit(selectedPackageType.getName());

//            packageTypeCbModel.removeAllElements();
//            for (PackageType pt : DbManager.db().getPackageTypes()) {
//                packageTypeCbModel.addElement(pt);
//            }

//            if (!selectedPackageType.isUnknown()) {
//                detailTypeCb.setSelectedItem(selectedPackageType.getPackageType());
//            }
        }
    }

    private void clearDetails() {
        detailName.setText("");
//        detailTypeCb.setSelectedIndex(-1);
    }

    private void showSaveDialog() {
        if (selectedPackageType != null) {
            String msg = selectedPackageType.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedPackageType.setName(detailName.getText());
//                    selectedPackageType.setPackageType((PackageType) detailTypeCb.getSelectedItem());
                    selectedPackageType.save();
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

        // Validate other ...

        return ok;
    }


    //
    // Dialog
    //
    @Override
    protected void onOK() {
//        PackageType pt = (PackageType) detailTypeCb.getSelectedItem();
//        if (pt != null && selectedPackageType != null) {
//            if (pt.getId() != selectedPackageType.getId()) {
//                canClose = false;
//                showSaveDialog();
//            }
//        }

        if (canClose) {
            dialogResult = OK;
            dispose();
        }
    }

    //
    // Gui update
    //
    @Override
    public void updateComponents(Object object) {
        try {
            application.beginWait();

            // Get all packages
            packageDefaultListModel.removeAllElements();
            for (PackageType p : DbManager.db().getPackageTypes()) {
                if (!p.isUnknown()) {
                    packageDefaultListModel.addElement(p);
                }
            }

            selectedPackageType = (PackageType) object;
            updateEnabledComponents();

            if (selectedPackageType != null) {
                packageList.setSelectedValue(selectedPackageType, true);
                setDetails();
            }

        } finally {
            application.endWait();
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
        DbObjectDialog<PackageType> dialog = new DbObjectDialog<>(application, "New Package", new PackageType());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            PackageType p = dialog.getDbObject();
            p.save();
        }
    }

    @Override
    public void onToolBarDelete() {
        if (selectedPackageType != null) {
            int res = JOptionPane.showConfirmDialog(PackageTypeTypeDialog.this, "Are you sure you want to delete \"" + selectedPackageType.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedPackageType.delete();
                selectedPackageType = null;
            }
        }
    }

    @Override
    public void onToolBarEdit() {
        if (selectedPackageType != null) {
            DbObjectDialog<PackageType> dialog = new DbObjectDialog<>(application, "Update " + selectedPackageType.getName(), selectedPackageType);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedPackageType.save();
            }
        }
    }

    //
    // Search
    //
    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        PackageType pFound = (PackageType) foundObjects.get(0); // Just get first, later get other with "next" arrow?
        packageList.setSelectedValue(pFound, true);
    }

    @Override
    public void onSearchCleared() {
        packageList.setSelectedValue(selectedPackageType, true);
    }

    //
    // List selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            JList list = (JList) e.getSource();
            updateComponents(list.getSelectedValue());
            if (selectedPackageType != null && !selectedPackageType.isUnknown()) {
                setDetails();
            } else {
                clearDetails();
            }
        }
    }


    //
    // Db Changed
    //
    @Override
    public void onAdded(PackageType object) {
        updateComponents(object);
    }

    @Override
    public void onUpdated(PackageType newObject, PackageType oldObject) {
        updateComponents(newObject);
    }

    @Override
    public void onDeleted(PackageType object) {
        updateComponents(null);
    }
}
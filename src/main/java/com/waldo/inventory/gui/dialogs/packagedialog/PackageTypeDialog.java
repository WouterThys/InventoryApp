package com.waldo.inventory.gui.dialogs.packagedialog;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.DimensionType;
import com.waldo.inventory.classes.PackageType;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;
import com.waldo.inventory.gui.dialogs.packagedialog.extras.EditDimensionDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

public class PackageTypeDialog extends PackageTypeDialogLayout {

    private boolean canClose = true;

    public PackageTypeDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();

        DbManager.db().addOnPackageTypeChangedListener(createPackageTypeListener());
        DbManager.db().addOnDimensionTypeChangedListener(createDimensionListener());
        updateComponents(null);
    }

    private void setDetails() {
        if (selectedPackageType != null) {
            detailName.setText(selectedPackageType.getName());
            detailDescription.setText(selectedPackageType.getDescription());
        }
    }

    private void clearDetails() {
        detailName.clearText();
        detailDescription.clearText();
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


    private void showSaveDialog(boolean closeAfter) {
        if (selectedPackageType != null) {
            String msg = selectedPackageType.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedPackageType.setDescription(detailDescription.getText());
                    selectedPackageType.save();
                    originalPackageType = selectedPackageType.createCopy();
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

    private boolean checkChange() {
        return (selectedPackageType != null) && !(selectedPackageType.equals(originalPackageType));
    }


    private DbObjectChangedListener<PackageType> createPackageTypeListener() {
        return new DbObjectChangedListener<PackageType>() {
            @Override
            public void onInserted(PackageType object) {
                updateComponents(object);
            }

            @Override
            public void onUpdated(PackageType newObject) {
                updateComponents(newObject);
            }

            @Override
            public void onDeleted(PackageType object) {
                updateComponents(null);
            }
        };
    }


    private DbObjectChangedListener<DimensionType> createDimensionListener() {
        return new DbObjectChangedListener<DimensionType>() {
            @Override
            public void onInserted(DimensionType object) {
                dimensionTableAdd(object);
                updateEnabledComponents();
            }

            @Override
            public void onUpdated(DimensionType object) {
                dimensionTableUpdate();
            }

            @Override
            public void onDeleted(DimensionType object) {
                // Delete from table
                dimensionTableDelete(object);
                updateEnabledComponents();
            }
        };
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
            dialogResult = OK;
            dispose();
        }
    }

    @Override
    protected void onNeutral() {
        if (verify()) {
            selectedPackageType.save();
            originalPackageType = selectedPackageType.createCopy();
            getButtonNeutral().setEnabled(false);
        }
    }

    @Override
    protected void onCancel() {
        if (selectedPackageType != null && originalPackageType != null) {
            originalPackageType.createCopy(selectedPackageType);
            selectedPackageType.setCanBeSaved(true);
        }
        super.onCancel();
    }

    //
    // Gui update
    //
    @Override
    public void updateComponents(Object object) {
        application.beginWait();
        try {

            // Get all packages
            packageDefaultListModel.removeAllElements();
            for (PackageType p : DbManager.db().getPackageTypes()) {
                if (!p.isUnknown()) {
                    packageDefaultListModel.addElement(p);
                }
            }

            selectedPackageType = (PackageType) object;
            dimensionTableUpdate();
            updateEnabledComponents();

            if (selectedPackageType != null) {
                originalPackageType = selectedPackageType.createCopy();
                packageList.setSelectedValue(selectedPackageType, true);
                setDetails();
            } else {
                originalPackageType = null;
                clearDetails();
            }

        } finally {
            application.endWait();
        }
    }


    private void listTbAdd() {
        DbObjectDialog<PackageType> dialog = new DbObjectDialog<>(application, "New Package", new PackageType());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            PackageType p = dialog.getDbObject();
            p.save();
        }
    }

    private void listTbDelete() {
        if (selectedPackageType != null) {
            int res = JOptionPane.showConfirmDialog(PackageTypeDialog.this, "Are you sure you want to delete \"" + selectedPackageType.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedPackageType.delete();
                selectedPackageType = null;
                originalPackageType = null;
            }
        }
    }

    private void listTbEdit() {
        if (selectedPackageType != null) {
            DbObjectDialog<PackageType> dialog = new DbObjectDialog<>(application, "Update " + selectedPackageType.getName(), selectedPackageType);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedPackageType.save();
                originalPackageType = selectedPackageType.createCopy();
            }
        }
    }


    private void detailTbAdd() {
        if (selectedPackageType != null && !selectedPackageType.isUnknown()) {
            EditDimensionDialog dimensionDialog = new EditDimensionDialog(application, "Add dimension", new DimensionType());
            if (dimensionDialog.showDialog() == IDialog.OK) {
                DimensionType dt = dimensionDialog.getDimensionType();
                dt.setPackageTypeId(selectedPackageType.getId());
                dt.save();
            }
        }
    }

    private void detailTbDelete() {
        deleteSelectedDimensionTypes(getSelectedDimensionTypes());
    }

    private void detailTbEdit() {
        if (selectedDimensionType != null) {
            EditDimensionDialog dimensionDialog = new EditDimensionDialog(application, "Edit " + selectedPackageType.getName(), selectedDimensionType);
            if (dimensionDialog.showDialog() == IDialog.OK) {
                DimensionType dt = dimensionDialog.getDimensionType();
                dt.setPackageTypeId(selectedPackageType.getId());
                dt.save();
            }
        }
    }

    private void deleteSelectedDimensionTypes(final List<DimensionType> itemsToDelete) {
        int result = JOptionPane.CANCEL_OPTION;
        if (itemsToDelete.size() == 1) {
            result = JOptionPane.showConfirmDialog(
                    PackageTypeDialog.this,
                    "Are you sure you want to delete " + itemsToDelete.get(0) + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
        } else if (itemsToDelete.size() > 1) {
            result = JOptionPane.showConfirmDialog(
                    PackageTypeDialog.this,
                    "Are you sure you want to delete " + itemsToDelete.size() + " items?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
        }
        if (result == JOptionPane.OK_OPTION) {
            // Delete from db
            for (DimensionType item : itemsToDelete) {
                item.delete();
            }
            selectedDimensionType = null;
        }

    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateComponents(null);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (source.equals(listToolBar)) {
            listTbAdd();
        } else {
            detailTbAdd();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (source.equals(listToolBar)) {
            listTbDelete();
        } else {
            detailTbDelete();
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (source.equals(listToolBar)) {
            listTbEdit();
        } else {
            detailTbEdit();
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

    @Override
    public void nextSearchObject(DbObject next) {
        packageList.setSelectedValue(next, true);
    }

    @Override
    public void previousSearchObject(DbObject previous) {
        packageList.setSelectedValue(previous, true);
    }

    //
    // List selected
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            if (e.getSource().equals(packageList)) {
                Object selected = packageList.getSelectedValue();

                if (checkChange()) {
                    showSaveDialog(false);
                }
                getButtonNeutral().setEnabled(false);
                updateComponents(selected);
//                if (selectedPackageType != null && !selectedPackageType.isUnknown()) {
//                    setDetails();
//                } else {
//                    clearDetails();
//                }
            } else {
                selectedDimensionType = dimensionTableGetSelected();
            }
            updateEnabledComponents();
        }
    }

    //
    // Field value changed
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        getButtonNeutral().setEnabled(checkChange());
    }

    @Override
    public DbObject getGuiObject() {
        return selectedPackageType;
    }


}

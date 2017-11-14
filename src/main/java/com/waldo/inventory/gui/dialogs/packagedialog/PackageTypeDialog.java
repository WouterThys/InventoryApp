package com.waldo.inventory.gui.dialogs.packagedialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Package;
import com.waldo.inventory.classes.dbclasses.PackageType;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;
import com.waldo.inventory.gui.dialogs.packagedialog.editpackagetypedialog.EditPackageTypeDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class PackageTypeDialog extends PackageTypeDialogLayout {

    private boolean canClose = true;

    public PackageTypeDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();

        DbManager.db().addOnPackageChangedListener(createPackageListener());
        DbManager.db().addOnPackageTypeChangedListener(createPackageTypeListener());
        createNewMouseAdapter();
        updateWithFirstPackage();
    }

    private void updateWithFirstPackage() {
        if (db().getPackages().size() > 1) {
            updateComponents(db().getPackages().get(1)); // Don't select unknown
        } else {
            updateComponents();
        }
    }

    private void createNewMouseAdapter() {
        dimensionTableAddMouseAdapter(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        detailTbEdit();
                    }
                }
            }
        );
    }

    private void setDetails() {
        if (selectedPackage != null) {
            detailNameTf.setText(selectedPackage.getName());
            detailDescriptionTa.setText(selectedPackage.getDescription());
            typeTableInitialize(selectedPackage);
        } else {
            clearDetails();
        }
    }

    private void clearDetails() {
        detailNameTf.clearText();
        detailDescriptionTa.clearText();
        typeTableInitialize(null);
    }

    private boolean verify() {
        boolean ok = true;
        if (detailNameTf.getText().isEmpty()) {
            detailNameTf.setError("Name can't be empty");
            ok = false;
        }

        // Validate other ...

        return ok;
    }


    private void showSaveDialog(boolean closeAfter) {
        if (selectedPackage != null) {
            String msg = selectedPackage.getName() + " is edited, do you want to save?";
            if (JOptionPane.showConfirmDialog(this, msg, "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                if (verify()) {
                    selectedPackage.save();
                    originalPackage = selectedPackage.createCopy();
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
        return (selectedPackage != null) && !(selectedPackage.equals(originalPackage));
    }


    private DbObjectChangedListener<Package> createPackageListener() {
        return new DbObjectChangedListener<Package>() {
            @Override
            public void onInserted(Package object) {
                updateComponents(object);
            }

            @Override
            public void onUpdated(Package newObject) {
                updateComponents(newObject);
            }

            @Override
            public void onDeleted(Package object) {
                updateWithFirstPackage();
            }

            @Override
            public void onCacheCleared() {}
        };
    }

    private DbObjectChangedListener<PackageType> createPackageTypeListener() {
        return new DbObjectChangedListener<PackageType>() {
            @Override
            public void onInserted(PackageType type) {
                selectedPackageType = type;
                typeTableAdd(selectedPackageType);
            }

            @Override
            public void onUpdated(PackageType type) {
                selectedPackageType = type;
                typeTableUpdate();
            }

            @Override
            public void onDeleted(PackageType type) {
                typeTableDelete(type);
                selectedPackageType = null;
            }

            @Override
            public void onCacheCleared() {

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
            selectedPackage.save();
            originalPackage = selectedPackage.createCopy();
            getButtonNeutral().setEnabled(false);
        }
    }

    @Override
    protected void onCancel() {
        if (selectedPackage != null && originalPackage != null) {
            originalPackage.createCopy(selectedPackage);
            selectedPackage.setCanBeSaved(true);
        }
        super.onCancel();
    }

    //
    // Gui update
    //
    @Override
    public void updateComponents(Object... object) {
        application.beginWait();
        try {

            // Get all packages
            packageModel.removeAllElements();
            for (Package p : DbManager.db().getPackages()) {
                if (!p.isUnknown()) {
                    packageModel.addElement(p);
                }
            }

            if (object.length > 0) {
                selectedPackage = (Package) object[0];
            }

            if (selectedPackage != null) {
                originalPackage = selectedPackage.createCopy();
                packageList.setSelectedValue(selectedPackage, true);
                setDetails();
            } else {
                originalPackage = null;
                clearDetails();
            }
            updateEnabledComponents();

        } finally {
            application.endWait();
        }
    }


    private void listTbAdd() {
        DbObjectDialog<Package> dialog = new DbObjectDialog<>(application, "New Package", new Package());
        if (dialog.showDialog() == DbObjectDialog.OK) {
            Package p = dialog.getDbObject();
            p.save();
        }
    }

    private void listTbDelete() {
        if (selectedPackage != null) {
            int res = JOptionPane.showConfirmDialog(PackageTypeDialog.this, "Are you sure you want to delete \"" + selectedPackage.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedPackage.delete();
                selectedPackage = null;
                originalPackage = null;
            }
        }
    }

    private void listTbEdit() {
        if (selectedPackage != null) {
            DbObjectDialog<Package> dialog = new DbObjectDialog<>(application, "Update " + selectedPackage.getName(), selectedPackage);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedPackage.save();
                originalPackage = selectedPackage.createCopy();
            }
        }
    }


    private void detailTbAdd() {
        if (selectedPackage != null && !selectedPackage.isUnknown()) {
            PackageType type = new PackageType(selectedPackage.getId());
            EditPackageTypeDialog dialog = new EditPackageTypeDialog(application, "Add type", type);
            if (dialog.showDialog() == IDialog.OK) {
                type.save();
            }
        }
    }

    private void detailTbDelete() {
        deleteSelectedPackageTypes(typeTableGetAllSelected());
    }

    private void detailTbEdit() {
        if (selectedPackageType != null) {
            EditPackageTypeDialog dialog = new EditPackageTypeDialog(application, "Edit " + selectedPackageType.getName(), selectedPackageType);
            if (dialog.showDialog() == IDialog.OK) {
                selectedPackageType.save();
            }
        }
    }

    private void deleteSelectedPackageTypes(final List<PackageType> itemsToDelete) {
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
            for (PackageType item : itemsToDelete) {
                item.delete();
            }
        }
    }

    //
    // Tool bar
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateWithFirstPackage();
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
        packageList.setSelectedValue(selectedPackage, true);
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
                if (selectedPackage != null && !selectedPackage.isUnknown()) {
                    setDetails();
                } else {
                    clearDetails();
                }
            }
        } else {
            selectedPackageType = typeTableGetSelected();
        }
        updateEnabledComponents();
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
        return selectedPackage;
    }


}

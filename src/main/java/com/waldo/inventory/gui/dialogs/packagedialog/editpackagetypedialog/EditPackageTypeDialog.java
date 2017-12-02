package com.waldo.inventory.gui.dialogs.packagedialog.editpackagetypedialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.PackageType;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.managers.SearchManager;

import java.awt.*;

public class EditPackageTypeDialog extends EditPackageTypeDialogLayout {


    public EditPackageTypeDialog(Application application, String title, PackageType packageType) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(packageType);

    }

    @Override
    protected void onOK() {
        if (verify()) {
            super.onOK();
        }
    }

    private boolean verify() {
        if (packageType.getName().isEmpty()) {
            nameTf.setError("Name can not be empty..");
            return false;
        }
        if (packageType.getId() < 0) {
            for (PackageType type : SearchManager.sm().findPackageTypesByPackageId(packageType.getPackageId())) {
                if (type.getName().equals(packageType.getName())) {
                    nameTf.setError("Name already exists..");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {

    }

    @Override
    public DbObject getGuiObject() {
        return packageType;
    }
}
package com.waldo.inventory.gui.dialogs.editorderfileformatdialog;

import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.OrderFileFormat;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.inventory.gui.Application;

import java.awt.*;

public class EditOrderFileFormatDialog extends EditOrderFileFormatDialogLayout {


    public EditOrderFileFormatDialog(Application application, String title, OrderFileFormat orderFileFormat) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(orderFileFormat);

    }

    public OrderFileFormat getOrderFileFormat() {
        return orderFileFormat;
    }

    @Override
    protected void onOK() {
        if (verify()) {
            super.onOK();
        }
    }

    private boolean verify() {
        boolean result = true;

        String name = nameTf.getText();
        if (name == null || name.isEmpty()) {
            result = false;
            nameTf.setError("Name can not be empty..");
        } else {
            if (SearchManager.sm().findOrderFileFormatByName(name) != null) {
                result = false;
                nameTf.setError("Name already exists..");
            }
        }

        String separator = separatorTf.getText();
        if (separator == null || separator.equals("")) {
            result = false;
            separatorTf.setError("Separator can not be empty..");
        } else {
            if (separator.length() > 2) {
                result = false;
                separatorTf.setError("Separator should be only one character..");
            }
        }

        return result;
    }

    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {
        //
    }

    @Override
    public DbObject getGuiObject() {
        return orderFileFormat;
    }
}
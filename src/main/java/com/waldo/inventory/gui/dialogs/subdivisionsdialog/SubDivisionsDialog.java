package com.waldo.inventory.gui.dialogs.subdivisionsdialog;


import com.waldo.inventory.classes.dbclasses.Category;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Product;
import com.waldo.inventory.gui.Application;

import java.awt.*;

public class SubDivisionsDialog extends SubDivisionsDialogLayout {

    private String originalName = "";

    // Unknown
    public SubDivisionsDialog(Application application, String title) {
        super(application, title, null, SubDivisionType.Unknown);
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    // Category
    public SubDivisionsDialog(Application application, String title, Category category) {
        super(application, title, category, SubDivisionType.Category);
        this.originalName = category.getName();
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    // Product
    public SubDivisionsDialog(Application application, String title, Product product) {
        super(application, title, product, SubDivisionType.Product);
        this.originalName = product.getName();
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }

    // Type
    public SubDivisionsDialog(Application application, String title, com.waldo.inventory.classes.dbclasses.Type type) {
        super(application, title, type, SubDivisionType.Type);
        this.originalName = type.getName();
        initializeComponents();
        initializeLayouts();
        updateComponents();
    }


    //
    // Dialog
    //
    @Override
    protected void onOK() {
        // Undo rename
        switch (divisionType) {
            case Unknown:
                // TODO
                break;
            case Category: category.save(); break;
            case Product: product.save(); break;
            case Type: type.save(); break;
        }
        super.onOK();
    }

    @Override
    protected void onCancel() {
        // Undo rename
        switch (divisionType) {
            case Unknown:
                // TODO
                break;
            case Category: category.setName(originalName); break;
            case Product: product.setName(originalName); break;
            case Type: type.setName(originalName); break;
        }

        super.onCancel();
    }

    //
    // Edited listener
    //
    @Override
    public void onValueChanged(Component component, String fieldName, Object previousValue, Object newValue) {

    }

    @Override
    public DbObject getGuiObject() {
        if (isShown) {
            switch (divisionType) {
                case Unknown:
                    // TODO
                    break;
                case Category: return category;
                case Product: return product;
                case Type: return type;
            }
        }
        return null;
    }
}
package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.Utils.GuiUtils;
import com.waldo.inventory.Utils.Statics.IconDisplayType;
import com.waldo.inventory.classes.dbclasses.Category;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Product;
import com.waldo.utils.icomponents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

abstract class SubDivisionsDialogLayout extends IDialog implements IEditedListener {

    enum SubDivisionType {
        Unknown,
        Category,
        Product,
        Type
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private IComboBox<Category> categoryCb;
    private IComboBox<Product> productCb;

    private ITextField nameTf;
    // Type
    private ICheckBox canHaveValueCb;
    private IComboBox<IconDisplayType> displayTypeCb;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Category category;
    Product product;
    com.waldo.inventory.classes.dbclasses.Type type;

    final SubDivisionType divisionType;


    SubDivisionsDialogLayout(Window parent, String title, DbObject division, SubDivisionType type) {
        super(parent, title);

        this.divisionType = type;
        switch (type) {
            case Unknown: break;
            case Category: this.category = (Category) division; break;
            case Product: this.product = (Product) division; break;
            case Type: this.type = (com.waldo.inventory.classes.dbclasses.Type) division; break;
        }
    }

    /*
     *                  PRIVATE METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void createCategoryCb() {
        List<Category> categories = new ArrayList<>();
        switch (divisionType) {
            case Unknown:
                // TODO
                break;
            case Product:
                categories.add(product.getCategory());
                categoryCb = new IComboBox<>(categories , null, false);
                categoryCb.setSelectedIndex(0);
                categoryCb.setEnabled(false);
                break;
            case Type:
                categories.add(type.getProduct().getCategory());
                categoryCb = new IComboBox<>(categories, null, false);
                categoryCb.setSelectedIndex(0);
                categoryCb.setEnabled(false);
                break;
            default:
                categoryCb = new IComboBox<>();
                categoryCb.setVisible(false);
                break;
        }
    }

    private void createProductCb() {
        List<Product> products = new ArrayList<>();
        switch (divisionType) {
            case Unknown:
                // TODO
                break;
            case Type:
                products.add(type.getProduct());
                productCb = new IComboBox<>(products, null, false);
                productCb.setSelectedIndex(0);
                productCb.setEnabled(false);
                break;
            default:
                productCb = new IComboBox<>();
                productCb.setVisible(false);
                break;
        }
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void initializeComponents() {
        showTitlePanel(false);

        // Combo boxes
        createCategoryCb();
        createProductCb();

        // Name
        nameTf = new ITextField(this, "name");

        canHaveValueCb = new ICheckBox();
        canHaveValueCb.addEditedListener(this, "canHaveValue");

        displayTypeCb = new IComboBox<>(IconDisplayType.values());
        displayTypeCb.addItemListener(e -> {
            if (SubDivisionsDialogLayout.this.isShown && e.getStateChange() == ItemEvent.SELECTED) {
                if (type != null) {
                    type.setDisplayType((IconDisplayType) displayTypeCb.getSelectedItem());
                }
            }
        });
    }

    @Override
    public void initializeLayouts() {

        GuiUtils.GridBagHelper gbc = new GuiUtils.GridBagHelper(getContentPanel(), 120);

        switch (divisionType) {
            case Unknown:
                // TODO
                break;
            case Category:
                break;
            case Product:
                gbc.addLine("Category: ", categoryCb);
                break;
            case Type:
                gbc.addLine("Category: ", categoryCb);
                gbc.addLine("Product:" , productCb);
                break;
        }
        // Name
        gbc.addLine("Name: ", nameTf);

        switch (divisionType) {
            case Type:
                gbc.addLine("Can have value: ", canHaveValueCb);
                gbc.addLine("Display type: ", displayTypeCb);
                break;
        }

        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        // Pack
        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        switch (divisionType) {
            case Unknown:
                // TODO
                break;
            case Category:
                nameTf.setText(category.getName());
                break;
            case Product:
                nameTf.setText(product.getName());
                break;
            case Type:
                nameTf.setText(type.getName());
                canHaveValueCb.setSelected(type.isCanHaveValue());
                displayTypeCb.setSelectedItem(type.getDisplayType());
                break;
        }
    }
}



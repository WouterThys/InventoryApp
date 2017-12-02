package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.Category;
import com.waldo.inventory.classes.dbclasses.DbObject;
import com.waldo.inventory.classes.dbclasses.Product;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class SubDivisionsDialogLayout extends IDialog implements IEditedListener {

    enum SubDivisionType {
        Unknown,
        Category,
        Product,
        Type
    }

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    IComboBox<Category> categoryCb;
    IComboBox<Product> productCb;

    ITextField nameTf;

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    Category category;
    Product product;
    com.waldo.inventory.classes.dbclasses.Type type;

    SubDivisionType divisionType;


    SubDivisionsDialogLayout(Application application, String title, DbObject division, SubDivisionType type) {
        super(application, title);

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

    void updateEnabledComponents() {

    }

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
    }

    @Override
    public void initializeLayouts() {

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(getContentPanel());

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

        gbc.addLine("Name: ", nameTf);

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
                break;
        }
    }
}



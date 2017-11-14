package com.waldo.inventory.gui.dialogs.projectidesdialog.parserdialog.editparseritemlinkdialog;

import com.waldo.inventory.Utils.ComparatorUtils.DbObjectNameComparator;
import com.waldo.inventory.Utils.PanelUtils;
import com.waldo.inventory.classes.dbclasses.Category;
import com.waldo.inventory.classes.dbclasses.ParserItemLink;
import com.waldo.inventory.classes.dbclasses.Product;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IComboBox;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IEditedListener;
import com.waldo.inventory.gui.components.ITextField;

import javax.swing.*;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.managers.SearchManager.sm;

public abstract class EditParserItemLinkDialogLayout extends IDialog implements IEditedListener {

    /*
    *                  COMPONENTS
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private ITextField pcbItemNameTf;
    IComboBox<Category> categoryCb;
    IComboBox<Product> productCb;
    IComboBox<com.waldo.inventory.classes.dbclasses.Type> typeCb;

     /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    ParserItemLink parserItemLink;

    /*
   *                  CONSTRUCTOR
   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditParserItemLinkDialogLayout(Application application, String title) {
        super(application, title);

    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    void updateEnabledComponents() {
        boolean categoryEnabled = !parserItemLink.getPcbItemName().isEmpty();
        categoryCb.setEnabled(categoryEnabled);
    }

    private void createCategoryCb() {
        categoryCb = new IComboBox<>(db().getCategories(), new DbObjectNameComparator<>(), false);
        categoryCb.insertItemAt(null, 0);
        categoryCb.addActionListener(e -> {
            if (!application.isUpdating()) {
                JComboBox jbc = (JComboBox) e.getSource();
                Category c = (Category) jbc.getSelectedItem();
                if (c == null) {
                    productCb.setEnabled(false);
                    typeCb.setEnabled(false);
                } else {
                    productCb.setEnabled(true);
                    updateProductCb(c);
                }
            }
        });
    }

    private void createProductCb() {
        productCb = new IComboBox<>(db().getProducts(), new DbObjectNameComparator<>(), false);
        productCb.insertItemAt(null, 0);
        productCb.setEnabled(false);
        productCb.addActionListener(e -> {
            if (!application.isUpdating()) {
                JComboBox jbc = (JComboBox) e.getSource();
                Product p = (Product) jbc.getSelectedItem();
                if (p == null) {
                    typeCb.setEnabled(false);
                } else {
                    typeCb.setEnabled(true);
                    updateTypeCb(p);
                }
            }
        });
    }

    private void createTypeCb() {
        typeCb = new IComboBox<>(db().getTypes(), new DbObjectNameComparator<>(), false);
        typeCb.insertItemAt(null, 0);
        typeCb.setEnabled(false);
    }

    private void updateProductCb(Category category) {
        productCb.updateList(sm().findProductListForCategory(category.getId()));
        productCb.insertItemAt(null, 0);
    }

    private void updateTypeCb(Product product) {
        typeCb.updateList(sm().findTypeListForProduct(product.getId()));
        typeCb.insertItemAt(null, 0);
    }

    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {
        // Dialog
        showTitlePanel(false);

        // Stuff
        pcbItemNameTf = new ITextField(this, "pcbItemName");

        createCategoryCb();
        createProductCb();
        createTypeCb();
    }

    @Override
    public void initializeLayouts() {
        JPanel panel = new JPanel();

        PanelUtils.GridBagHelper gbc = new PanelUtils.GridBagHelper(panel);
        gbc.addLine("Component name: ", pcbItemNameTf);
        gbc.addLine("Category: ", categoryCb);
        gbc.addLine("Product: ", productCb);
        gbc.addLine("Type: ", typeCb);

        getContentPanel().add(panel);
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(10,5,10,5));

        pack();
    }

    @Override
    public void updateComponents(Object... object) {
        if (object.length != 0 && object[0] != null) {
            parserItemLink = (ParserItemLink) object[0];

            pcbItemNameTf.setText(parserItemLink.getPcbItemName());

            application.beginWait();
            try {
                Category c = parserItemLink.getCategory();
                if (c != null && !c.isUnknown()) {
                    categoryCb.setSelectedItem(c);
                    updateProductCb(c);
                    productCb.setEnabled(true);
                    Product p = parserItemLink.getProduct();
                    if (p != null && !p.isUnknown()) {
                        productCb.setSelectedItem(p);
                        updateTypeCb(p);
                        typeCb.setEnabled(true);
                        com.waldo.inventory.classes.dbclasses.Type t = parserItemLink.getType();
                        if (t != null && !t.isUnknown()) {
                            typeCb.setSelectedItem(t);
                        } else {
                            typeCb.setSelectedItem(null);
                        }
                    } else {
                        productCb.setSelectedItem(null);
                        typeCb.setSelectedItem(null);
                    }

                } else {
                    categoryCb.setSelectedItem(null);
                    productCb.setSelectedItem(null);
                    typeCb.setSelectedItem(null);
                }

            } finally {
                application.endWait();
            }

            updateEnabledComponents();
        }
    }
}
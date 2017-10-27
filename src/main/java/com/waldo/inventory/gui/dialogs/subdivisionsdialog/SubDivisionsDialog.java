package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.dialogs.DbObjectDialog;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;
import static com.waldo.inventory.managers.SearchManager.sm;

public class SubDivisionsDialog extends SubDivisionsDialogLayout {

    public int showDialog() {
        setLocationRelativeTo(application);
        pack();
        setMinimumSize(getSize());
        setVisible(true);
        return dialogResult;
    }


    public SubDivisionsDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();
        updateComponents();

        initActions();
        setCategoriesChanged();
        setProductsChanged();
        setTypesChanged();

        db().addOnCategoriesChangedListener(categoriesChanged);
        db().addOnProductsChangedListener(productsChanged);
        db().addOnTypesChangedListener(typesChanged);
    }

    private void initActions() {
        subDivisionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !application.isUpdating()) {
                JList list = (JList)e.getSource();
                selectedSubType = list.getSelectedIndex();
                detailsPanel.setTitle((String) list.getSelectedValue());
                updateComponents(list.getSelectedIndex()); // Visibilities of components
                updateDetailList();
                updateSelectionCbItems();
            }
        });

        detailList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !application.isUpdating()) {
                setSelectedObject((DbObject) ((JList)e.getSource()).getSelectedValue());
                updateEnabledComponents();
            }
        });

        selectionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateDetailList();
                updateEnabledComponents();
            }
        });
    }

    private void updateDetailList() {
        switch (selectedSubType) {
            case CATEGORIES:
                updateCategoryList();
                break;
            case PRODUCTS:
                updateProductList();
                break;
            case TYPES:
                updateTypeList();
                break;
        }
    }

    private void updateSelectionCbItems() {
        switch (selectedSubType) {
            case CATEGORIES:
                // No combo box
                break;
            case PRODUCTS:
                selectionCbModel.removeAllElements();
                for (Category c : db().getCategories()) {
                    if (!c.isUnknown()) {
                        selectionCbModel.addElement(c);
                    }
                }
                break;
            case TYPES:
                selectionCbModel.removeAllElements();
                for (Product p : db().getProducts()) {
                    if (!p.isUnknown()) {
                        selectionCbModel.addElement(p);
                    }
                }
                break;
        }
    }

    private void setSelectedObject(DbObject object) {
        selectedObject = object;
        if (selectedObject != null) {
            String iconPath = selectedObject.getIconPath();
            if (iconPath != null && !iconPath.isEmpty()) {
                try {
                    iconLabel.setIcon(selectedObject.getIconPath());
                } catch (Exception e) {
                    iconLabel.setIcon(imageResource.readImage("Common.UnknownIcon32"));
                    Status().setError("Error reading icon, setting default value.", e);
                }
            } else {
                iconLabel.setIcon(imageResource.readImage("Common.UnknownIcon32"));
            }
        } else {
            iconLabel.setIcon((Icon) null);
        }
    }

    private void selectObject(DbObject object) throws ClassCastException {
        switch (DbObject.getType(object)) {
            case DbObject.TYPE_CATEGORY: {
                subDivisionList.setSelectedIndex(0);
                break;
            }
            case DbObject.TYPE_PRODUCT: {
                subDivisionList.setSelectedIndex(1);
                Category c = sm().findCategoryById(((Product)object).getCategoryId());
                selectionComboBox.setSelectedItem(c);
                break;
            }
            case DbObject.TYPE_TYPE: {
                subDivisionList.setSelectedIndex(2);
                Product p = sm().findProductById(((com.waldo.inventory.classes.Type)object).getProductId());
                selectionComboBox.setSelectedItem(p);
                break;
            }
        }
        detailList.setSelectedValue(object, true);
        setSelectedObject(selectedObject);
    }

    private void setCategoriesChanged() {
        categoriesChanged = new DbObjectChangedListener<Category>() {
            @Override
            public void onInserted(Category object) {
                updateCategoryList();
            }

            @Override
            public void onUpdated(Category newCategory) {
                updateCategoryList();
            }

            @Override
            public void onDeleted(Category object) {
                updateCategoryList();
            }

            @Override
            public void onCacheCleared() {}
        };
    }

    /**
     * If the view is on types, the cb on top is showing products.
     * Get the id of which product is selected, than get all types for this product, and add them to the
     * Detail list
     */
    private void updateTypeList() {
        if (selectedSubType == TYPES) {
            long productId;
            DbObject obj = ((DbObject)selectionCbModel.getSelectedItem());
            if (obj != null) {
                productId = obj.getId();
                if (productId < 0) {
                    productId = 1; // Unknown
                }
                detailListModel.removeAllElements();
                for (com.waldo.inventory.classes.Type t : sm().findTypeListForProduct(productId)) {
                    if (!t.isUnknown()) {
                        detailListModel.addElement(t);
                    }
                }
            }
        }
    }

    private void setProductsChanged() {
        productsChanged = new DbObjectChangedListener<Product>() {
            @Override
            public void onInserted(Product object) {
                updateProductList();
            }

            @Override
            public void onUpdated(Product newProduct) {
                updateProductList();
            }

            @Override
            public void onDeleted(Product object) {
                updateProductList();
            }

            @Override
            public void onCacheCleared() {}
        };
    }

    private void updateProductList() {
        if (selectedSubType == PRODUCTS) {
            long categoryId;
            DbObject obj = ((DbObject)selectionCbModel.getSelectedItem());
            if (obj != null) {
                categoryId = obj.getId();
                if (categoryId < 0) {
                    categoryId = 1; // Unknown
                }
                detailListModel.removeAllElements();
                for (Product p : sm().findProductListForCategory(categoryId)) {
                    if (!p.isUnknown()) {
                        detailListModel.addElement(p);
                    }
                }
            }
        }
    }

    private void setTypesChanged() {
        typesChanged = new DbObjectChangedListener<com.waldo.inventory.classes.Type>() {
            @Override
            public void onInserted(com.waldo.inventory.classes.Type object) {
                updateTypeList();
            }

            @Override
            public void onUpdated(com.waldo.inventory.classes.Type newType) {
                updateTypeList();
            }

            @Override
            public void onDeleted(com.waldo.inventory.classes.Type object) {
                updateTypeList();
            }

            @Override
            public void onCacheCleared() {}
        };
    }

    private void updateCategoryList() {
        if (selectedSubType == CATEGORIES) {
            detailListModel.removeAllElements();
            for (Category c : DbManager.db().getCategories()) {
                if (!c.isUnknown()) {
                    detailListModel.addElement(c);
                }
            }
        }
    }

    @Override
    public void updateComponents(Object... object) {
        try {
            application.beginWait();
            // Update
            switch (selectedSubType) {
                case CATEGORIES:
                    selectionLabel.setVisible(false);
                    selectionComboBox.setVisible(false);
                    updateCategoryList();
                    break;

                case PRODUCTS:
                    selectionLabel.setText("Select a category");
                    selectionLabel.setVisible(true);
                    selectionComboBox.setVisible(true);
                    updateProductList();
                    break;

                case TYPES:
                    selectionLabel.setText("Select a product");
                    selectionLabel.setVisible(true);
                    selectionComboBox.setVisible(true);
                    updateTypeList();
                    break;
            }
            updateEnabledComponents();
            SwingUtilities.invokeLater(this::repaint);
        } finally {
            application.endWait();
        }
    }

    //
    // Dialog Listeners
    //
    @Override
    protected void onOK() {
        db().removeOnCategoriesChangedListener(categoriesChanged);
        db().removeOnProductsChangedListener(productsChanged);
        db().removeOnTypesChangedListener(typesChanged);
        super.onOK();
    }

    @Override
    protected void onCancel() {
        db().removeOnCategoriesChangedListener(categoriesChanged);
        db().removeOnProductsChangedListener(productsChanged);
        db().removeOnTypesChangedListener(typesChanged);
        super.onCancel();
    }

    //
    // Search Listeners
    //
    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        selectObject(foundObjects.get(0)); // Just select first
    }

    @Override
    public void onSearchCleared() {
        setSelectedObject(selectedObject);
    }

    @Override
    public void nextSearchObject(DbObject next) {
        selectObject(next);
    }

    @Override
    public void previousSearchObject(DbObject previous) {
        selectObject(previous);
    }

    //
    // Tool bar listeners
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        updateComponents();
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        DbObjectDialog dialog;
        switch (selectedSubType) {
            case CATEGORIES:
                dialog = new DbObjectDialog<>(application, "New category", new Category());
                if (dialog.showDialog() == DbObjectDialog.OK) {
                    dialog.getDbObject().save();
                }
                break;
            case PRODUCTS:
                dialog = new DbObjectDialog<>(application, "New product", new Product());
                if (dialog.showDialog() == DbObjectDialog.OK) {
                    Category c = (Category) selectionCbModel.getSelectedItem();
                    ((Product)dialog.getDbObject()).setCategoryId(c.getId());
                    dialog.getDbObject().save();
                }
                break;
            case TYPES:
                dialog = new DbObjectDialog<>(application, "New product", new com.waldo.inventory.classes.Type());
                if (dialog.showDialog() == DbObjectDialog.OK) {
                    Product p = (Product) selectionCbModel.getSelectedItem();
                    ((com.waldo.inventory.classes.Type)dialog.getDbObject()).setProductId(p.getId());
                    dialog.getDbObject().save();
                }
                break;
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (selectedObject != null) {
            int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete \"" + selectedObject.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                selectedObject.delete();
                selectedObject = null;
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedObject != null) {
            DbObjectDialog dialog = new DbObjectDialog<>(application, "Update " + selectedObject.getName(), selectedObject);
            if (dialog.showDialog() == DbObjectDialog.OK) {
                selectedObject.save();
            }
        }
    }
}
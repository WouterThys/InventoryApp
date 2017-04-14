package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class SubDivisionsDialog extends SubDivisionsDialogLayout {

    public static int showDialog(Application parent) {
        SubDivisionsDialog dialog = new SubDivisionsDialog(parent, "Sub Divisions");

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dbInstance().removeOnCategoriesChangedListener(dialog.categoriesChanged);
                dbInstance().removeOnProductsChangedListener(dialog.productsChanged);
                dbInstance().removeOnTypesChangedListener(dialog.typesChanged);
                super.windowClosing(e);
            }
        });

        if (parent != null) {
            dialog.setLocationRelativeTo(parent);
        } else {
            dialog.setLocationByPlatform(true);
        }
        dialog.pack();
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
        return dialog.dialogResult;
    }


    private SubDivisionsDialog(Application application, String title) {
        super(application, title);
        initializeComponents();
        initializeLayouts();

        initActions();
        setCategoriesChanged();
        setProductsChanged();
        setTypesChanged();

        dbInstance().addOnCategoriesChangedListener(categoriesChanged);
        dbInstance().addOnProductsChangedListener(productsChanged);
        dbInstance().addOnTypesChangedListener(typesChanged);
    }

    private void initActions() {
        subDivisionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JList list = (JList)e.getSource();
                selectedSubType = list.getSelectedIndex();
                detailsPanel.setTitle((String) list.getSelectedValue());
                updateComponents(list.getSelectedIndex()); // Visibilities of components
                updateDetailList();
                updateSelectionCbItems();
            }
        });

        detailList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                setSelectedObject((DbObject) ((JList)e.getSource()).getSelectedValue());
            }
        });

        selectionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateDetailList();
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
                for (Category c : dbInstance().getCategories()) {
                    selectionCbModel.addElement(c);
                }
                break;
            case TYPES:
                selectionCbModel.removeAllElements();
                for (Product p : dbInstance().getProducts()) {
                    selectionCbModel.addElement(p);
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
                    iconLabel.setIcon(resourceManager.readImage("Common.UnknownIcon32"));
                    e.printStackTrace();
                }
            } else {
                iconLabel.setIcon(resourceManager.readImage("Common.UnknownIcon32"));
            }
        } else {
            iconLabel.setIcon((Icon) null);
        }
    }

    private void selectObject(DbObject object) throws ClassCastException {
        switch (DbObject.getType(object)) {
            case DbObject.TYPE_CATEGORY: {
                break;
            }
            case DbObject.TYPE_PRODUCT: {
                subDivisionList.setSelectedIndex(1);
                int ndx = dbInstance().findProductIndex(object.getId());
                if (ndx >= 0) {
                    selectionComboBox.setSelectedIndex(ndx);
                }
                break;
            }
            case DbObject.TYPE_TYPE: {
                subDivisionList.setSelectedIndex(2);
                int ndx = dbInstance().findTypeIndex(object.getId());
                if (ndx >= 0) {
                    selectionComboBox.setSelectedIndex(ndx);
                }
                break;
            }
        }
        detailList.setSelectedValue(object, true);
        setSelectedObject(selectedObject);
    }

    private void setCategoriesChanged() {
        categoriesChanged = new DbObjectChangedListener<Category>() {
            @Override
            public void onAdded(Category object) {
                updateCategoryList();
            }

            @Override
            public void onUpdated(Category object) {
                updateCategoryList();
            }

            @Override
            public void onDeleted(Category object) {
                updateCategoryList();
            }
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
                for (com.waldo.inventory.classes.Type t : DbManager.dbInstance().getTypeListForProduct(productId)) {
                    detailListModel.addElement(t);
                }
            }
        }
    }

    private void setProductsChanged() {
        productsChanged = new DbObjectChangedListener<Product>() {
            @Override
            public void onAdded(Product object) {
                updateProductList();
            }

            @Override
            public void onUpdated(Product object) {
                updateProductList();
            }

            @Override
            public void onDeleted(Product object) {
                updateProductList();
            }
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
                for (Product p : DbManager.dbInstance().getProductListForCategory(categoryId)) {
                    detailListModel.addElement(p);
                }
            }
        }
    }

    private void setTypesChanged() {
        typesChanged = new DbObjectChangedListener<com.waldo.inventory.classes.Type>() {
            @Override
            public void onAdded(com.waldo.inventory.classes.Type object) {
                updateTypeList();
            }

            @Override
            public void onUpdated(com.waldo.inventory.classes.Type object) {
                updateTypeList();
            }

            @Override
            public void onDeleted(com.waldo.inventory.classes.Type object) {
                updateTypeList();
            }
        };
    }

    private void updateCategoryList() {
        if (selectedSubType == CATEGORIES) {
            detailListModel.removeAllElements();
            for (Category c : DbManager.dbInstance().getCategories()) {
                detailListModel.addElement(c);
            }
        }
    }

    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        selectObject(foundObjects.get(0)); // Just select first
    }

    @Override
    public void onSearchCleared() {
        setSelectedObject(selectedObject);
    }
}

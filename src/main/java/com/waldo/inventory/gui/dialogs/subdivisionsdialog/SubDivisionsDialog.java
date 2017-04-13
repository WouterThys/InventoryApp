package com.waldo.inventory.gui.dialogs.subdivisionsdialog;

import com.waldo.inventory.classes.Category;
import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Product;
import com.waldo.inventory.classes.Type;
import com.waldo.inventory.gui.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.database.DbManager.dbInstance;

public class SubDivisionsDialog extends SubDivisionsDialogLayout {

    public static void showDialog(Application parent) {
        JDialog dialog = new JDialog(parent, "Sub Divisions", true);
        final SubDivisionsDialog sdd = new SubDivisionsDialog(parent, dialog);
        dialog.getContentPane().add(sdd);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationByPlatform(true);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dbInstance().removeOnCategoriesChangedListener(sdd.categoriesChanged);
                dbInstance().removeOnProductsChangedListener(sdd.productsChanged);
                dbInstance().removeOnTypesChangedListener(sdd.typesChanged);
                super.windowClosing(e);
            }
        });
        dialog.setLocationByPlatform(true);
        dialog.setLocationRelativeTo(null);
        dialog.pack();
        dialog.setVisible(true);
    }


    private SubDivisionsDialog(Application application, JDialog dialog) {
        super(application, dialog);
        initializeComponents();
        initializeLayouts();

        initActions();

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

    @Override
    public void onDbObjectFound(List<DbObject> foundObjects) {
        selectObject(foundObjects.get(0)); // Just select first
    }

    @Override
    public void onSearchCleared() {
        setSelectedObject(selectedObject);
    }
}

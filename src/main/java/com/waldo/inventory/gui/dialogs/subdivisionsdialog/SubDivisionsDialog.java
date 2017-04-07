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

    private static final Logger LOG = LoggerFactory.getLogger(SubDivisionsDialog.class);

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
        //dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);
    }


    private int selectedSubNdx = 0;

    private SubDivisionsDialog(Application application, JDialog dialog) {
        super(application, dialog);
        initActions();
        initializeComponents();
        initializeLayouts();



        dbInstance().addOnCategoriesChangedListener(categoriesChanged);
        dbInstance().addOnProductsChangedListener(productsChanged);
        dbInstance().addOnTypesChangedListener(typesChanged);
    }

    private void initActions() {
        initSearchAction();

        initSubDivisionChangedAction();
        initDetailChangedListener();

        initSelectionCbIndexChanged();
    }

    private void initSubDivisionChangedAction() {
        subDivisionChangedAction = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    JList list = (JList)e.getSource();
                    selectedSubType = list.getSelectedIndex();
                    detailsPanel.setTitle((String) list.getSelectedValue());
                    updateComponents(list.getSelectedIndex()); // Visibilities of components
                    updateDetailList();
                    updateSelectionCbItems();
                }
            }
        };
    }

    private void initSearchAction() {
        searchAction = new AbstractAction("Search", resourceManager.readImage("Common.Search")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchWord = searchField.getText();
                if (searchWord.isEmpty()) {
                    searchField.setError("Enter a search word");
                } else {
                    java.util.List<DbObject> foundList = search(searchWord);
                    if (foundList.size() > 0) {
                        if (foundList.size() == 1) {
                            DbObject obj = foundList.get(0);
                            try {
                                selectObject(obj);
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        } else {

                        }
                    }
                }
            }
        };
    }
    private void initDetailChangedListener() {
        detailChangedAction = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    setSelectedObject((DbObject) ((JList)e.getSource()).getSelectedValue());
                }
            }
        };
    }
    private void initSelectionCbIndexChanged() {
        selectionCbIndexChanged = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateDetailList();
                }
            }
        };
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
                try {
                    for (Category c : dbInstance().getCategories()) {
                        selectionCbModel.addElement(c);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case TYPES:
                selectionCbModel.removeAllElements();
                try {
                    for (Product p : dbInstance().getProducts()) {
                        selectionCbModel.addElement(p);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
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

    private void selectObject(DbObject object) throws SQLException, ClassCastException {
        if (object instanceof Category) {
            selectedSubNdx = 0;
        } else if (object instanceof Product) {
            selectedSubNdx = 1;
        } else {
            selectedSubNdx = 2;
        }

        subDivisionList.setSelectedIndex(selectedSubNdx);
        switch (selectedSubNdx) {
            case 0: // Category
                break;
            case 1: { // Product
                int ndx = dbInstance().findProductIndex(object.getId());
                if (ndx >= 0) {
                    selectionComboBox.setSelectedIndex(ndx);
                }
                break;
            }
            case 2: { // Type
                int ndx = dbInstance().getTypeListForProduct(((Type) object).getProductId()).indexOf(object);
                selectionComboBox.setSelectedIndex(ndx);
                break;
            }
        }
        detailList.setSelectedValue(object, true);
        setSelectedObject(selectedObject);
    }

    private List<DbObject> search(String searchWord) {
        List<DbObject> foundList = new ArrayList<>();

        try {
            Category c = dbInstance().findCategoryByName(searchWord);
            if (c != null) {
                foundList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Product p = dbInstance().findProductByName(searchWord);
            if (p != null) {
                foundList.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Type t= dbInstance().findTypeByName(searchWord);
            if (t != null) {
                foundList.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foundList;
    }

}

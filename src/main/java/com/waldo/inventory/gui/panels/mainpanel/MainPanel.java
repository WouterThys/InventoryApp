package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.Utils.ResourceManager;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import static com.waldo.inventory.database.DbManager.db;

public class MainPanel extends MainPanelLayout {

    private Application application;

    private DbObjectChangedListener<Item> itemsChanged;
    private DbObjectChangedListener<Category> categoriesChanged;
    private DbObjectChangedListener<Product> productsChanged;
    private DbObjectChangedListener<Type> typesChanged;

    public MainPanel(Application application) {
        URL url = MainPanel.class.getResource("/settings/Settings.properties");
        resourceManager = new ResourceManager(url.getPath());
        this.application = application;

        initializeComponents();
        initializeLayouts();
        initActions();
        initializeListeners();

        db().addOnItemsChangedListener(itemsChanged);
        db().addOnCategoriesChangedListener(categoriesChanged);
        db().addOnProductsChangedListener(productsChanged);
        db().addOnTypesChangedListener(typesChanged);

        updateComponents(null);
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public DbObject getLastSelectedDivision() {
        return lastSelectedDivision;
    }

    public ItemTableModel getTableModel() {
        return tableModel;
    }

    private void initActions() {
        initMouseClicked();
    }

    private void initMouseClicked() {
        itemTable.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = (JTable) e.getSource();
                if (e.getClickCount() == 2) {
                    Item selectedItem = application.getSelectedItem();
                    EditItemDialog dialog = new EditItemDialog(application, "Item", selectedItem);
                    if (dialog.showDialog() == EditItemDialog.OK) {
                        dialog.getItem().save();
                    }
                }
            }
        });
    }

    private void initializeListeners() {
        setItemsChangedListener();
        setCategoriesChangedListener();
        setProductsChangedListener();
        setTypesChangedListener();
    }

    private void setItemsChangedListener() {
        itemsChanged = new DbObjectChangedListener<Item>() {
            @Override
            public void onAdded(Item item) {
                selectedItem = item;
                updateItems();
            }

            @Override
            public void onUpdated(Item item) {
                selectedItem = item;
                updateItems();
            }

            @Override
            public void onDeleted(Item item) {
                selectedItem = null;
                updateItems();
            }
        };
    }

    private void updateItems() {
        if (lastSelectedDivision == null) {
            lastSelectedDivision =  ((DivisionTreeModel.DbObjectNode) treeModel.getRoot()).getDbObject();
        }
        updateComponents(lastSelectedDivision);
    }

    private void setCategoriesChangedListener() {
        categoriesChanged = new DbObjectChangedListener<Category>() {
            @Override
            public void onAdded(Category category) {
                treeModel.updateTreeData();
                subDivisionTree.setModel(treeModel);
            }

            @Override
            public void onUpdated(Category category) {
                treeModel.updateTreeData();
                subDivisionTree.setModel(treeModel);

            }

            @Override
            public void onDeleted(Category category) {
                treeModel.updateTreeData();
                subDivisionTree.setModel(treeModel);
            }
        };
    }

    private void setProductsChangedListener() {
        productsChanged = new DbObjectChangedListener<Product>() {
            @Override
            public void onAdded(Product object) {
                treeModel.updateTreeData();
                subDivisionTree.setModel(treeModel);
            }

            @Override
            public void onUpdated(Product object) {
                treeModel.updateTreeData();
                subDivisionTree.setModel(treeModel);
            }

            @Override
            public void onDeleted(Product object) {
                treeModel.updateTreeData();
                subDivisionTree.setModel(treeModel);
            }
        };
    }

    private void setTypesChangedListener() {
        typesChanged = new DbObjectChangedListener<Type>() {
            @Override
            public void onAdded(Type object) {
                treeModel.updateTreeData();
                subDivisionTree.setModel(treeModel);
            }

            @Override
            public void onUpdated(Type object) {
                treeModel.updateTreeData();
                subDivisionTree.setModel(treeModel);
            }

            @Override
            public void onDeleted(Type object) {
                treeModel.updateTreeData();
                subDivisionTree.setModel(treeModel);
            }
        };
    }

    //
    // Tree selection interface
    //

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DivisionTreeModel.DbObjectNode node = (DivisionTreeModel.DbObjectNode) subDivisionTree.getLastSelectedPathComponent();

        if (node == null) {
            lastSelectedDivision = null;
            return; // Nothing selected
        }

        selectedItem = null;
        lastSelectedDivision = node.getDbObject();
        application.clearSearch();

        updateComponents(lastSelectedDivision);
    }

    //
    // Table selection changed
    //

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = itemTable.getSelectedRow();
            if (row >= 0) {
                selectedItem = getItemAt(itemTable.getSelectedRow());
                updateComponents(null);
            }
        }
    }

//    @Override
//    public void focusGained(FocusEvent e) {
//        //
//    }
//
//    @Override
//    public void focusLost(FocusEvent e) {
//        selectedItem = null;
//        updateComponents(null);
//    }
}

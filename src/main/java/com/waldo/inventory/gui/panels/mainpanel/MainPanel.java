package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.IItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.database.DbManager.db;

public class MainPanel extends MainPanelLayout {

    private DbObjectChangedListener<Item> itemsChanged;
    private DbObjectChangedListener<Category> categoriesChanged;
    private DbObjectChangedListener<Product> productsChanged;
    private DbObjectChangedListener<Type> typesChanged;

    public MainPanel(Application application) {
        super(application);

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

    public IItemTableModel getTableModel() {
        return tableModel;
    }

    public TopToolBar getToolBar() {
        return topToolBar;
    }


    private void initActions() {
        initMouseClicked();
    }

    private void initMouseClicked() {
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
                itemChanged(item);
                //updateComponents(lastSelectedDivision); // Give it non-null value
            }

            @Override
            public void onUpdated(Item newItem, Item oldItem) {
                itemChanged(newItem);
                //selectedItem = newItem;
                //updateComponents(lastSelectedDivision);
            }

            @Override
            public void onDeleted(Item item) {
                selectedItem = null;
                updateComponents(lastSelectedDivision);
            }
        };
    }

    private void setCategoriesChangedListener() {
        categoriesChanged = new DbObjectChangedListener<Category>() {
            @Override
            public void onAdded(Category category) {
                treeModel.addObject(category);
            }

            @Override
            public void onUpdated(Category newCategory, Category oldCategory) {
                treeModel.updateObject(newCategory, oldCategory);
            }

            @Override
            public void onDeleted(Category category) {
                treeModel.removeObject(category);
            }
        };
    }

    private void setProductsChangedListener() {
        productsChanged = new DbObjectChangedListener<Product>() {
            @Override
            public void onAdded(Product product) {
                treeModel.addObject(product);
            }

            @Override
            public void onUpdated(Product newProduct, Product oldProduct) {
                treeModel.updateObject(newProduct, oldProduct);
            }

            @Override
            public void onDeleted(Product product) {
                treeModel.removeObject(product);
            }
        };
    }

    private void setTypesChangedListener() {
        typesChanged = new DbObjectChangedListener<Type>() {
            @Override
            public void onAdded(Type type) {
                treeModel.addObject(type);
            }

            @Override
            public void onUpdated(Type newType, Type oldType) {
                treeModel.updateObject(newType, oldType);
            }

            @Override
            public void onDeleted(Type type) {
                treeModel.removeObject(type);
            }
        };
    }

    //
    // Tree selection interface
    //

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!application.isUpdating()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) subDivisionTree.getLastSelectedPathComponent();

            if (node == null) {
                lastSelectedDivision = null;
                return; // Nothing selected
            }

            selectedItem = null;
            application.clearSearch();

            updateComponents(node.getUserObject());
        }
    }

    //
    // Table selection changed
    //

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            int row = itemTable.getSelectedRow();
            if (row >= 0) {
                selectedItem = getItemAt(row);
                updateComponents(lastSelectedDivision);
            }
        }
    }

    //
    //  Tool bar listener
    //

    @Override
    public void onToolBarRefresh() {
        updateComponents(lastSelectedDivision);
    }

    @Override
    public void onToolBarAdd() {
        EditItemDialog dialog = new EditItemDialog(application, "Add item");
        if (dialog.showDialog() == EditItemDialog.OK) {
            Item newItem = dialog.getItem();
            if (newItem != null) {
                newItem.save();
            }
        }
    }

    @Override
    public void onToolBarDelete() {
        Item selectedItem = application.getSelectedItem();
        if (selectedItem != null) {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(application, "Delete " + selectedItem + "?", "Delete", JOptionPane.YES_NO_OPTION)) {
                selectedItem.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit() {
        Item selected = application.getSelectedItem();
        if (selected != null) {
            EditItemDialog dialog = new EditItemDialog(application, "Edit item", selected);
            if (dialog.showDialog() == EditItemDialog.OK) {
                Item newItem = dialog.getItem();
                if (newItem != null) {
                    newItem.save();
                }
            }
        }
    }

}


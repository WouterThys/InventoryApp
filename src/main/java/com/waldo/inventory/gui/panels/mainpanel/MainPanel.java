package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.managers.SearchManager.sm;

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
        initListeners();

        db().addOnItemsChangedListener(itemsChanged);
        db().addOnCategoriesChangedListener(categoriesChanged);
        db().addOnProductsChangedListener(productsChanged);
        db().addOnTypesChangedListener(typesChanged);

        updateComponents((Object)null);
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
                if (e.getClickCount() == 1) {
                    int row = itemTable.rowAtPoint(e.getPoint());
                    int col = itemTable.columnAtPoint(e.getPoint());
                    if (row >= 0 && col == 4) {
                        Item selectedItem = application.getSelectedItem();
                        if (selectedItem.getLocationId() > DbObject.UNKNOWN_ID
                                && selectedItem.getLocation().getLocationTypeId() > DbObject.UNKNOWN_ID) {
                            showLocationPopup(selectedItem, e.getX(), e.getY());
                        }
                    }
                } else if (e.getClickCount() == 2) {
                    Item selectedItem = application.getSelectedItem();
                    if (selectedItem != null && !selectedItem.isUnknown()) {
                        EditItemDialog dialog = new EditItemDialog(application, "Item", selectedItem);
                        dialog.showDialog();
                    }
                }
            }
        });
    }

    private void showLocationPopup(Item item, int x, int y) {
        JPopupMenu menu = new JPopupMenu ();

        LocationType type = item.getLocation().getLocationType();

        ILocationMapPanel panel = new ILocationMapPanel(application, type.getLocations(),null, false);;
        panel.setHighlighted(item.getLocation(), ILocationMapPanel.GREEN);

        JMenuItem name = new JMenuItem(type.getName());
        name.setEnabled(false);

        menu.add(name);
        menu.addSeparator();
        menu.add(panel);
        menu.show(itemTable, x - panel.getPreferredSize().width, y);
    }

    private void initListeners() {
        setItemsChangedListener();
        setCategoriesChangedListener();
        setProductsChangedListener();
        setTypesChangedListener();
    }

    private void setItemsChangedListener() {
        itemsChanged = new DbObjectChangedListener<Item>() {
            @Override
            public void onInserted(Item item) {
                itemChanged(item);
                //updateComponents(lastSelectedDivision); // Give it non-null value
            }

            @Override
            public void onUpdated(Item newItem) {
                itemChanged(newItem);
                //selectedItem = newItem;
                //updateComponents(lastSelectedDivision);
            }

            @Override
            public void onDeleted(Item item) {
                selectedItem = null;
                updateComponents(lastSelectedDivision);
            }

            @Override
            public void onCacheCleared() {
                recreateNodes();
                updateComponents(lastSelectedDivision);
            }
        };
    }

    private void itemChanged(Item addedItem) {
            application.beginWait();
            try {
                selectedItem = addedItem;
                // Find and select in tree
                selectDivision(addedItem);
                // Update table items
                updateTable(lastSelectedDivision);
                // Select in items
                selectItem(addedItem);
                // Update detail panel
                detailPanel.updateComponents(addedItem);
            } finally {
                application.endWait();
            }
    }

    private void selectDivision(Item selectedItem) {
        if (selectedItem.getTypeId() > DbObject.UNKNOWN_ID) {
            lastSelectedDivision = sm().findTypeById(selectedItem.getTypeId());
        } else {
            if (selectedItem.getProductId() > DbObject.UNKNOWN_ID) {
                lastSelectedDivision = sm().findProductById(selectedItem.getProductId());
            } else {
                if (selectedItem.getCategoryId() > DbObject.UNKNOWN_ID) {
                    lastSelectedDivision = sm().findCategoryById(selectedItem.getCategoryId());
                } else {
                    lastSelectedDivision = null; //??
                }
            }
        }
        SwingUtilities.invokeLater(() -> treeModel.setSelectedObject(lastSelectedDivision));
    }

    private void setCategoriesChangedListener() {
        categoriesChanged = new DbObjectChangedListener<Category>() {
            @Override
            public void onInserted(Category category) {
                treeModel.addObject(category);
            }

            @Override
            public void onUpdated(Category newCategory) {
                treeModel.updateObject(newCategory);
            }

            @Override
            public void onDeleted(Category category) {
                treeModel.removeObject(category);
            }

            @Override
            public void onCacheCleared() {}
        };
    }

    private void setProductsChangedListener() {
        productsChanged = new DbObjectChangedListener<Product>() {
            @Override
            public void onInserted(Product product) {
                treeModel.addObject(product);
            }

            @Override
            public void onUpdated(Product newProduct) {
                treeModel.updateObject(newProduct);
            }

            @Override
            public void onDeleted(Product product) {
                treeModel.removeObject(product);
            }

            @Override
            public void onCacheCleared() {}
        };
    }

    private void setTypesChangedListener() {
        typesChanged = new DbObjectChangedListener<Type>() {
            @Override
            public void onInserted(Type type) {
                treeModel.addObject(type);
            }

            @Override
            public void onUpdated(Type newType) {
                treeModel.updateObject(newType);
            }

            @Override
            public void onDeleted(Type type) {
                treeModel.removeObject(type);
            }

            @Override
            public void onCacheCleared() {}
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
    public void onToolBarRefresh(IdBToolBar source) {
        application.beginWait();
        try {
            for (Item item : getTableModel().getItemList()) {
                item.updateOrderState();
            }
        } finally {
            application.endWait();
        }

        updateTable(lastSelectedDivision);
        updateEnabledComponents();

        detailPanel.updateComponents(selectedItem);
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        EditItemDialog dialog = new EditItemDialog(application, "Add item");
        dialog.showDialog();
//        if (dialog.showDialog() == EditItemDialog.OK) {
//            Item newItem = dialog.getOrderItem();
//            if (newItem != null) {
//                newItem.save();
//            }
//        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        Item selectedItem = application.getSelectedItem();
        if (selectedItem != null) {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(application, "Delete " + selectedItem + "?", "Delete", JOptionPane.YES_NO_OPTION)) {
                selectedItem.delete();
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
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


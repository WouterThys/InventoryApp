package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.popups.DivisionPopup;
import com.waldo.inventory.gui.components.popups.ItemPopup;
import com.waldo.inventory.gui.components.popups.LocationPopup;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class MainPanel extends MainPanelLayout {

    private CacheChangedListener<Item> itemsChanged;
    private CacheChangedListener<Category> categoriesChanged;
    private CacheChangedListener<Product> productsChanged;
    private CacheChangedListener<Type> typesChanged;
    private CacheChangedListener<Set> setsChanged;

    public MainPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initListeners();

        cache().addListener(Item.class, itemsChanged);
        cache().addListener(Category.class, categoriesChanged);
        cache().addListener(Product.class, productsChanged);
        cache().addListener(Type.class, typesChanged);
        cache().addListener(Set.class, setsChanged);

        updateComponents((Object) null);
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public DbObject getLastSelectedDivision() {
        return selectedDivision;
    }

    public IItemTableModel getTableModel() {
        return tableModel;
    }

    @Override
    void onTableRowClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            if (SwingUtilities.isRightMouseButton(e)) {
                tableSelectItem(itemTable.getRowAtPoint(e.getPoint()));

                if (selectedItem != null) {
                    ItemPopup popup = new ItemPopup(selectedItem) {
                        @Override
                        public void onEditItem() {
                            MainPanel.this.onEditItem();
                        }

                        @Override
                        public void onDeleteItem() {
                            MainPanel.this.onDeleteItem();
                        }

                        @Override
                        public void onOpenLocalDataSheet(Item item) {
                            application.openDataSheet(item, false);
                        }

                        @Override
                        public void onOpenOnlineDataSheet(Item item) {
                            application.openDataSheet(item, true);
                        }

                        @Override
                        public void onOrderItem(Item item) {
                            MainPanel.this.onOrderItem(item);
                        }

                        @Override
                        public void onShowHistory(Item item) {
                            MainPanel.this.onShowHistory(item);
                        }
                    };
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            } else {
                int row = itemTable.getRowAtPoint(e.getPoint());
                int col = itemTable.getColumnAtPoint(e.getPoint());
                if (row >= 0 && col == 4) {
                    if (selectedItem.getLocationId() > DbObject.UNKNOWN_ID
                            && selectedItem.getLocation().getLocationTypeId() > DbObject.UNKNOWN_ID) {

                        LocationPopup popup = new LocationPopup(application, selectedItem);
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        } else if (e.getClickCount() == 2) {
            if (selectedItem != null && !selectedItem.isUnknown()) {
                onEditItem();
            }
        }
    }

    private void initListeners() {
        setItemsChangedListener();
        setCategoriesChangedListener();
        setProductsChangedListener();
        setTypesChangedListener();
        setSetChangedListener();
    }

    private void setItemsChangedListener() {
        itemsChanged = new CacheChangedListener<Item>() {
            @Override
            public void onInserted(Item item) {
                selectedItem = item;
                // Select the division in the tree
                treeSelectDivisionForItem(item);
                // Set the table
                tableInitialize(selectedDivision);
                // Add to table
                tableAddItem(item);
                // Select in table
                tableSelectItem(item);
                detailPanel.updateComponents(selectedItem);
                updateEnabledComponents();
            }

            @Override
            public void onUpdated(Item item) {
                selectedItem = item;
                final long itemId = tableUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedItem = sm().findItemById(itemId);
                    tableSelectItem(selectedItem);
                    updateEnabledComponents();
                });

            }

            @Override
            public void onDeleted(Item item) {
                tableRemoveItem(item);
                selectedItem = null;
            }

            @Override
            public void onCacheCleared() {
                treeRecreateNodes();
                updateComponents(selectedDivision);
            }
        };
    }

    private void setCategoriesChangedListener() {
        categoriesChanged = new CacheChangedListener<Category>() {
            @Override
            public void onInserted(Category category) {
                treeModel.addObject(category, true);
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
            public void onCacheCleared() {
            }
        };
    }

    private void setProductsChangedListener() {
        productsChanged = new CacheChangedListener<Product>() {
            @Override
            public void onInserted(Product product) {
                treeModel.addObject(product, true);
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
            public void onCacheCleared() {
            }
        };
    }

    private void setTypesChangedListener() {
        typesChanged = new CacheChangedListener<Type>() {
            @Override
            public void onInserted(Type type) {
                treeModel.addObject(type, false);
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
            public void onCacheCleared() {
            }
        };
    }

    private void setSetChangedListener() {
        setsChanged = new CacheChangedListener<Set>() {
            @Override
            public void onInserted(Set set) {
                selectedSet = set;
                listAddItem(set);
                updateEnabledComponents();
            }

            @Override
            public void onUpdated(Set set) {
                selectedSet = set;
                listUpdateItems();
                updateEnabledComponents();
            }

            @Override
            public void onDeleted(Set set) {
                selectedSet = null;
                listDeleteItem(set);
                updateEnabledComponents();
            }

            @Override
            public void onCacheCleared() {

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
                selectedDivision = null;
                return; // Nothing selected
            }

            selectedSet = null;
            setList.clearSelection();

            selectedItem = null;
            application.clearSearch();

            updateComponents(node.getUserObject());
        }
    }

    @Override
    void onTreeRightClick(MouseEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) subDivisionTree.getLastSelectedPathComponent();
        if (node != null) {
            selectedDivision = (DbObject) node.getUserObject();
            JPopupMenu popupMenu = new DivisionPopup(selectedDivision, node.isRoot()) {
                @Override
                public void onAddDivision() {
                    MainPanel.this.onAddDivision();
                }

                @Override
                public void onEditDivision() {
                    MainPanel.this.onEditDivision();
                }

                @Override
                public void onDeleteDivision() {
                    MainPanel.this.onDeleteDivision();
                }
            };
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    //
    // Divisions
    //
    private void onAddDivision() {
        DbObject newDivision = null;
        SubDivisionsDialog divisionsDialog = null;
        if (selectedDivision != null && selectedDivision.canBeSaved()) {
            switch (DbObject.getType(selectedDivision)) {
                case DbObject.TYPE_CATEGORY:
                    newDivision = new Product(selectedDivision.getId());
                    divisionsDialog = new SubDivisionsDialog(application, "Add product", (Product) newDivision);
                    break;
                case DbObject.TYPE_PRODUCT:
                    newDivision = new Type(selectedDivision.getId());
                    divisionsDialog = new SubDivisionsDialog(application, "Add type", (Type) newDivision);
                    break;
            }
        } else {
            newDivision = new Category();
            divisionsDialog = new SubDivisionsDialog(application, "Add category", (Category) newDivision);
        }
        if (divisionsDialog != null) {
            if (divisionsDialog.showDialog() == IDialog.OK) {
                newDivision.save();
            }
        }
    }

    private void onEditDivision() {
        if (selectedDivision != null && selectedDivision.canBeSaved()) {
            SubDivisionsDialog divisionsDialog = null;
            switch (DbObject.getType(selectedDivision)) {
                case DbObject.TYPE_CATEGORY:
                    divisionsDialog = new SubDivisionsDialog(application, "Edit " + selectedDivision.getName(), (Category) selectedDivision);
                    break;
                case DbObject.TYPE_PRODUCT:
                    divisionsDialog = new SubDivisionsDialog(application, "Edit " + selectedDivision.getName(), (Product) selectedDivision);
                    break;
                case DbObject.TYPE_TYPE:
                    divisionsDialog = new SubDivisionsDialog(application, "Edit " + selectedDivision.getName(), (Type) selectedDivision);
                    break;
            }
            if (divisionsDialog != null) {
                if (divisionsDialog.showDialog() == IDialog.OK) {
                    selectedDivision.save();
                }
            }
        }
    }

    private void onDeleteDivision() {
        if (selectedDivision != null && selectedDivision.canBeSaved()) {
            int res = JOptionPane.showConfirmDialog(application, "Are you sure you want to delete " + selectedDivision);
            if (res == JOptionPane.YES_OPTION) {
                selectedDivision.delete();
            }
        }
    }


    // Items
    private void onEditItem() {
        if (selectedItem != null) {
            EditItemDialog dialog = new EditItemDialog(application, "Edit item", selectedItem, selectedSet);
            dialog.showDialog();
        }
    }

    private void onAddItem() {
        EditItemDialog dialog = new EditItemDialog(application, "Add item", new Item(), selectedSet);
        dialog.showDialog();
    }

    private void onDeleteItem() {
        if (selectedItem != null) {
            if (selectedSet != null) {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                        this,
                        "Delete " + selectedItem + " from " + selectedSet + "?",
                        "Delete",
                        JOptionPane.YES_NO_OPTION)) {

                    selectedSet.removeSetItem(selectedItem);
                }
            } else {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                        this,
                        "Delete " + selectedItem + "?",
                        "Delete",
                        JOptionPane.YES_NO_OPTION)) {
                    selectedItem.delete();
                }
            }
        }
    }

    // Sets
    private void onAddSet() {
        EditItemDialog dialog = new EditItemDialog(application, "Add set", new Set());
        dialog.showDialog();
    }

    private void onEditSet() {
        if (selectedSet != null) {
            EditItemDialog dialog = new EditItemDialog(application, "Edit set", selectedSet);
            dialog.showDialog();
            setList.setSelectedValue(selectedSet, true);
        }
    }

    private void onDeleteSet() {
        if (selectedSet != null && selectedSet.canBeSaved()) {
            int res = JOptionPane.showConfirmDialog(application, "Are you sure you want to delete " + selectedSet);
            if (res == JOptionPane.YES_OPTION) {
                selectedSet.delete();
            }
        }
    }

    //
    // Table or list selection changed
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            application.beginWait();
            try {
                if (e.getSource().equals(setList)) {
                    selectedSet = setList.getSelectedValue();
                    if (selectedSet != null) {
                        selectedDivision = null;
                        treeModel.setSelectedObject(null);
                        tableModel.setItemList(selectedSet.getSetItems());

                        selectedItem = null;
                        detailPanel.updateComponents((Item)null);
                    }
                    updateEnabledComponents();
                } else {
                    selectedItem = itemTable.getSelectedItem();
                    updateComponents(selectedDivision);
                }
            } finally {

                application.endWait();
            }
        }
    }

    //
    //  Tool bar listener
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        if (source.equals(divisionTb)) {
            treeRecreateNodes();
        } else if (source.equals(setTb)) {
            initializeSets();
        } else {
            application.beginWait();
            try {
                for (Item item : getTableModel().getItemList()) {
                    item.updateOrderState();
                }
            } finally {
                application.endWait();
            }

            tableInitialize(selectedDivision);
            updateEnabledComponents();

            detailPanel.updateComponents(selectedItem);
            //previewPanel.updateComponents(selectedItem);
        }
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (source != null && source.equals(divisionTb)) {
            if (selectedDivision == null) {
                selectedDivision = virtualRoot;
            }
            onAddDivision();
        } else if (source != null && source.equals(setTb)) {
            onAddSet();
        } else {
            onAddItem();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (source != null && source.equals(divisionTb)) {
            onDeleteDivision();
        } else if (source != null && source.equals(setTb)) {
            onDeleteSet();
        } else {
            onDeleteItem();
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (source != null && source.equals(divisionTb)) {
            onEditDivision();
        } else if (source != null && source.equals(setTb)) {
            onEditSet();
        } else {
            onEditItem();
        }
    }

    //
    // Detail panel
    //
    @Override
    public void onShowDataSheet(Item item) {
        if (item != null) {
            application.openDataSheet(item);
        }
    }

    @Override
    public void onShowDataSheet(Item item, boolean online) {
        if (item != null) {
            application.openDataSheet(item, online);
        }
    }

    @Override
    public void onOrderItem(Item item) {
        if (item != null) {
            application.orderItem(item);
        }
    }

    @Override
    public void onShowHistory(Item item) {
        if (item != null) {
            application.showHistory(item);
        }
    }


}


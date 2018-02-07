package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.popups.DivisionPopup;
import com.waldo.inventory.gui.components.popups.ItemPopup;
import com.waldo.inventory.gui.components.popups.LocationPopup;
import com.waldo.inventory.gui.components.popups.MultiItemPopup;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.setitemswizaddialog.SetItemsWizardDialog;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;
import java.util.List;

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

    //
    // Table
    //
    @Override
    void onTableRowClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            if (SwingUtilities.isRightMouseButton(e)) {
                List<Item> selectedItems = itemTable.getAllSelectedItems();
                JPopupMenu popup = null;
                if (selectedItems == null || selectedItems.size() < 2) {
                    tableSelectItem(itemTable.getRowAtPoint(e.getPoint()));
                    if (selectedItem != null) {
                        popup = new ItemPopup(selectedItem) {
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

                            @Override
                            public void onAddToSet(Set set, Item item) {
                                MainPanel.this.onAddItemToSet(set, item);
                            }
                        };
                    }
                } else {
                   popup = new MultiItemPopup(selectedItems) {
                       @Override
                       public void onDeleteItems(List<Item> itemList) {
                           MainPanel.this.onDeleteItem();
                       }

                       @Override
                       public void onOrderItems(List<Item> itemList) {
                           application.orderItems(itemList);
                       }

                       @Override
                       public void onAddToSet(Set set, List<Item> itemList) {
                            for (Item item : itemList) {
                                MainPanel.this.onAddItemToSet(set, item);
                            }
                       }
                   };
                }
                if (popup != null) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            } else {
                int row = itemTable.getRowAtPoint(e.getPoint());
                int col = itemTable.getColumnAtPoint(e.getPoint());
                if (row >= 0 && col == 4) {
                    if (selectedItem.getLocationId() > DbObject.UNKNOWN_ID
                            && selectedItem.getLocation().getLocationTypeId() > DbObject.UNKNOWN_ID) {

                        LocationPopup popup = new LocationPopup(application, selectedItem.getLocation());
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

    @Override
    public void onShowSets(boolean show) {
        showSets = show;
        tableInitialize(selectedDivision);
    }

    @Override
    public void onShowSetItems(boolean show) {
        showSetItems = show;
        tableInitialize(selectedDivision);
    }

    private void initListeners() {
        setItemsChangedListener();
        setCategoriesChangedListener();
        setProductsChangedListener();
        setTypesChangedListener();
        setSetsChangedListener();
    }

    private void setItemsChangedListener() {
        itemsChanged = new CacheChangedListener<Item>() {
            @Override
            public void onInserted(Item item) {
                selectedItem = item;
                detailPanel.updateComponents(selectedItem);

                if (setsSelected()) {
                    ((Set) selectedDivision).addSetItem(item);
                } else {
                    treeSelectDivisionForItem(item);
                }

                // Add to table
                tableInitialize(selectedDivision);
                tableAddItem(item);
                // Select in table
                tableSelectItem(item);
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
                updateEnabledComponents();
            }

            @Override
            public void onCacheCleared() {
                //treeRecreateNodes();
                //updateComponents(selectedDivision);
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

    private void setSetsChangedListener() {
        setsChanged = new CacheChangedListener<Set>() {
            @Override
            public void onInserted(Set set) {
                treeModel.addObject(set, false);
            }

            @Override
            public void onUpdated(Set set) {
                treeModel.updateObject(set);
            }

            @Override
            public void onDeleted(Set set) {
                treeModel.removeObject(set);
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
        if (!Application.isUpdating(MainPanel.this)) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionTree.getLastSelectedPathComponent();

            if (node == null) {
                selectedDivision = null;
                return; // Nothing selected
            }

            selectedItem = null;
            application.clearSearch();

            updateComponents(node.getUserObject());
        }
    }

    @Override
    void onTreeRightClick(MouseEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionTree.getLastSelectedPathComponent();
        if (node != null) {
            selectedDivision = (DbObject) node.getUserObject();
            JPopupMenu popupMenu = new DivisionPopup(selectedDivision) {
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

                @Override
                public void onSetWizardAction() {
                    MainPanel.this.onSetWizardAction();
                }
            };
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    //
    // Divisions
    //
    private void onAddDivision() {
        DbObject newDivision;
        IDialog dialog = null;
        if (selectedDivision != null && selectedDivision.canBeSaved()) {
            switch (DbObject.getType(selectedDivision)) {
                case DbObject.TYPE_CATEGORY:
                    newDivision = new Product(selectedDivision.getId());
                    dialog = new SubDivisionsDialog(application, "Add product", (Product) newDivision);
                    break;
                case DbObject.TYPE_PRODUCT:
                    newDivision = new Type(selectedDivision.getId());
                    dialog = new SubDivisionsDialog(application, "Add type", (Type) newDivision);
                    break;
                case DbObject.TYPE_SET:
                    newDivision = new Set();
                    dialog = new EditItemDialog<>(application, "Add set", (Set) newDivision);
                    break;
            }
        } else {
            if (setsSelected()) {
                newDivision = new Set();
                dialog = new EditItemDialog<>(application, "Add set", (Set) newDivision);
            } else {
                newDivision = new Category();
                dialog = new SubDivisionsDialog(application, "Add category", (Category) newDivision);
            }
        }
        if (dialog != null) {
            dialog.showDialog();
        }
    }

    private void onEditDivision() {
        if (selectedDivision != null && selectedDivision.canBeSaved()) {
            IDialog dialog = null;
            String title = "Edit " + selectedDivision.getName();
            switch (DbObject.getType(selectedDivision)) {
                case DbObject.TYPE_CATEGORY:
                    dialog = new SubDivisionsDialog(application, title, (Category) selectedDivision);
                    break;
                case DbObject.TYPE_PRODUCT:
                    dialog = new SubDivisionsDialog(application, title, (Product) selectedDivision);
                    break;
                case DbObject.TYPE_TYPE:
                    dialog = new SubDivisionsDialog(application, title, (Type) selectedDivision);
                    break;
                case DbObject.TYPE_SET:
                    dialog = new EditItemDialog<>(application, title, (Set) selectedDivision);
                    break;
            }
            if (dialog != null) {
                dialog.showDialog();
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

    private void onSetWizardAction() {
        if (setsSelected() && selectedDivision.canBeSaved()) {
            // Add set items wizard dialog
            SetItemsWizardDialog dialog = new SetItemsWizardDialog(application, "Set item magic", (Set) selectedDivision);
            dialog.showDialog();
        }
    }


    // Items
    private void onEditItem() {
        if (selectedItem != null) {
            EditItemDialog dialog = new EditItemDialog<>(application, "Edit item", selectedItem);
            dialog.showDialog();
        }
    }

    private void onAddItem() {
        EditItemDialog dialog = new EditItemDialog<>(application, "Add item", new Item());
        if (setsSelected()) {
            dialog.setValuesForSet((Set) selectedDivision);
        }
        dialog.showDialog();
    }

    private void onDeleteItem() {
        List<Item> selectedItems = itemTable.getAllSelectedItems();
        if (selectedItems != null) {
            if (setsSelected()) {
                deleteItemsFromSet(selectedItems);
            } else {
                deleteItems(selectedItems);
            }
        }
    }

    private void deleteItems(List<Item> selectedItems) {
        String message = "";
        boolean delete = true;

        if (selectedItems.size() == 1) {
            message = "Delete " + selectedItems.get(0) + "?";
        } else if (selectedItems.size() > 1) {
            message = "Delete " + selectedItems.size() + " selected items?";
        } else {
            delete = false;
        }

        delete &= JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                this,
                message,
                "Delete",
                JOptionPane.YES_NO_OPTION);

        if (delete) {
            boolean askAgain = true;
            for (Item item : selectedItems) {
                List<SetItemLink> setItemLinks = SearchManager.sm().findSetItemLinksByItemId(item.getId());
                if (askAgain && setItemLinks.size() > 0) {
                    String msg = item.toString() + " is contained in a set, deleting the item will also delete it from the set. Do you want to go on?";
                    JCheckBox forAllItems = new JCheckBox("Do this for all items", true);
                    Object[] params;
                    if (selectedItems.size() > 1) {
                        params = new Object[] {msg, forAllItems};
                    } else {
                        params = new Object[] {msg};
                    }
                    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                            MainPanel.this,
                            params,
                            "Contained in set",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE)) {

                        askAgain = !forAllItems.isSelected();

                        for (SetItemLink setItemLink : setItemLinks) {
                            setItemLink.getSet().removeSetItem(item);
                        }

                        item.delete();
                    }
                } else {
                    if (!askAgain) {
                        for (SetItemLink setItemLink : setItemLinks) {
                            setItemLink.getSet().removeSetItem(item);
                        }
                    }
                    item.delete();
                }
            }
        }
    }

    private void deleteItemsFromSet(List<Item> selectedItems) {
        int res = JOptionPane.NO_OPTION;
        JCheckBox checkBox = new JCheckBox("Also delete item", false);

        if (selectedItems.size() == 1) {
            String message = "Delete " + selectedItems.get(0) + " from " + selectedDivision + "?";
            Object[] params = {message, checkBox};
            res = JOptionPane.showConfirmDialog(
                    this,
                    params,
                    "Delete",
                    JOptionPane.YES_NO_OPTION);
        } else if (selectedItems.size() > 1) {
            String message = "Delete all " + selectedItems.size() + " selected items?";
            Object[] params = {message, checkBox};
            res = JOptionPane.showConfirmDialog(
                    this,
                    params,
                    "Delete",
                    JOptionPane.YES_NO_OPTION);
        }

        if (res == JOptionPane.YES_OPTION) {
            for (Item item : selectedItems) {
                tableRemoveItem(item);
                ((Set)selectedDivision).removeSetItem(item);
                if (checkBox.isSelected()) {
                    item.delete();
                }
            }
            selectedItem = null;
            updateEnabledComponents();
        }
    }

    //
    // Table or list selection changed
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !Application.isUpdating(MainPanel.this)) {
            Application.beginWait(MainPanel.this);
            try {
                selectedItem = itemTable.getSelectedItem();
                updateComponents(selectedDivision);
            } finally {
                Application.endWait(MainPanel.this);
            }
        }
    }

    //
    //  Tool bar listener
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        if (source.equals(selectionTb)) {
            cache().getCategories().clear();
            cache().getProducts().clear();
            cache().getTypes().clear();
            treeRecreateNodes();
        } else {
            Application.beginWait(MainPanel.this);
            try {
                cache().getItems().clear();
                tableInitialize(selectedDivision);
                for (Item item : getTableModel().getItemList()) {
                    item.updateOrderState();
                }
            } finally {
                Application.endWait(MainPanel.this);
            }

            detailPanel.updateComponents(selectedItem);
            updateEnabledComponents();
        }
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (source != null && source.equals(selectionTb)) {
            if (selectedDivision == null) {
                selectedDivision = treeGetItemRoot();
            }
            onAddDivision();
        } else {
            onAddItem();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (source != null && source.equals(selectionTb)) {
            onDeleteDivision();
        } else {
            onDeleteItem();
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (source != null && source.equals(selectionTb)) {
            onEditDivision();
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

    private void onAddItemToSet(Set set, Item item) {
        if (set != null && item != null) {
            selectedItem = item;
            selectedDivision = set;
            detailPanel.updateComponents(selectedItem);

            // Add to table
            treeSelectDivision(selectedDivision);
            tableInitialize(selectedDivision);
            if (set.addSetItem(item)) {
                tableAddItem(item);
            }
            // Select in table
            tableSelectItem(item);
            updateEnabledComponents();
        }
    }


}


package com.waldo.inventory.gui.panels.mainpanel;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.ILocationMapPanel;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.subdivisionsdialog.SubDivisionsDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class MainPanel extends MainPanelLayout {

    private CacheChangedListener<Item> itemsChanged;
    private CacheChangedListener<Category> categoriesChanged;
    private CacheChangedListener<Product> productsChanged;
    private CacheChangedListener<Type> typesChanged;

    public MainPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initListeners();

        cache().addOnItemsChangedListener(itemsChanged);
        cache().addOnCategoriesChangedListener(categoriesChanged);
        cache().addOnProductsChangedListener(productsChanged);
        cache().addOnTypesChangedListener(typesChanged);

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
                openDatasheetOnlineAa.setEnabled(!selectedItem.getOnlineDataSheet().isEmpty());
                openDatasheetLocalAa.setEnabled(!selectedItem.getLocalDataSheet().isEmpty());
                itemPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            } else {
                int row = itemTable.getRowAtPoint(e.getPoint());
                int col = itemTable.getColumnAtPoint(e.getPoint());
                if (row >= 0 && col == 4) {
                    if (selectedItem.getLocationId() > DbObject.UNKNOWN_ID
                            && selectedItem.getLocation().getLocationTypeId() > DbObject.UNKNOWN_ID) {
                        showLocationPopup(selectedItem, e.getX(), e.getY());
                    }
                }
            }
        } else if (e.getClickCount() == 2) {
            if (selectedItem != null && !selectedItem.isUnknown()) {
                EditItemDialog dialog = new EditItemDialog(application, "Item", selectedItem);
                dialog.showDialog();
            }
        }
    }

    private void showLocationPopup(Item item, int x, int y) {
        JPopupMenu menu = new JPopupMenu();

        LocationType type = item.getLocation().getLocationType();

        ILocationMapPanel panel = new ILocationMapPanel(application, type.getLocations(), null, false);
        ;
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
            JPopupMenu popupMenu = null;
            switch (DbObject.getType(selectedDivision)) {
                case DbObject.TYPE_CATEGORY:
                    popupMenu = createCategoryPopup(node.isRoot());
                    break;
                case DbObject.TYPE_PRODUCT:
                    popupMenu = createProductPopup();
                    break;
                case DbObject.TYPE_TYPE:
                    popupMenu = createTypePopup();
                    break;
            }
            if (popupMenu != null) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private JPopupMenu createCategoryPopup(boolean isRoot) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem nameItem = new JMenuItem("Divisions", imageResource.readImage("Items.Tree.Title"));
        nameItem.setEnabled(false);
        popupMenu.add(nameItem);
        popupMenu.addSeparator();

        if (!isRoot) {
            treeAddDivisionAa.putValue(AbstractAction.NAME, "Add product");
            treeEditDivisionAa.putValue(AbstractAction.NAME, "Edit category");
            treeDeleteDivisionAa.putValue(AbstractAction.NAME, "Delete category");
            popupMenu.add(treeAddDivisionAa);
            popupMenu.add(treeEditDivisionAa);
            popupMenu.add(treeDeleteDivisionAa);
        } else {
            treeAddDivisionAa.putValue(AbstractAction.NAME, "Add category");
            popupMenu.add(treeAddDivisionAa);
        }
        return popupMenu;
    }

    private JPopupMenu createProductPopup() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem nameItem = new JMenuItem("Divisions", imageResource.readImage("Items.Tree.Title"));
        nameItem.setEnabled(false);

        treeAddDivisionAa.putValue(AbstractAction.NAME, "Add type");
        treeEditDivisionAa.putValue(AbstractAction.NAME, "Edit product");
        treeDeleteDivisionAa.putValue(AbstractAction.NAME, "Delete product");

        popupMenu.add(nameItem);
        popupMenu.addSeparator();
        popupMenu.add(treeAddDivisionAa);
        popupMenu.add(treeEditDivisionAa);
        popupMenu.add(treeDeleteDivisionAa);
        return popupMenu;
    }

    private JPopupMenu createTypePopup() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem nameItem = new JMenuItem("Divisions", imageResource.readImage("Items.Tree.Title"));
        nameItem.setEnabled(false);

        treeEditDivisionAa.putValue(AbstractAction.NAME, "Edit type");
        treeDeleteDivisionAa.putValue(AbstractAction.NAME, "Delete type");

        popupMenu.add(nameItem);
        popupMenu.addSeparator();
        popupMenu.add(treeEditDivisionAa);
        popupMenu.add(treeDeleteDivisionAa);
        return popupMenu;
    }

    //
    // Divisions
    //
    @Override
    void onAddDivision() {
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

    @Override
    void onEditDivision() {
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

    @Override
    void onDeleteDivision() {
        if (selectedDivision != null && selectedDivision.canBeSaved()) {
            int res = JOptionPane.showConfirmDialog(application, "Are you sure you want to delete " + selectedDivision);
            if (res == JOptionPane.YES_OPTION) {
                selectedDivision.delete();
            }
        }
    }

    //
    // Table selection changed
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !application.isUpdating()) {
            selectedItem = itemTable.getSelectedItem();
            updateComponents(selectedDivision);
        }
    }

    //
    //  Tool bar listener
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        if (source.equals(divisionTb)) {
            treeRecreateNodes();
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
        if (source.equals(divisionTb)) {
            if (selectedDivision == null) {
                selectedDivision = virtualRoot;
            }
            onAddDivision();
        } else {
            Item newItem = new Item();
            EditItemDialog dialog = new EditItemDialog(application, "Add item", newItem);
            dialog.showDialog();
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (source != null && source.equals(divisionTb)) {
            onDeleteDivision();
        } else {
            Item selectedItem = application.getSelectedItem();
            if (selectedItem != null) {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(application, "Delete " + selectedItem + "?", "Delete", JOptionPane.YES_NO_OPTION)) {
                    selectedItem.delete();
                }
            }
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (source != null && source.equals(divisionTb)) {
            onEditDivision();
        } else {
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


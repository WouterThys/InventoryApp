package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.waldo.inventory.database.DbManager.db;

public class OrderPanel extends OrderPanelLayout {

    private DbObjectChangedListener<Item> itemsChanged;
    private DbObjectChangedListener<Order> ordersChanged;

    public OrderPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initActions();
        initializeListeners();

        db().addOnItemsChangedListener(itemsChanged);
        db().addOnOrdersChangedListener(ordersChanged);

        updateComponents(null);
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public DbObject getLastSelectedOrder() {
        return lastSelectedOrder;
    }

    public IItemTableModel getTableModel() {
        return tableModel;
    }




    private void initActions() {
        initMouseClicked();
    }

    private void initMouseClicked() {
        itemTable.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // TODO: go to orders tab?
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
        setOrdersChangedListener();
    }

    private void setItemsChangedListener() {
        itemsChanged = new DbObjectChangedListener<Item>() {
            @Override
            public void onAdded(Item item) {
                selectedItem = item;
                updateItems();
            }

            @Override
            public void onUpdated(Item newItem, Item oldItem) {
                selectedItem = newItem;
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
        if (lastSelectedOrder == null) {
            lastSelectedOrder = (DbObject) treeModel.getRoot();
        }
        updateComponents(lastSelectedOrder);
    }

    private void setOrdersChangedListener() {
        ordersChanged = new DbObjectChangedListener<Order>() {
            @Override
            public void onAdded(Order order) {
                treeModel.addObject(order);
            }

            @Override
            public void onUpdated(Order newOrder, Order oldOrder) {
                treeModel.updateObject(newOrder, oldOrder);
            }

            @Override
            public void onDeleted(Order order) {
                treeModel.removeObject(order);
            }
        };
    }


    //
    // Tree selection interface
    //

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ordersTree.getLastSelectedPathComponent();

        if (node == null) {
            lastSelectedOrder = null;
            return; // Nothing selected
        }

        selectedItem = null;
        lastSelectedOrder = (DbObject) node.getUserObject();
        application.clearSearch();

        updateComponents(lastSelectedOrder);
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

    //
    //  Tool bar listener
    //

    @Override
    public void onToolBarRefresh() {

    }

    @Override
    public void onToolBarAdd() {

    }

    @Override
    public void onToolBarDelete() {

    }

    @Override
    public void onToolBarEdit() {

    }
}

package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.classes.DbObject;
import com.waldo.inventory.classes.Item;
import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IOrderItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.OrderSearchItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;

public class OrderPanel extends OrderPanelLayout {

    private DbObjectChangedListener<Item> itemsChanged;
    private DbObjectChangedListener<Order> ordersChanged;
    private DbObjectChangedListener<OrderItem> orderItemsChanged;

    public OrderPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initActions();
        initializeListeners();

        db().addOnItemsChangedListener(itemsChanged);
        db().addOnOrdersChangedListener(ordersChanged);
        db().addOnOrderItemsChangedListener(orderItemsChanged);

        updateComponents(null);
    }

    public OrderItem getSelectedItem() {
        return selectedItem;
    }

    public DbObject getLastSelectedOrder() {
        return lastSelectedOrder;
    }

    public IOrderItemTableModel getTableModel() {
        return tableModel;
    }

    public void addItemToOrder(Item item, Order order) {
        // Add to data base
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setOrderId(order.getId());
        orderItem.setName(item.toString() + " - " + order.toString());

        orderItem.save();
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
        setOrderItemsChangedListener();
    }

    private void setItemsChangedListener() {
        itemsChanged = new DbObjectChangedListener<Item>() {
            @Override
            public void onAdded(Item item) {
                //selectedItem = DbManager.db().findOrderItem(lastSelectedOrder.getId());
                //updateItems();
            }

            @Override
            public void onUpdated(Item newItem, Item oldItem) {
                //selectedItem = newItem;
                //updateItems();
            }

            @Override
            public void onDeleted(Item item) {
                //selectedItem = null;
                //updateItems();
            }
        };
    }

    private void updateItems() {
        if (lastSelectedOrder == null) {
            lastSelectedOrder = (Order) ((DefaultMutableTreeNode)treeModel.getRoot()).getUserObject();
        }
        updateComponents(lastSelectedOrder);
    }

    private void setOrdersChangedListener() {
        ordersChanged = new DbObjectChangedListener<Order>() {
            @Override
            public void onAdded(Order order) {
                treeModel.addObject(order);
                updateComponents(order);
            }

            @Override
            public void onUpdated(Order newOrder, Order oldOrder) {
                treeModel.updateObject(newOrder, oldOrder);
                updateComponents(newOrder);
            }

            @Override
            public void onDeleted(Order order) {
                treeModel.removeObject(order);
                updateComponents(null);
            }
        };
    }

    private void setOrderItemsChangedListener() {
        orderItemsChanged = new DbObjectChangedListener<OrderItem>() {
            @Override
            public void onAdded(OrderItem orderItem) {
                Order order = DbManager.db().findOrderById(orderItem.getOrderId());
                order.addItemToList(orderItem);
                setSelectedItem(orderItem);
            }

            @Override
            public void onUpdated(OrderItem newOrderItem, OrderItem oldOrderItem) {
                Order order = DbManager.db().findOrderById(newOrderItem.getOrderId());
                updateComponents(order);
            }

            @Override
            public void onDeleted(OrderItem orderItem) {
                Order order = DbManager.db().findOrderById(orderItem.getOrderId());
                selectedItem = null;
                updateComponents(order);
            }
        };
    }

    private void setSelectedItem(OrderItem selectedItem) {
        try {
            application.beginWait();
            this.selectedItem = selectedItem;
            // Tree
            treeModel.setSelectedObject(lastSelectedOrder);

            // Items
            updateTable(lastSelectedOrder);
            selectItem(selectedItem);
            itemDetailPanel.updateComponents(selectedItem);

        } finally {
            application.endWait();
        }
    }

    private void selectItem(OrderItem selectedItem) {
        if (selectedItem != null) {
            List<OrderItem> itemList = getTableModel().getItemList();
            if (itemList != null) {
                int ndx = itemList.indexOf(selectedItem);
                if (ndx >= 0 && ndx < itemList.size()) {
                    itemTable.setRowSelectionInterval(ndx, ndx);
                }
            }
        }
    }


    //
    // Tree selection interface
    //

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ordersTree.getLastSelectedPathComponent();

        if (node == null || ((Order) node.getUserObject()).isUnknown() || !((Order) node.getUserObject()).canBeSaved() ){
            lastSelectedOrder = null;
            return; // Nothing selected
        }

        selectedItem = null;
        application.clearSearch();

        updateComponents(node.getUserObject());
    }

    //
    // Table selection changed
    //

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = itemTable.getSelectedRow();
            if (row >= 0) {
                selectedItem = getTableModel().getItem(itemTable.getSelectedRow());
                updateComponents(lastSelectedOrder);
            }
        }
    }

    //
    //  Item tool bar listener
    //

    @Override
    public void onToolBarRefresh() {
        try {
            application.beginWait();
            updateComponents(lastSelectedOrder);
        } finally {
            application.endWait();
        }
    }

    @Override
    public void onToolBarAdd() {
        if (lastSelectedOrder != null && !lastSelectedOrder.isUnknown() && lastSelectedOrder.canBeSaved()) {
            OrderSearchItemDialog dialog = new OrderSearchItemDialog(application, "Search item to order");
            if (dialog.showDialog() == IDialog.OK) {
                Item itemToOrder = dialog.getItemToOrder();
                if (itemToOrder != null) {
                    addItemToOrder(itemToOrder, lastSelectedOrder);
                }
            }
        }
    }

    @Override
    public void onToolBarDelete() {
        if (selectedItem != null) {
            int res = JOptionPane.showConfirmDialog(OrderPanel.this, "Are you sure you want to delete \"" + selectedItem.toString() + "\" from order \""+lastSelectedOrder.toString()+"\"?");
            if (res == JOptionPane.OK_OPTION) {
                try {
                    application.beginWait();
                    lastSelectedOrder.removeItemFromList(selectedItem);
                } finally {
                    application.endWait();
                }
            }
        }
    }

    @Override
    public void onToolBarEdit() {

    }

    //
    // OrderButton click listener
    //
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}

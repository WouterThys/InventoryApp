package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IOrderItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.orderconfirmdialog.OrderConfirmDialog;
import com.waldo.inventory.gui.dialogs.orderinfodialog.OrderInfoDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.OrderSearchItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.Calendar;
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
        return selectedOrderItem;
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

        // Order
        tbOrderButton.addActionListener(e -> {
            OrderFile orderFile = new OrderFile(lastSelectedOrder);
            orderFile.createOrderFile();
            if (orderFile.isSuccess()) {
                OrderInfoDialog infoDialog = new OrderInfoDialog(application, "Order Info", orderFile);
                infoDialog.showDialog();
                lastSelectedOrder.setOrderFile(orderFile);
                lastSelectedOrder.save();
            } else {
                String msg = "Order failed with next errors: ";
                for (String s : orderFile.getErrorMessages()) {
                    msg += s + "\n\n";
                }
                JOptionPane.showMessageDialog(OrderPanel.this, msg, "Order errors", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Set ordered
        tbSetOrderedBtn.addActionListener(e -> {
            if (!lastSelectedOrder.isOrdered()) {
                OrderConfirmDialog dialog = new OrderConfirmDialog(application, "Confirm order", lastSelectedOrder);
                if (dialog.showDialog() == IDialog.OK) {
                    setOrdered();
                }
            } else if (lastSelectedOrder.isOrdered() && !lastSelectedOrder.isReceived()) {
                setReceived();
            }
        });
    }

    private void setOrdered() {
        lastSelectedOrder.setDateOrdered(new Date(Calendar.getInstance().getTimeInMillis()));
        try {
            application.beginWait();
            lastSelectedOrder.setItemStates(Statics.ItemOrderState.ORDERED);
            lastSelectedOrder.save();
            recreateNodes();
            orderItemDetailPanel.updateComponents(null);
        } finally {
            application.endWait();
        }
    }

    private void setReceived() {
        lastSelectedOrder.setDateReceived(new Date(Calendar.getInstance().getTimeInMillis()));
        try {
            application.beginWait();
            lastSelectedOrder.setItemStates(Statics.ItemOrderState.NONE);
            lastSelectedOrder.updateItemAmounts();
            lastSelectedOrder.save();
            updateComponents(lastSelectedOrder);
        } finally {
            application.endWait();
        }
    }

    private void initMouseClicked() {
        itemTable.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    EditItemDialog dialog = new EditItemDialog(application, "Item", selectedOrderItem.getItem());
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
                //selectedOrderItem = DbManager.db().findOrderItem(lastSelectedOrder.getId());
                //updateItems();
            }

            @Override
            public void onUpdated(Item newItem, Item oldItem) {
                //selectedOrderItem = newItem;
                //updateItems();
            }

            @Override
            public void onDeleted(Item item) {
                //selectedOrderItem = null;
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
                orderChanged(order);
            }

            @Override
            public void onUpdated(Order newOrder, Order oldOrder) {
                treeModel.updateObject(newOrder, oldOrder);
                application.beginWait();
                selectOrder(newOrder);
                application.endWait();
                if (selectedOrderItem != null) {
                    itemDetailPanel.updateComponents(selectedOrderItem.getItem());
                    if (!newOrder.isOrdered()) {
                        orderItemDetailPanel.updateComponents(selectedOrderItem);
                    }
                }
            }

            @Override
            public void onDeleted(Order order) {
                treeModel.removeObject(order);
                updateComponents(null);
            }
        };
    }

    private void orderChanged(Order changedOrder) {
        try {
            application.beginWait();
            // Find and select in tree
            selectOrder(changedOrder);
            // Select in items
            selectOrderItem(selectedOrderItem);
            // Update detail panel
            if (selectedOrderItem != null) {
                itemDetailPanel.updateComponents(selectedOrderItem.getItem());
                if (!changedOrder.isOrdered()) {
                    orderItemDetailPanel.updateComponents(selectedOrderItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            application.endWait();
        }
    }

    private void setOrderItemsChangedListener() {
        orderItemsChanged = new DbObjectChangedListener<OrderItem>() {
            @Override
            public void onAdded(OrderItem orderItem) {
                Order order = DbManager.db().findOrderById(orderItem.getOrderId());
                order.addItemToList(orderItem);
                lastSelectedOrder = order;
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
                selectedOrderItem = null;
                updateComponents(order);
            }
        };
    }

    private void setSelectedItem(OrderItem selectedItem) {
        try {
            application.beginWait();
            this.selectedOrderItem = selectedItem;
            // Tree
            treeModel.setSelectedObject(lastSelectedOrder);

            // Items
            updateTable(lastSelectedOrder);
            selectItem(selectedItem);
            itemDetailPanel.updateComponents(selectedItem.getItem());
            if (!lastSelectedOrder.isOrdered()) {
                orderItemDetailPanel.updateComponents(selectedItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (!application.isUpdating()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) ordersTree.getLastSelectedPathComponent();

            if (node == null || ((Order) node.getUserObject()).isUnknown() || !((Order) node.getUserObject()).canBeSaved()) {
                lastSelectedOrder = null;
                return; // Nothing selected
            }

            application.clearSearch();
            updateComponents(node.getUserObject());
        }
    }

    //
    // Table selection changed
    //

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = itemTable.getSelectedRow();
            if (row >= 0) {
                selectedOrderItem = getTableModel().getItem(itemTable.getSelectedRow());
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
                    // Update item
                    itemToOrder.setOrderState(Statics.ItemOrderState.PLANNED);
                    itemToOrder.save();

                    addItemToOrder(itemToOrder, lastSelectedOrder);
                }
            }
        }
    }

    @Override
    public void onToolBarDelete() {
        if (selectedOrderItem != null) {
            int res = JOptionPane.showConfirmDialog(OrderPanel.this, "Are you sure you want to delete \"" + selectedOrderItem.toString() + "\" from order \""+lastSelectedOrder.toString()+"\"?");
            if (res == JOptionPane.OK_OPTION) {
                try {
                    application.beginWait();
                    lastSelectedOrder.removeItemFromList(selectedOrderItem);
                } finally {
                    application.endWait();
                }
            }
        }
    }

    @Override
    public void onToolBarEdit() {
    }
}

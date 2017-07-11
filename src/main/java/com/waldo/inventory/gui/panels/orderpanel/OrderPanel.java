package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.TopToolBar;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.orderconfirmdialog.OrderConfirmDialog;
import com.waldo.inventory.gui.dialogs.orderdetailsdialog.OrderDetailsDialog;
import com.waldo.inventory.gui.dialogs.orderinfodialog.OrderInfoDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.OrderSearchItemDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.waldo.inventory.database.DbManager.db;
import static com.waldo.inventory.database.SearchManager.sm;
import static com.waldo.inventory.gui.components.IStatusStrip.Status;

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

    public OrderItem getSelectedOrderItem() {
        return selectedOrderItem;
    }

    public IOrderItemTableModel getTableModel() {
        return tableModel;
    }

    public TopToolBar getToolBar() {
        return topToolBar;
    }

    public void addItemToOrder(Item item, Order order) {
        // Add to data base
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setOrderId(order.getId());
        orderItem.setName(item.toString() + " - " + order.toString());

        // Part number
        PartNumber partNumber = sm().findPartNumber(order.getDistributorId(), item.getId());
        if (partNumber != null) {
            orderItem.setItemRef(partNumber.getItemRef());
        }

        orderItem.save();
    }

    public void addItemsToOrder(List<Item> itemsToOrder, Order order) {
        for (Item item : itemsToOrder) {
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(item.getId());
            orderItem.setOrderId(order.getId());
            orderItem.setName(item.toString() + " - " + order.toString());

            // Part number
            PartNumber partNumber = sm().findPartNumber(order.getDistributorId(), item.getId());
            if (partNumber != null) {
                orderItem.setItemRef(partNumber.getItemRef());
            }

            orderItem.save();
        }
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

        // Details
        tbViewOrderDetailsBtn.addActionListener(e -> {
            if (lastSelectedOrder != null) {
                OrderDetailsDialog dialog = new OrderDetailsDialog(application, "Order details", lastSelectedOrder);
                dialog.showDialog();
            }
        });
    }

    private void setOrdered() {
        lastSelectedOrder.setDateOrdered(new Date(Calendar.getInstance().getTimeInMillis()));
        application.beginWait();
        try {
            lastSelectedOrder.setItemStates(Statics.ItemOrderStates.ORDERED);
            recreateNodes();
            orderItemDetailPanel.updateComponents(null);
        } finally {
            application.endWait();
        }
        lastSelectedOrder.save();
    }

    private void setReceived() {
        lastSelectedOrder.setDateReceived(new Date(Calendar.getInstance().getTimeInMillis()));
        application.beginWait();
        try {
            lastSelectedOrder.setItemStates(Statics.ItemOrderStates.NONE);
            lastSelectedOrder.updateItemAmounts();
            updateComponents(lastSelectedOrder);
        } finally {
            application.endWait();
        }
        lastSelectedOrder.save();
    }

    private List<OrderItem> getSelectedOrderItems() {
        List<OrderItem> selectedOrderItems = new ArrayList<>();
        int[] selectedRows = orderItemTable.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int row : selectedRows) {
                OrderItem oi = (OrderItem) orderItemTable.getValueAtRow(row);
                if (oi != null) {
                    selectedOrderItems.add(oi);
                }
            }
        }
        return selectedOrderItems;
    }

    private void deleteSelectedOrderItems(final List<OrderItem> itemsToDelete) {
        if (lastSelectedOrder == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            int result = JOptionPane.CANCEL_OPTION;
            if (itemsToDelete.size() == 1) {
                result = JOptionPane.showConfirmDialog(
                        OrderPanel.this,
                        "Are you sure you want to delete " + itemsToDelete.get(0).getName() + "?",
                        "Confirm delete",
                        JOptionPane.YES_NO_OPTION);
            } else if (itemsToDelete.size() > 1) {
                result = JOptionPane.showConfirmDialog(
                        OrderPanel.this,
                        "Are you sure you want to delete " + itemsToDelete.size() + " items?",
                        "Confirm delete",
                        JOptionPane.YES_NO_OPTION);
            }

            if (result == JOptionPane.OK_OPTION) {
                for (OrderItem item : itemsToDelete) {
                    lastSelectedOrder.removeItemFromList(item);
                }
            }
        });
    }

    private void initMouseClicked() {
        orderItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    EditItemDialog dialog = new EditItemDialog(application, "Item", selectedOrderItem.getItem());
//                    if (dialog.showDialog() == EditItemDialog.OK) {
//                        dialog.getOrderItem().save();
//                    }
                    dialog.showDialog();
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
                updateComponents(lastSelectedOrder);
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
                    setSelectedItem(selectedOrderItem);
                    updateEnabledComponents();
                }
                updateComponents(lastSelectedOrder);
            }

            @Override
            public void onDeleted(Order order) {
                treeModel.removeObject(order);
                recreateNodes();
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
            Status().setError("Error in order changed", e);
        } finally {
            application.endWait();
        }
    }

    private void setOrderItemsChangedListener() {
        orderItemsChanged = new DbObjectChangedListener<OrderItem>() {
            @Override
            public void onAdded(OrderItem orderItem) {
                Order order = sm().findOrderById(orderItem.getOrderId());
                order.addItemToList(orderItem);
                lastSelectedOrder = order;
                setSelectedItem(orderItem);
            }

            @Override
            public void onUpdated(OrderItem newOrderItem, OrderItem oldOrderItem) {
                Order order = sm().findOrderById(newOrderItem.getOrderId());
                updateComponents(order);
            }

            @Override
            public void onDeleted(OrderItem orderItem) {
                Order order = sm().findOrderById(orderItem.getOrderId());
                selectedOrderItem = null;
                updateComponents(order);
            }
        };
    }

    public void setSelectedItem(OrderItem selectedItem) {
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
            Status().setError("Error in setting selected item.", e);
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
                    orderItemTable.setRowSelectionInterval(ndx, ndx);
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
            selectedOrderItem = null;

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
            selectedOrderItem = (OrderItem) orderItemTable.getValueAtRow(orderItemTable.getSelectedRow());
            updateComponents(lastSelectedOrder);
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
                    itemToOrder.setOrderState(Statics.ItemOrderStates.PLANNED);
                    itemToOrder.save();

                    addItemToOrder(itemToOrder, lastSelectedOrder);
                }
            }
        }
    }

    @Override
    public void onToolBarDelete() {
        deleteSelectedOrderItems(getSelectedOrderItems());
    }

    @Override
    public void onToolBarEdit() {
        if (selectedOrderItem != null) {
            EditItemDialog dialog = new EditItemDialog(application, "Edit item", selectedOrderItem.getItem());
            dialog.showDialog();
        }
    }
}

package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.orderconfirmdialog.OrderConfirmDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.OrderSearchItemDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class OrderPanel extends OrderPanelLayout {

    private CacheChangedListener<Item> itemsChanged;
    private CacheChangedListener<Order> ordersChanged;
    private CacheChangedListener<OrderItem> orderItemsChanged;
    private CacheChangedListener<DistributorPartLink> partNumbersChanged;

    public OrderPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initActions();
        initializeListeners();

        cache().addOnItemsChangedListener(itemsChanged);
        cache().addOnOrdersChangedListener(ordersChanged);
        cache().addOnOrderItemsChangedListener(orderItemsChanged);
        cache().addOnPartNumbersChangedListener(partNumbersChanged);

        updateComponents();
    }

    public OrderItem getSelectedOrderItem() {
        return selectedOrderItem;
    }

    public IOrderItemTableModel getTableModel() {
        return tableModel;
    }

    public Map<String, Item> addItemsToOrder(List<Item> itemsToOrder, Order order) {
        Map<String, Item> failedItems = null;
        for (Item item : itemsToOrder) {
            if (!order.containsItemId(item.getId())) {
                OrderItem orderItem = new OrderItem();
                orderItem.setItemId(item.getId());
                orderItem.setOrderId(order.getId());
                orderItem.setName(item.toString() + " - " + order.toString());

                // Part number
                DistributorPartLink distributorPartLink = sm().findDistributorPartLink(order.getDistributorId(), item.getId());
                if (distributorPartLink != null) {
                    orderItem.setDistributorPartId(distributorPartLink.getId());
                }

                orderItem.save(); // TODO: if more than one item, the Listeners will also fire more than once and gui will update multiple times....
            } else {
                OrderItem orderItem = order.findOrderItemInOrder(item.getId());
                orderItem.setAmount(orderItem.getAmount() + 1);
                orderItem.save();
//                if (failedItems == null) {
//                    failedItems = new HashMap<>();
//                }
//                failedItems.put("Item " + item.getNameText() + " was already in the list..", item);
            }
        }
        return failedItems;
    }

    public Map<String, Item> addOrderItemsToOrder(List<OrderItem> itemsToOrder, Order order) {
        Map<String, Item> failedItems = null;
        for (OrderItem oi : itemsToOrder) {
            if (!order.containsItemId(oi.getItemId())) {

                // Part number
                DistributorPartLink distributorPartLink = sm().findDistributorPartLink(order.getDistributorId(), oi.getId());
                if (distributorPartLink != null) {
                    oi.setDistributorPartId(distributorPartLink.getId());
                }

                oi.save(); // TODO: if more than one item, the Listeners will also fire more than once and gui will update multiple times....
            } else {
                if (failedItems == null) {
                    failedItems = new HashMap<>();
                }
                failedItems.put("Item " + oi.getName() + " was already in the list..", oi.getItem());
            }
        }
        return failedItems;
    }


    private void initActions() {
        initMouseClicked();

        tbOrderFlowPanel.addOrderClickListener(e -> {
            OrderConfirmDialog dialog = new OrderConfirmDialog(application, "Confirm order", selectedOrder);
            dialog.showDialog();
        });
        tbOrderFlowPanel.addReceivedClickListener(e -> {
            OrderConfirmDialog dialog = new OrderConfirmDialog(application, "Confirm receive", selectedOrder);
            dialog.showDialog(OrderConfirmDialog.TAB_ORDER_DETAILS, null);
        });
    }

    private void deleteSelectedOrderItems(final List<OrderItem> itemsToDelete) {
        if (selectedOrder == null) {
            return;
        }

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
                selectedOrder.removeItemFromList(item);
            }
            selectedOrderItem = null;
            selectedOrder.save(); // This will fire the onOrderItemsChanged -> order updated
        }

    }

    private void initMouseClicked() {
        orderItemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    EditItemDialog dialog = new EditItemDialog(application, "Item", selectedOrderItem.getItem());
                    dialog.showDialog();
                }
            }
        });
    }

    private void initializeListeners() {
        setItemsChangedListener();
        setOrdersChangedListener();
        setOrderItemsChangedListener();
        setPartNumbersChangedListener();
    }

    private void setItemsChangedListener() {
        itemsChanged = new CacheChangedListener<Item>() {
            @Override
            public void onInserted(Item item) {
                // No effect here
            }

            @Override
            public void onUpdated(Item newItem) {
                if (selectedOrder != null) {
                    if (selectedOrder.containsItemId(newItem.getId())) { // when new items are added, this should be false
                        tableUpdate();
                    }
                    if (selectedOrderItem != null) {
                        itemDetailPanel.updateComponents(selectedOrderItem.getItem());
                    }
                }
            }

            @Override
            public void onDeleted(Item item) {
                itemDetailPanel.updateComponents();
            }

            @Override
            public void onCacheCleared() {}
        };
    }

    private void setOrdersChangedListener() {
        ordersChanged = new CacheChangedListener<Order>() {
            @Override
            public void onInserted(Order order) {
                selectedOrder = order;
                selectedOrderItem = null;

                tableInitialize(order);
                tableSelectOrderItem(selectedOrderItem);

                treeRecreateNodes();
                final long orderId = treeUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedOrder = SearchManager.sm().findOrderById(orderId);
                    treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });

            }

            @Override
            public void onUpdated(Order newOrder) {
                selectedOrder = newOrder;

                tableSelectOrderItem(selectedOrderItem); // When deleted, this should be null
                treeRecreateNodes();
                final long orderId = treeUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedOrder = SearchManager.sm().findOrderById(orderId);
                    treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });
            }

            @Override
            public void onDeleted(Order order) {
                selectedOrder = null;
                selectedOrderItem = null;

                tableClear();
                tableSelectOrderItem(null);
                treeDeleteOrder(order);

                updateVisibleComponents();
                updateEnabledComponents();
            }

            @Override
            public void onCacheCleared() {
                tableSelectOrderItem(selectedOrderItem); // When deleted, this should be null
                treeRecreateNodes();
                final long orderId = treeUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedOrder = SearchManager.sm().findOrderById(orderId);
                    treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });
            }
        };
    }

    private void setOrderItemsChangedListener() {
        orderItemsChanged = new CacheChangedListener<OrderItem>() {
            @Override
            public void onInserted(OrderItem orderItem) {
                Order order = sm().findOrderById(orderItem.getOrderId());
                order.addItemToList(orderItem);
                // Update table
                selectedOrder = order;
                tableInitialize(selectedOrder);
                treeSelectOrder(selectedOrder);

                // Select and highlight in table
                SwingUtilities.invokeLater(() -> {
                    selectedOrderItem = orderItem;
                    tableAddOrderItem(selectedOrderItem);
                    tableSelectOrderItem(orderItem);

                    // Update stuff
                    updateEnabledComponents();
                    updateVisibleComponents();
                });
            }

            @Override
            public void onUpdated(OrderItem newOrderItem) {
                selectedOrder = sm().findOrderById(newOrderItem.getOrderId());
                selectedOrderItem = newOrderItem;

                final long orderItemId = tableUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedOrderItem = SearchManager.sm().findOrderItemById(orderItemId);
                    tableSelectOrderItem(selectedOrderItem);
                    treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });
            }

            @Override
            public void onDeleted(OrderItem orderItem) {
                // Handled in order change listener
            }

            @Override
            public void onCacheCleared() {}
        };
    }

    private void setPartNumbersChangedListener() {
        partNumbersChanged = new CacheChangedListener<DistributorPartLink>() {
            @Override
            public void onInserted(DistributorPartLink distributorPartLink) {
                if (selectedOrder != null) {
                    if (selectedOrder.containsItemId(distributorPartLink.getItemId())) {
                        tableUpdate();
                    }
                }
            }

            @Override
            public void onUpdated(DistributorPartLink newDistributorPartLink) {
                if (selectedOrder != null) {
                    if (selectedOrder.containsItemId(newDistributorPartLink.getItemId())) {
                        tableUpdate();
                    }
                }
            }

            @Override
            public void onDeleted(DistributorPartLink distributorPartLink) {
                // Should not happen
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
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) ordersTree.getLastSelectedPathComponent();

            if (node == null || ((Order) node.getUserObject()).isUnknown() || !((Order) node.getUserObject()).canBeSaved()) {
                selectedOrder = null;
                return; // Nothing selected
            }
            selectedOrder = (Order) node.getUserObject();
            selectedOrderItem = null;

            application.clearSearch();
            tableInitialize(selectedOrder);
            updateToolBar(selectedOrder);

            updateVisibleComponents();
            updateEnabledComponents();
        }
    }

    //
    // Table selection changed
    //
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            SwingUtilities.invokeLater(() -> {
                selectedOrderItem = tableGetSelectedItem();
                if (selectedOrderItem != null) {
                    itemDetailPanel.updateComponents(selectedOrderItem.getItem());
                    if (selectedOrder != null) {
                        if (selectedOrder.isOrdered() || selectedOrder.isReceived()) {
                            itemDetailPanel.setRemarksPanelVisible(true);
                            orderItemDetailPanel.updateComponents();
                        } else {
                            itemDetailPanel.setRemarksPanelVisible(false);
                            orderItemDetailPanel.updateComponents(selectedOrderItem);
                        }
                    }
                } else {
                    itemDetailPanel.updateComponents();
                    orderItemDetailPanel.updateComponents();
                }
                updateToolBar(selectedOrder);
                updateVisibleComponents();
                updateEnabledComponents();
            });
        }
    }

    //
    //  Table tool bar listener
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        try {
            application.beginWait();
            tableInitialize(selectedOrder);
            treeRecreateNodes();
            final long orderId = treeUpdate();

            SwingUtilities.invokeLater(() -> {
                selectedOrder = SearchManager.sm().findOrderById(orderId);
                treeSelectOrder(selectedOrder);

                updateVisibleComponents();
                updateEnabledComponents();
            });
        } finally {
            application.endWait();
        }
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (selectedOrder != null && !selectedOrder.isUnknown() && selectedOrder.canBeSaved()) {
            OrderSearchItemDialog dialog = new OrderSearchItemDialog(application, "Search item to order");
            if (dialog.showDialog() == IDialog.OK) {
                List<Item> itemsToOrder = dialog.getItemsToOrder();
                if (itemsToOrder != null) {
                    // Update item
                    for (Item item : itemsToOrder) {
                        item.setOrderState(Statics.ItemOrderStates.PLANNED);
                        item.save();
                    }

                    addItemsToOrder(itemsToOrder, selectedOrder);
                }
            }
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        deleteSelectedOrderItems(tableGetAllSelectedOrderItems());
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedOrderItem != null) {
            EditItemDialog dialog = new EditItemDialog(application, "Edit item", selectedOrderItem.getItem());
            dialog.showDialog();
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

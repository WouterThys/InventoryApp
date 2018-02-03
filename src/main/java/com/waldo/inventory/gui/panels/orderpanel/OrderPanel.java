package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.popups.OrderItemPopup;
import com.waldo.inventory.gui.components.popups.OrderPopup;
import com.waldo.inventory.gui.components.tablemodels.IOrderItemTableModel;
import com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog.EditDistributorPartLinkDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.editreceiveditemlocationdialog.EditReceivedItemsLocationDialog;
import com.waldo.inventory.gui.dialogs.orderconfirmdialog.OrderConfirmDialog;
import com.waldo.inventory.gui.dialogs.ordersdialog.OrdersDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.OrderSearchItemDialog;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

import static com.waldo.inventory.gui.Application.imageResource;
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

        cache().addListener(Item.class, itemsChanged);
        cache().addListener(Order.class, ordersChanged);
        cache().addListener(OrderItem.class, orderItemsChanged);
        cache().addListener(DistributorPartLink.class, partNumbersChanged);

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

//                // Part number
//                DistributorPartLink distributorPartLink = sm().findDistributorPartLink(order.getDistributorId(), item.getId());
//                if (distributorPartLink != null) {
//                    orderItem.setDistributorPartId(distributorPartLink.getId());
//                }

                orderItem.save();
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

//                // Part number
//                DistributorPartLink distributorPartLink = sm().findDistributorPartLink(order.getDistributorId(), oi.getId());
//                if (distributorPartLink != null) {
//                    oi.setDistributorPartId(distributorPartLink.getId());
//                }

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
            public void onCacheCleared() {
            }
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

                    if (selectedOrder.isReceived()) {
                        checkOrderedItemsLocations(selectedOrder);
                    }
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
            public void onCacheCleared() {
            }
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
            public void onCacheCleared() {
            }
        };
    }

    private void checkOrderedItemsLocations(Order order) {
        if (order.isReceived()) {
            // Find items without location
            List<Item> itemsWithoutLocation = new ArrayList<>();
            for (OrderItem oi : order.getOrderItems()) {
                if (oi.getItem().getLocationId() <= DbObject.UNKNOWN_ID) {
                    itemsWithoutLocation.add(oi.getItem());
                }
            }

            // There are items without location -> ask to set them
            if (itemsWithoutLocation.size() > 0) {
                int res = JOptionPane.showConfirmDialog(
                        this,
                        "Some items do not have a location yet, do you want to set it now?",
                        "New item locations",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (res == JOptionPane.YES_OPTION) {
                    EditReceivedItemsLocationDialog dialog = new EditReceivedItemsLocationDialog(application, "Set location", itemsWithoutLocation);
                    dialog.showDialog();
                }
            }
        }
    }

    private boolean validateOrderlines(Order order) {
        List<String> errors = checkOrder(order);
        if (errors.size() > 0) {
            showErrors(errors);
            return false;
        }
        return true;
    }

    private List<String> checkOrder(Order order) {
        List<String> errorList = new ArrayList<>();

        if (order == null) {
            errorList.add(" - No order selected..");
        } else {
            if (order.getDistributor() == null) {
                errorList.add(" - Order had no distributor..");
            } else {
                if (order.getDistributor().getOrderFileFormat() != null && !order.getDistributor().getOrderFileFormat().isUnknown()) {

                    if (order.getOrderItems().size() < 1) {
                        errorList.add(" - Order has no items..");
                    } else {
                        List<OrderItem> errorItems = order.missingOrderReferences();
                        if (errorItems.size() > 0) {
                            errorList.add(" - Next order items have no reference: ");
                            for (OrderItem oi : errorItems) {
                                errorList.add(" \t * " + oi.getItem().getName());
                            }
                        }
                    }
                }
            }
        }
        return errorList;
    }

    private void showErrors(List<String> errorList) {
        StringBuilder builder = new StringBuilder();
        builder.append("Creation of order file failed with next ").append(errorList.size()).append("error(s): ").append("\n");
        for (String error : errorList) {
            builder.append(error).append("\n");
        }

        JOptionPane.showMessageDialog(this,
                builder.toString(),
                "Order file errors",
                JOptionPane.ERROR_MESSAGE);
    }


    //
    // Actions
    //
    @Override
    public void onSetOrderItemAmount(OrderItem orderItem, int amount) {
        Order order = getSelectedOrder();
        if (order != null && order.canBeSaved() && (order.isPlanned() || !order.isLocked())) {
            if (orderItem != null) {
                orderItem.setAmount(amount);
                orderItem.save();
            }
        }
    }

    @Override
    void onAddOrder() {
        OrdersDialog dialog = new OrdersDialog(application, "New order", new Order(), true);
        if (dialog.showDialog() == IDialog.OK) {
            Order o = dialog.getOrder();
            o.save();
        }
    }

    @Override
    void onEditOrder(Order order) {
        if (order != null && order.canBeSaved()) {
            OrdersDialog dialog = new OrdersDialog(application, "Edit order", order);
            if (dialog.showDialog() == IDialog.OK) {
                Order o = dialog.getOrder();
                o.save();
            }
        }
    }

    @Override
    void onDeleteOrder(Order order) {
        if (order != null && order.canBeSaved()) {
            int res = JOptionPane.showConfirmDialog(OrderPanel.this, "Are you sure you want to delete \"" + order.getName() + "\"?");
            if (res == JOptionPane.OK_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    List<OrderItem> orderItems = selectedOrder.getOrderItems();

                    order.delete(); // Cascaded delete will delete order items too
                    selectedOrder = null;
                    selectedOrderItem = null;

                    // Do this after delete: items will not be updated in change listener for orders
                    for (OrderItem orderItem : orderItems) {
                        orderItem.getItem().setOrderState(Statics.ItemOrderStates.NoOrder);
                        orderItem.getItem().save();
                    }
                });
            }
        }
    }

    @Override
    void onOrderDetails(Order order) {
        if (order != null && order.canBeSaved()) {
            OrderConfirmDialog dialog = new OrderConfirmDialog(application, "Confirm receive", order);
            if (order.isReceived()) {
                dialog.showDialog(OrderConfirmDialog.TAB_ORDER_DETAILS, null);
            } else {
                dialog.showDialog();
            }
        }
    }

    @Override
    void onMoveToOrdered(Order order) {
        if (order != null && order.canBeSaved() && !order.isOrdered()) {
            // Check
            if (validateOrderlines(order)) {
                // Do order
                order.setDateOrdered(new Date(Calendar.getInstance().getTimeInMillis()));
                order.setLocked(true);
                Application.beginWait(OrderPanel.this);
                try {
                    order.updateItemStates();
                } finally {
                    Application.endWait(OrderPanel.this);
                }
                order.save();

                int res = JOptionPane.showConfirmDialog(
                        this,
                        "Browse order page?",
                        "Browse",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        imageResource.readImage("Common.WebBrowseBig")
                );
                if (res == JOptionPane.YES_OPTION) {
                    // Go to website
                    order.copyOrderLinesToClipboard();
                    try {
                        order.browseOrderPage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    void onMoveToReceived(Order order) {
        if (order != null && order.canBeSaved() && !order.isReceived()) {
            // Do receive
            order.setDateReceived(new Date(Calendar.getInstance().getTimeInMillis()));
            order.setLocked(true);
            Application.beginWait(OrderPanel.this);
            try {
                order.updateItemStates();
                order.updateItemAmounts(true);
            } finally {
                Application.endWait(OrderPanel.this);
            }
            order.save();
        }
    }

    @Override
    void onBackToOrdered(Order order) {
        if (order != null && order.canBeSaved() && order.isReceived()) {
            int res = JOptionPane.showConfirmDialog(
                    OrderPanel.this,
                    "Are you sure you want to move the order back to the \"Ordered\" tab?",
                    "Back to ordered",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                order.setDateReceived((Date) null);
                order.setLocked(true);
                Application.beginWait(OrderPanel.this);
                try {
                    order.updateItemStates();
                    order.updateItemAmounts(false);
                } finally {
                    Application.endWait(OrderPanel.this);
                }
                order.save();
            }
        }
    }

    @Override
    void onBackToPlanned(Order order) {
        if (order != null && order.canBeSaved() && order.isOrdered()) {
            int res = JOptionPane.showConfirmDialog(
                    OrderPanel.this,
                    "Are you sure you want to move the order back to the \"Planned\" tab?",
                    "Back to planned",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                order.setDateReceived((Date) null);
                order.setDateOrdered((Date) null);
                order.setLocked(false);
                Application.beginWait(OrderPanel.this);
                try {
                    order.updateItemStates();
                } finally {
                    Application.endWait(OrderPanel.this);
                }
                order.save();
            }
        }
    }

    private void onSetOrderLocked(Order order, boolean locked) {
        if (order != null) {
            order.setLocked(locked);
            updateToolBar(order);
            updateEnabledComponents();
        }
    }

    @Override
    void onDeleteOrderItem(OrderItem orderItem) {
        if (orderItem != null && selectedOrder != null) {

            int res = JOptionPane.showConfirmDialog(
                    OrderPanel.this,
                    "Are you sure you want to delete " + orderItem.getName() + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                selectedOrder.removeItemFromList(orderItem);
                selectedOrderItem = null;
                selectedOrder.save(); // This will fire the onOrderItemsChanged -> order updated
            }
        }
    }

    @Override
    void onEditItem(OrderItem orderItem) {
        if (orderItem != null) {
            EditItemDialog<Item> dialog = new EditItemDialog<>(application, "Edit item", orderItem.getItem());
            dialog.showDialog();
        }
    }

    @Override
    public void onEditReference(OrderItem orderItem) {
        if (orderItem != null) {
            DistributorPartLink link = orderItem.getDistributorPartLink();
            EditDistributorPartLinkDialog dialog = new EditDistributorPartLinkDialog(
                    application,
                    "Reference & price",
                    link);
            dialog.enableDistributor(false);
            if (dialog.showDialog() == IDialog.OK) {
                link.save();
                tableUpdate();
            }
        }
    }

    //
    // Table and tree click
    //
    @Override
    void onTreeRightClick(MouseEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ordersTree.getLastSelectedPathComponent();
        if (node != null) {
            Order order = (Order) node.getUserObject();
            JPopupMenu popupMenu = null;
            if (order != null && order.canBeSaved()) {
                treeSelectNewOrder(order);

                popupMenu = new OrderPopup(selectedOrder) {
                    @Override
                    public void onEditOrder(Order order) {
                        OrderPanel.this.onEditOrder(order);
                    }

                    @Override
                    public void onDeleteOrder(Order order) {
                        OrderPanel.this.onDeleteOrder(order);
                    }

                    @Override
                    public void onOrderDetails(Order order) {
                        OrderPanel.this.onOrderDetails(order);
                    }

                    @Override
                    public void onMoveToOrdered(Order order) {
                        OrderPanel.this.onMoveToOrdered(order);
                    }

                    @Override
                    public void onMoveToReceived(Order order) {
                        OrderPanel.this.onMoveToReceived(order);
                    }

                    @Override
                    public void onBackToOrdered(Order order) {
                        OrderPanel.this.onBackToOrdered(order);
                    }

                    @Override
                    public void onBackToPlanned(Order order) {
                        OrderPanel.this.onBackToPlanned(order);
                    }

                    @Override
                    public void onLocked(Order order, boolean locked) {
                        OrderPanel.this.onSetOrderLocked(order, locked);
                    }
                };
            }

            if (popupMenu != null) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    void onTableRowClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
            if (SwingUtilities.isRightMouseButton(e)) {
                JPopupMenu popupMenu = new OrderItemPopup(getSelectedOrderItem()) {
                    @Override
                    public void onDeleteOrderItem(OrderItem orderItem) {
                        OrderPanel.this.onDeleteOrderItem(orderItem);
                    }

                    @Override
                    public void onEditReference(OrderItem orderItem) {
                        OrderPanel.this.onEditReference(orderItem);
                    }

                    @Override
                    public void onEditItem(OrderItem item) {
                        OrderPanel.this.onEditItem(item);
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
                        application.orderItem(item);
                    }

                    @Override
                    public void onShowHistory(Item item) {
                        application.showHistory(item);
                    }
                };
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
        if (e.getClickCount() == 2) {
            EditItemDialog<Item> dialog = new EditItemDialog<>(application, "Item", selectedOrderItem.getItem());
            dialog.showDialog();
        }
    }

    //
    // Tree selection interface
    //
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!Application.isUpdating(OrderPanel.this)) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) ordersTree.getLastSelectedPathComponent();

            if (node == null || ((Order) node.getUserObject()).isUnknown() || !((Order) node.getUserObject()).canBeSaved()) {
                selectedOrder = null;
                return; // Nothing selected
            }

            if (selectedOrder != null) {
                selectedOrder.setLocked(!selectedOrder.isPlanned());
            }

            treeSelectNewOrder((Order) node.getUserObject());
        }
    }

    private void treeSelectNewOrder(Order newOrder) {
        selectedOrder = newOrder;
        selectedOrderItem = null;

        application.clearSearch();
        tableInitialize(selectedOrder);
        updateToolBar(selectedOrder);

        updateVisibleComponents();
        updateEnabledComponents();
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
                    itemDetailPanel.updateComponents(selectedOrderItem);
                } else {
                    itemDetailPanel.updateComponents();
                }
                updateToolBar(selectedOrder);
                updateVisibleComponents();
                updateEnabledComponents();
            });
        }
    }


    //
    //  Tool bar listener
    //
    @Override
    public void onToolBarRefresh(IdBToolBar source) {
        if (source.equals(treeToolBar)) {
            treeRecreateNodes();
            final long orderId = treeUpdate();
            final long orderItemId = tableUpdate();

            SwingUtilities.invokeLater(() -> {
                selectedOrder = SearchManager.sm().findOrderById(orderId);
                treeSelectOrder(selectedOrder);
                selectedOrderItem = SearchManager.sm().findOrderItemById(orderItemId);
                tableSelectOrderItem(selectedOrderItem);
            });
        } else {
            Application.beginWait(OrderPanel.this);
            try {
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
                Application.endWait(OrderPanel.this);
            }
        }
    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (source.equals(treeToolBar)) {
            onAddOrder();
        } else {
            if (selectedOrder != null && !selectedOrder.isUnknown() && selectedOrder.canBeSaved()) {
                OrderSearchItemDialog dialog = new OrderSearchItemDialog(application, "Search item to order");
                if (dialog.showDialog() == IDialog.OK) {
                    List<Item> itemsToOrder = dialog.getItemsToOrder();
                    if (itemsToOrder != null) {
                        // Update item
                        for (Item item : itemsToOrder) {
                            item.setOrderState(Statics.ItemOrderStates.Planned);
                            item.save();
                        }

                        addItemsToOrder(itemsToOrder, selectedOrder);
                    }
                }
            }
        }
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        if (source.equals(treeToolBar)) {
            onDeleteOrder(selectedOrder);
        } else {
            deleteSelectedOrderItems(tableGetAllSelectedOrderItems());
        }
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (source.equals(treeToolBar)) {
            onEditOrder(selectedOrder);
        } else {
            onEditItem(selectedOrderItem);
        }
    }

    //
    // Detail panel
    //
    @Override
    public void onShowDataSheet(Item orderItem) {
        if (orderItem != null) {
            application.openDataSheet(orderItem);
        }
    }

    @Override
    public void onOrderItem(Item orderItem) {
        if (orderItem != null) {
            application.orderItem(orderItem);
        }
    }

    @Override
    public void onShowHistory(Item orderItem) {
        if (orderItem != null) {
            application.showHistory(orderItem);
        }
    }

    @Override
    public void onEditPrice(OrderItem orderItem) {
        if (orderItem != null) {
            onEditReference(orderItem);
        }
    }
}

package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.popups.OrderLinePopup;
import com.waldo.inventory.gui.components.popups.OrderPopup;
import com.waldo.inventory.gui.components.tablemodels.IOrderLineTableModel;
import com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog.EditDistributorPartLinkDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.editreceiveditemlocationdialog.EditReceivedItemsLocationDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.OrderSearchItemsDialog;
import com.waldo.inventory.gui.dialogs.pendingordersdialog.PendingOrdersCacheDialog;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IDialog;

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
    private CacheChangedListener<OrderLine> orderLinesChanged;
    private CacheChangedListener<DistributorPartLink> partNumbersChanged;

    public OrderPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initializeListeners();

        cache().addListener(Item.class, itemsChanged);
        cache().addListener(Order.class, ordersChanged);
        cache().addListener(OrderLine.class, orderLinesChanged);
        cache().addListener(DistributorPartLink.class, partNumbersChanged);

        updateComponents();
    }

    private OrderLine getSelectedOrderLine() {
        return selectedOrderLine;
    }

    public IOrderLineTableModel getTableModel() {
        return tableModel;
    }


    public Map<String, Item> addItemsToOrder(List<Item> itemsToOrder, Order order) {
        Map<String, Item> failedItems = null;
        if (order.getDistributorType() == DistributorType.Items) {
            for (Item item : itemsToOrder) {
                try {
                    OrderLine orderLine = order.findOrderLineFor(item);
                    if (orderLine == null) {
                        orderLine = new OrderLine(order, item, 1);
                    }
                    orderLine.save();
                } catch (Exception e) {
                    if (failedItems == null) {
                        failedItems = new HashMap<>();
                    }
                    failedItems.put("Failed to add item " + item.toString(), item);
                }
            }
        } else {
            failedItems = new HashMap<>();
            for(Item item : itemsToOrder) {
                failedItems.put("Can not add items to an order for PCB's", item);
            }
        }
        checkPendingOrders(order);
        return failedItems;
    }

    public Map<String, Item> addOrderItemsToOrder(List<OrderLine> itemsToOrder, Order order) {
        Map<String, Item> failedItems = null;
        if (order.getDistributorType() == DistributorType.Items) {
            for (OrderLine ol : itemsToOrder) {
                try {
                    if (!order.getOrderLines().contains(ol)) {
                        ol.save();
                    } else {
                        ol.setAmount(ol.getAmount() + 1);
                        ol.save();
                    }
                } catch (Exception e) {
                    if (failedItems == null) {
                        failedItems = new HashMap<>();
                    }
                    failedItems.put("Failed to add item " + ol.toString(), ol.getItem());
                }
            }
        } else {
            failedItems = new HashMap<>();
            for(OrderLine orderLine : itemsToOrder) {
                failedItems.put("Can not add items to an order for PCB's", orderLine.getItem());
            }
        }
        checkPendingOrders(order);
        return failedItems;
    }

    private void checkPendingOrders(Order order) {
        if (order != null && order.getDistributorId() > DbObject.UNKNOWN_ID) {
            if (SearchManager.sm().findPendingOrdersByDistributorId(order.getDistributorId()).size() > 0) {
                int res = JOptionPane.showConfirmDialog(
                        this,
                        "There are pending orders for " + order.getDistributor() + ", do you want to add them now?",
                        "Pending orders",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (res == JOptionPane.YES_OPTION) {
                    PendingOrdersCacheDialog dialog = new PendingOrdersCacheDialog(application, "Pending orders");
                    dialog.showDialog();
                }
            }
        }
    }

    private void deleteSelectedOrderItems(final List<OrderLine> itemsToDelete) {
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
            for (OrderLine item : itemsToDelete) {
                selectedOrder.removeOrderLine(item);
            }
            selectedOrderLine = null;
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
                    if (selectedOrder.containsOrderLineFor(newItem)) { // when new items are added, this should be false
                        tableUpdate();
                    }
                    if (selectedOrderLine != null) {
                        detailPanel.updateComponents(selectedOrderLine);
                    }
                }
            }

            @Override
            public void onDeleted(Item item) {
                detailPanel.updateComponents();
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
                selectedOrderLine = null;

                tableInitialize(order);
                tableSelectOrderItem(selectedOrderLine);
                ordersTree.addItem(order);

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

                tableSelectOrderItem(selectedOrderLine); // When deleted, this should be null
                final long orderId = newOrder.getId();
                treeReload();

                SwingUtilities.invokeLater(() -> {
                    selectedOrder = SearchManager.sm().findOrderById(orderId);
                    treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();

                    if (selectedOrder.isReceived()) {
                        switch (selectedOrder.getDistributorType()) {
                            case Items:
                                checkOrderedItemsLocations(selectedOrder);
                                break;
                            case Pcbs:
                                checkOrderedPcbs(selectedOrder);
                                break;
                        }
                    }
                });
            }

            @Override
            public void onDeleted(Order order) {
                selectedOrder = null;
                selectedOrderLine = null;

                tableClear();
                tableSelectOrderItem(null);
                treeDeleteOrder(order);

                updateVisibleComponents();
                updateEnabledComponents();
            }

            @Override
            public void onCacheCleared() {
                tableSelectOrderItem(selectedOrderLine); // When deleted, this should be null
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
        orderLinesChanged = new CacheChangedListener<OrderLine>() {
            @Override
            public void onInserted(OrderLine orderLine) {
                Order order = sm().findOrderById(orderLine.getOrderId());
                order.addOrderLine(orderLine);
                // Update table
                selectedOrder = order;
                tableInitialize(selectedOrder);
                treeSelectOrder(selectedOrder);

                // Select and highlight in table
                SwingUtilities.invokeLater(() -> {
                    selectedOrderLine = orderLine;
                    tableAddOrderItem(selectedOrderLine);
                    tableSelectOrderItem(orderLine);

                    // Update stuff
                    updateEnabledComponents();
                    updateVisibleComponents();
                });
            }

            @Override
            public void onUpdated(OrderLine orderLine) {
                selectedOrder = sm().findOrderById(orderLine.getOrderId());
                selectedOrderLine = orderLine;

                final long orderItemId = tableUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedOrderLine = SearchManager.sm().findOrderLineById(orderItemId);
                    tableSelectOrderItem(selectedOrderLine);
                    treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });
            }

            @Override
            public void onDeleted(OrderLine orderLine) {
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
                    tableUpdate();
                }
            }

            @Override
            public void onUpdated(DistributorPartLink newDistributorPartLink) {
                if (selectedOrder != null) {
                    tableUpdate();
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
        if (order.isReceived() && order.getDistributorType() == DistributorType.Items) {
            // Find items without location
            List<Item> itemsWithoutLocation = new ArrayList<>();
            for (OrderLine oi : order.getOrderLines()) {
                if ((oi.getItem().getLocationId() <= DbObject.UNKNOWN_ID)) {
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

    private void checkOrderedPcbs(Order order) {
        if (order.isReceived() && order.getDistributorType() == DistributorType.Pcbs) {
            for (OrderLine orderLine : order.getOrderLines()) {
                ProjectPcb pcb = orderLine.getPcb();
                if (pcb != null) {
                    List<CreatedPcb> createdPcbs = SearchManager.sm().findCreatedPcbsByPcbAndOrder(pcb.getId(), order.getId());
                    if (createdPcbs.size() == 0) {
                        for (int i = 0; i < orderLine.getAmount(); i++) {
                            CreatedPcb createdPcb = new CreatedPcb(orderLine.getName() + i, pcb, order);
                            createdPcb.setDateCreated(order.getDateReceived());
                            createdPcb.save();
                        }

                        JOptionPane.showMessageDialog(
                                OrderPanel.this,
                                "Created " + orderLine.getAmount() + " pcbs!",
                                "Pcb's created",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
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

                    if (order.getOrderLines().size() < 1) {
                        errorList.add(" - Order has no items..");
                    } else {
                        List<OrderLine> errorItems = order.missingOrderReferences();
                        if (errorItems.size() > 0) {
                            errorList.add(" - Next order items have no reference: ");
                            for (OrderLine oi : errorItems) {
                                errorList.add(" \t * " + oi.getName());
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
    public void onSetOrderItemAmount(OrderLine line, int amount) {
        Order order = getSelectedOrder();
        if (order != null && order.canBeSaved() && (order.isPlanned() || !order.isLocked())) {
            if (line != null) {
                line.setAmount(amount);
                line.save();
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
                    order.updateLineStates();
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
                        imageResource.readIcon("Common.WebBrowseBig")
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
                order.updateLineStates();
                order.updateLineAmounts(true);
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
                    order.updateLineStates();
                    order.updateLineAmounts(false);
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
                    order.updateLineStates();
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
    void onDeleteOrderItem(OrderLine orderItem) {
        if (orderItem != null && selectedOrder != null) {

            int res = JOptionPane.showConfirmDialog(
                    OrderPanel.this,
                    "Are you sure you want to delete " + orderItem.getName() + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                selectedOrder.removeOrderLine(orderItem);
                selectedOrderLine = null;
                selectedOrder.save(); // This will fire the onOrderItemsChanged -> order updated
            }
        }
    }

    @Override
    void onEditItem(Item item) {
        if (item != null) {
            EditItemDialog<Item> dialog = new EditItemDialog<>(application, "Edit item", item);
            dialog.showDialog();
        }
    }

    @Override
    public void onEditReference(OrderLine line) {
        if (line != null && selectedOrder != null) {
            DistributorPartLink link = line.getDistributorPartLink();
            if (link == null) {
                long id = 0;
                switch (selectedOrder.getDistributorType()) {
                    case Items:
                        id = line.getItemId();
                        break;
                    case Pcbs:
                        id = line.getPcbId();
                        break;
                }
                link = new DistributorPartLink(selectedOrder.getDistributor(), id);
            }
            EditDistributorPartLinkDialog dialog = new EditDistributorPartLinkDialog(application, link);
            dialog.enableDistributor(false);
            if (dialog.showDialog() == IDialog.OK) {
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
                        previewPanel.editOrder(order);
                    }

                    @Override
                    public void onDeleteOrder(Order order) {
                        previewPanel.deleteOrder(order);
                    }

                    @Override
                    public void onOrderDetails(Order order) {
                        previewPanel.viewOrderDetails(order);
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
                JPopupMenu popupMenu = new OrderLinePopup(getSelectedOrderLine()) {
                    @Override
                    public void onDeleteOrderItem(OrderLine orderItem) {
                        OrderPanel.this.onDeleteOrderItem(orderItem);
                    }

                    @Override
                    public void onEditReference(OrderLine orderItem) {
                        OrderPanel.this.onEditReference(orderItem);
                    }

                    @Override
                    public void onEditItem(Item item) {
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
            if (selectedOrder.getDistributorType() == DistributorType.Items) {
                EditItemDialog<Item> dialog = new EditItemDialog<>(application, "Item", selectedOrderLine.getItem());
                dialog.showDialog();
            }
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
        selectedOrderLine = null;

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
                selectedOrderLine = tableGetSelectedItem();
                if (selectedOrderLine != null) {
                    detailPanel.updateComponents(selectedOrderLine);
                } else {
                    detailPanel.updateComponents();
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
            Application.beginWait(OrderPanel.this);
            try {
                treeReload();
                tableInitialize(selectedOrder);
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

    @Override
    public void onToolBarAdd(IdBToolBar source) {
            if (selectedOrder != null && !selectedOrder.isUnknown() && selectedOrder.canBeSaved()) {
                OrderSearchItemsDialog dialog = new OrderSearchItemsDialog(application, selectedOrder);
                dialog.showDialog();
//                if (dialog.showDialog() == ICacheDialog.OK) {
//                    List<Item> itemsToOrder = dialog.getItemsToOrder();
//                    if (itemsToOrder != null) {
//                        // Update item
//                        for (Item item : itemsToOrder) {
//                            item.updateOrderState();//(Statics.OrderStates.Planned);
//                            //item.save();
//                        }
//
//                        addItemsToOrder(itemsToOrder, selectedOrder);
//                    }
//                }
                // TODO #1 Advanced search dialog
            }

    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        deleteSelectedOrderItems(tableGetAllSelectedOrderItems());
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedOrder != null) {
            switch (selectedOrder.getDistributorType()) {
                case Items:
                    onEditItem( selectedOrderLine.getItem());
                    break;
                case Pcbs:
                    // TODO
                    break;
            }
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
    public void onEditPrice(OrderLine orderItem) {
        if (orderItem != null) {
            onEditReference(orderItem);
        }
    }
}

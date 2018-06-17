package com.waldo.inventory.gui.panels.orderpanel;

import com.waldo.inventory.Utils.Statics.DistributorType;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.tablemodels.IOrderLineTableModel;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.editreceiveditemlocationdialog.EditReceivedItemsLocationDialog;
import com.waldo.inventory.managers.OrderManager;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.waldo.inventory.gui.Application.imageResource;
import static com.waldo.inventory.managers.CacheManager.cache;
import static com.waldo.inventory.managers.SearchManager.sm;

public class ItemOrderPanel extends ItemOrderPanelLayout {


    @Override
    public void onSetOrderItemAmount(AbstractOrderLine line, int amount) {

    }

    @Override
    public void onEditReference(AbstractOrderLine line) {

    }

    @Override
    public void onEditPrice(AbstractOrderLine line) {

    }

    private CacheChangedListener<Item> itemsChanged;
    private CacheChangedListener<ItemOrder> itemOrdersChanged;
    private CacheChangedListener<ItemOrderLine> itemOrderLinesChanged;
    private CacheChangedListener<DistributorPartLink> partNumbersChanged;

    public ItemOrderPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initializeListeners();

        cache().addListener(Item.class, itemsChanged);
        cache().addListener(ItemOrder.class, itemOrdersChanged);
        cache().addListener(ItemOrderLine.class, itemOrderLinesChanged);
        cache().addListener(DistributorPartLink.class, partNumbersChanged);

        updateComponents();
    }

    private AbstractOrderLine getSelectedOrderLine() {
        return selectedOrderLine;
    }

    public IOrderLineTableModel getTableModel() {
        return tableModel;
    }


    private void deleteSelectedOrderItems(final List<ItemOrderLine> itemsToDelete) {
        if (selectedOrder == null) {
            return;
        }

        int result = JOptionPane.CANCEL_OPTION;
        if (itemsToDelete.size() == 1) {
            result = JOptionPane.showConfirmDialog(
                    ItemOrderPanel.this,
                    "Are you sure you want to delete " + itemsToDelete.get(0).getName() + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
        } else if (itemsToDelete.size() > 1) {
            result = JOptionPane.showConfirmDialog(
                    ItemOrderPanel.this,
                    "Are you sure you want to delete " + itemsToDelete.size() + " items?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
        }
        if (result == JOptionPane.OK_OPTION) {
            for (ItemOrderLine item : itemsToDelete) {
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
        itemOrdersChanged = new CacheChangedListener<ItemOrder>() {
            @Override
            public void onInserted(ItemOrder itemOrder) {
                selectedOrder = itemOrder;
                selectedOrderLine = null;

                tableInitialize(itemOrder);
                tableSelectOrderItem(selectedOrderLine);
                ordersTree.addItem(itemOrder);

                final long orderId = treeUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedOrder = SearchManager.sm().findItemOrderById(orderId);
                    //treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });

            }

            @Override
            public void onUpdated(ItemOrder newItemOrder) {
                selectedOrder = newItemOrder;

                tableSelectOrderItem(selectedOrderLine); // When deleted, this should be null
                final long orderId = newItemOrder.getId();
                treeReload();

                SwingUtilities.invokeLater(() -> {
                    selectedOrder = SearchManager.sm().findItemOrderById(orderId);
                    //treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();

                    if (selectedOrder.isReceived()) {
                        //checkOrderedItemsLocations(selectedOrder);
                    }
                });
            }

            @Override
            public void onDeleted(ItemOrder itemOrder) {
                selectedOrder = null;
                selectedOrderLine = null;

                tableClear();
                tableSelectOrderItem(null);
                treeDeleteOrder(itemOrder);

                updateVisibleComponents();
                updateEnabledComponents();
            }

            @Override
            public void onCacheCleared() {
                tableSelectOrderItem(selectedOrderLine); // When deleted, this should be null
                final long orderId = treeUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedOrder = SearchManager.sm().findItemOrderById(orderId);
                    //treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });
            }
        };
    }

    private void setOrderItemsChangedListener() {
        itemOrderLinesChanged = new CacheChangedListener<ItemOrderLine>() {
            @Override
            public void onInserted(ItemOrderLine itemOrderLine) {
                ItemOrder itemOrder = sm().findItemOrderById(itemOrderLine.getOrderId());
                itemOrder.addOrderLine(itemOrderLine);
                // Update table
                selectedOrder = itemOrder;
                tableInitialize(selectedOrder);
                //treeSelectOrder(selectedOrder);

                // Select and highlight in table
                SwingUtilities.invokeLater(() -> {
                    selectedOrderLine = itemOrderLine;
                    tableAddOrderItem(selectedOrderLine);
                    tableSelectOrderItem(itemOrderLine);

                    // Update stuff
                    updateEnabledComponents();
                    updateVisibleComponents();
                });
            }

            @Override
            public void onUpdated(ItemOrderLine itemOrderLine) {
                selectedOrder = sm().findItemOrderById(itemOrderLine.getOrderId());
                selectedOrderLine = itemOrderLine;

                final long orderItemId = tableUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedOrderLine = SearchManager.sm().findItemOrderLineById(orderItemId);
                    tableSelectOrderItem(selectedOrderLine);
                    //treeSelectOrder(selectedOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });
            }

            @Override
            public void onDeleted(ItemOrderLine itemOrderLine) {
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

    private void checkOrderedItemsLocations(ItemOrder itemOrder) {
        if (itemOrder.isReceived() && itemOrder.getDistributorType() == DistributorType.Items) {
            // Find items without location
            List<Item> itemsWithoutLocation = new ArrayList<>();
//            for (ItemOrderLine oi : itemOrder.getItemOrderLines()) {
//                if ((oi.getItem().getLocationId() <= DbObject.UNKNOWN_ID)) {
//                    itemsWithoutLocation.add(oi.getItem());
//                }
//            }

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


    //
    // Actions
    //
//    @Override
//    public void onSetOrderItemAmount(ItemOrderLine line, int amount) {
//        ItemOrder itemOrder = getSelectedOrder();
//        if (itemOrder != null && itemOrder.canBeSaved() && (itemOrder.isPlanned() || !itemOrder.isLocked())) {
//            if (line != null) {
//                line.setAmount(amount);
//                line.save();
//            }
//        }
//    }

    @Override
    void onMoveToOrdered(AbstractOrder itemOrder) {
        Application.beginWait(ItemOrderPanel.this);
        try {
            if (OrderManager.moveToOrdered(itemOrder)) {
                int res = JOptionPane.showConfirmDialog(
                        this,
                        "Browse itemOrder page?",
                        "Browse",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        imageResource.readIcon("Common.WebBrowseBig")
                );
                if (res == JOptionPane.YES_OPTION) {
                    // Go to website
                    itemOrder.copyOrderLinesToClipboard();
                    SwingUtilities.invokeLater(() -> {
                        try {
                            itemOrder.browseOrderPage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } finally {
            Application.endWait(ItemOrderPanel.this);
        }
    }

    @Override
    void onMoveToReceived(AbstractOrder itemOrder) {
        Application.beginWait(ItemOrderPanel.this);
        try {
            OrderManager.moveToReceived(itemOrder);
        } finally {
            Application.endWait(ItemOrderPanel.this);
        }
    }

    @Override
    void onBackToOrdered(AbstractOrder itemOrder) {
        if (itemOrder != null && itemOrder.canBeSaved() && itemOrder.isReceived()) {
            int res = JOptionPane.showConfirmDialog(
                    ItemOrderPanel.this,
                    "Are you sure you want to move the itemOrder back to the \"Ordered\" tab?",
                    "Back to ordered",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                Application.beginWait(ItemOrderPanel.this);
                try {
                    OrderManager.backToOrdered(itemOrder);
                } finally {
                    Application.endWait(ItemOrderPanel.this);
                }
            }
        }
    }

    @Override
    void onBackToPlanned(AbstractOrder itemOrder) {
        if (itemOrder != null && itemOrder.canBeSaved() && itemOrder.isOrdered()) {
            int res = JOptionPane.showConfirmDialog(
                    ItemOrderPanel.this,
                    "Are you sure you want to move the itemOrder back to the \"Planned\" tab?",
                    "Back to planned",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                Application.beginWait(ItemOrderPanel.this);
                try {
                    OrderManager.backToPlanned(itemOrder);
                } finally {
                    Application.endWait(ItemOrderPanel.this);
                }
            }
        }
    }

    private void onSetOrderLocked(ItemOrder itemOrder, boolean locked) {
        if (itemOrder != null) {
            itemOrder.setLocked(locked);
            updateToolBar(itemOrder);
            updateEnabledComponents();
        }
    }

    @Override
    void onDeleteOrderItem(ItemOrderLine orderItem) {
        if (orderItem != null && selectedOrder != null) {

            int res = JOptionPane.showConfirmDialog(
                    ItemOrderPanel.this,
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

//    @Override
//    public void onEditReference(ItemOrderLine line) {
//        if (line != null && selectedOrder != null) {
//            DistributorPartLink link = line.getDistributorPartLink();
//            if (link == null) {
//                long id = line.getItemId();
//                link = new DistributorPartLink(selectedOrder.getDistributor(), id);
//            }
//            EditDistributorPartLinkDialog dialog = new EditDistributorPartLinkDialog(application, link);
//            dialog.enableDistributor(false);
//            if (dialog.showDialog() == IDialog.OK) {
//                tableUpdate();
//            }
//        }
//    }

    //
    // Table and tree click
    //
    @Override
    void onTreeRightClick(MouseEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) ordersTree.getLastSelectedPathComponent();
        if (node != null) {
            ItemOrder itemOrder = (ItemOrder) node.getUserObject();
            JPopupMenu popupMenu = null;
            if (itemOrder != null && itemOrder.canBeSaved()) {
                treeSelectNewOrder(itemOrder);

//                popupMenu = new OrderPopup(selectedOrder) {
//                    @Override
//                    public void onEditOrder(ItemOrder itemOrder) {
//                        previewPanel.editOrder(itemOrder);
//                    }
//
//                    @Override
//                    public void onDeleteOrder(ItemOrder itemOrder) {
//                        previewPanel.deleteOrder(itemOrder);
//                    }
//
//                    @Override
//                    public void onOrderDetails(ItemOrder itemOrder) {
//                        previewPanel.viewOrderDetails(itemOrder);
//                    }
//
//                    @Override
//                    public void onMoveToOrdered(ItemOrder itemOrder) {
//                        ItemOrderPanel.this.onMoveToOrdered(itemOrder);
//                    }
//
//                    @Override
//                    public void onMoveToReceived(ItemOrder itemOrder) {
//                        ItemOrderPanel.this.onMoveToReceived(itemOrder);
//                    }
//
//                    @Override
//                    public void onBackToOrdered(ItemOrder itemOrder) {
//                        ItemOrderPanel.this.onBackToOrdered(itemOrder);
//                    }
//
//                    @Override
//                    public void onBackToPlanned(ItemOrder itemOrder) {
//                        ItemOrderPanel.this.onBackToPlanned(itemOrder);
//                    }
//
//                    @Override
//                    public void onLocked(ItemOrder itemOrder, boolean locked) {
//                        ItemOrderPanel.this.onSetOrderLocked(itemOrder, locked);
//                    }
//                };
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
//                JPopupMenu popupMenu = new OrderLinePopup(getSelectedOrderLine()) {
//                    @Override
//                    public void onDeleteOrderItem(ItemOrderLine orderItem) {
//                        ItemOrderPanel.this.onDeleteOrderItem(orderItem);
//                    }
//
//                    @Override
//                    public void onEditReference(ItemOrderLine orderItem) {
//                        ItemOrderPanel.this.onEditReference(orderItem);
//                    }
//
//                    @Override
//                    public void onEditItem(Item item) {
//                        ItemOrderPanel.this.onEditItem(item);
//                    }
//
//                    @Override
//                    public void onOpenLocalDataSheet(Item item) {
//                        application.openDataSheet(item, false);
//                    }
//
//                    @Override
//                    public void onOpenOnlineDataSheet(Item item) {
//                        application.openDataSheet(item, true);
//                    }
//
//                    @Override
//                    public void onOrderItem(Item item) {
//                        application.orderItem(item);
//                    }
//
//                    @Override
//                    public void onShowHistory(Item item) {
//                        application.showHistory(item);
//                    }
//                };
//                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
        if (e.getClickCount() == 2) {
            if (selectedOrder.getDistributorType() == DistributorType.Items) {
//                EditItemDialog<Item> dialog = new EditItemDialog<>(application, "Item", selectedOrderLine.getItem());
//                dialog.showDialog();
            }
        }
    }

    //
    // Tree selection interface
    //
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (!Application.isUpdating(ItemOrderPanel.this)) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) ordersTree.getLastSelectedPathComponent();

            if (node == null || ((ItemOrder) node.getUserObject()).isUnknown() || !((ItemOrder) node.getUserObject()).canBeSaved()) {
                selectedOrder = null;
                return; // Nothing selected
            }

            if (selectedOrder != null) {
                selectedOrder.setLocked(!selectedOrder.isPlanned());
            }

            treeSelectNewOrder((ItemOrder) node.getUserObject());
        }
    }

    private void treeSelectNewOrder(ItemOrder newItemOrder) {
        selectedOrder = newItemOrder;
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
        Application.beginWait(ItemOrderPanel.this);
        try {
            treeReload();
            tableInitialize(selectedOrder);
            final long orderId = treeUpdate();

            SwingUtilities.invokeLater(() -> {
//                selectedOrder = SearchManager.sm().findItemOrderById(orderId);
//                treeSelectOrder(selectedOrder);
//
//                updateVisibleComponents();
//                updateEnabledComponents();
            });
        } finally {
            Application.endWait(ItemOrderPanel.this);
        }

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (selectedOrder != null && !selectedOrder.isUnknown() && selectedOrder.canBeSaved()) {
//            OrderSearchItemsDialog dialog = new OrderSearchItemsDialog(application, selectedOrder);
//            dialog.showDialog();
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
//        deleteSelectedOrderItems(tableGetAllSelectedOrderItems());
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        if (selectedOrder != null) {
            switch (selectedOrder.getDistributorType()) {
                case Items:
//                    onEditItem(selectedOrderLine.getItem());
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

//    @Override
//    public void onEditPrice(ItemOrderLine orderItem) {
//        if (orderItem != null) {
//            onEditReference(orderItem);
//        }
//    }
}

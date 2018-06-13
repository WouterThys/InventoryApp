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
import com.waldo.inventory.managers.OrderManager;
import com.waldo.inventory.managers.SearchManager;
import com.waldo.utils.icomponents.IDialog;

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

public class OrderPanel extends OrderPanelLayout {

    private CacheChangedListener<Item> itemsChanged;
    private CacheChangedListener<ItemOrder> ordersChanged;
    private CacheChangedListener<ItemOrderLine> orderLinesChanged;
    private CacheChangedListener<DistributorPartLink> partNumbersChanged;

    public OrderPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();
        initializeListeners();

        cache().addListener(Item.class, itemsChanged);
        cache().addListener(ItemOrder.class, ordersChanged);
        cache().addListener(ItemOrderLine.class, orderLinesChanged);
        cache().addListener(DistributorPartLink.class, partNumbersChanged);

        updateComponents();
    }

    private ItemOrderLine getSelectedOrderLine() {
        return selectedItemOrderLine;
    }

    public IOrderLineTableModel getTableModel() {
        return tableModel;
    }


    private void deleteSelectedOrderItems(final List<ItemOrderLine> itemsToDelete) {
        if (selectedItemOrder == null) {
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
            for (ItemOrderLine item : itemsToDelete) {
                selectedItemOrder.removeOrderLine(item);
            }
            selectedItemOrderLine = null;
            selectedItemOrder.save(); // This will fire the onOrderItemsChanged -> order updated
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
                if (selectedItemOrder != null) {
                    if (selectedItemOrder.containsOrderLineFor(newItem)) { // when new items are added, this should be false
                        tableUpdate();
                    }
                    if (selectedItemOrderLine != null) {
                        detailPanel.updateComponents(selectedItemOrderLine);
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
        ordersChanged = new CacheChangedListener<ItemOrder>() {
            @Override
            public void onInserted(ItemOrder itemOrder) {
                selectedItemOrder = itemOrder;
                selectedItemOrderLine = null;

                tableInitialize(itemOrder);
                tableSelectOrderItem(selectedItemOrderLine);
                ordersTree.addItem(itemOrder);

                final long orderId = treeUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedItemOrder = SearchManager.sm().findItemOrderById(orderId);
                    treeSelectOrder(selectedItemOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });

            }

            @Override
            public void onUpdated(ItemOrder newItemOrder) {
                selectedItemOrder = newItemOrder;

                tableSelectOrderItem(selectedItemOrderLine); // When deleted, this should be null
                final long orderId = newItemOrder.getId();
                treeReload();

                SwingUtilities.invokeLater(() -> {
                    selectedItemOrder = SearchManager.sm().findItemOrderById(orderId);
                    treeSelectOrder(selectedItemOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();

                    if (selectedItemOrder.isReceived()) {
                        switch (selectedItemOrder.getDistributorType()) {
                            case Items:
                                checkOrderedItemsLocations(selectedItemOrder);
                                break;
                            case Pcbs:
                                checkOrderedPcbs(selectedItemOrder);
                                break;
                        }
                    }
                });
            }

            @Override
            public void onDeleted(ItemOrder itemOrder) {
                selectedItemOrder = null;
                selectedItemOrderLine = null;

                tableClear();
                tableSelectOrderItem(null);
                treeDeleteOrder(itemOrder);

                updateVisibleComponents();
                updateEnabledComponents();
            }

            @Override
            public void onCacheCleared() {
                tableSelectOrderItem(selectedItemOrderLine); // When deleted, this should be null
                final long orderId = treeUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedItemOrder = SearchManager.sm().findItemOrderById(orderId);
                    treeSelectOrder(selectedItemOrder);

                    updateVisibleComponents();
                    updateEnabledComponents();
                });
            }
        };
    }

    private void setOrderItemsChangedListener() {
        orderLinesChanged = new CacheChangedListener<ItemOrderLine>() {
            @Override
            public void onInserted(ItemOrderLine itemOrderLine) {
                ItemOrder itemOrder = sm().findItemOrderById(itemOrderLine.getOrderId());
                itemOrder.addOrderLine(itemOrderLine);
                // Update table
                selectedItemOrder = itemOrder;
                tableInitialize(selectedItemOrder);
                treeSelectOrder(selectedItemOrder);

                // Select and highlight in table
                SwingUtilities.invokeLater(() -> {
                    selectedItemOrderLine = itemOrderLine;
                    tableAddOrderItem(selectedItemOrderLine);
                    tableSelectOrderItem(itemOrderLine);

                    // Update stuff
                    updateEnabledComponents();
                    updateVisibleComponents();
                });
            }

            @Override
            public void onUpdated(ItemOrderLine itemOrderLine) {
                selectedItemOrder = sm().findItemOrderById(itemOrderLine.getOrderId());
                selectedItemOrderLine = itemOrderLine;

                final long orderItemId = tableUpdate();

                SwingUtilities.invokeLater(() -> {
                    selectedItemOrderLine = SearchManager.sm().findItemOrderLineById(orderItemId);
                    tableSelectOrderItem(selectedItemOrderLine);
                    treeSelectOrder(selectedItemOrder);

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
                if (selectedItemOrder != null) {
                    tableUpdate();
                }
            }

            @Override
            public void onUpdated(DistributorPartLink newDistributorPartLink) {
                if (selectedItemOrder != null) {
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
            for (ItemOrderLine oi : itemOrder.getItemOrderLines()) {
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

    private void checkOrderedPcbs(ItemOrder itemOrder) {
        if (itemOrder.isReceived() && itemOrder.getDistributorType() == DistributorType.Pcbs) {
            for (ItemOrderLine itemOrderLine : itemOrder.getItemOrderLines()) {
                ProjectPcb pcb = itemOrderLine.getPcb();
                if (pcb != null) {
                    List<CreatedPcb> createdPcbs = SearchManager.sm().findCreatedPcbsByPcbAndOrder(pcb.getId(), itemOrder.getId());
                    if (createdPcbs.size() == 0) {
                        for (int i = 0; i < itemOrderLine.getAmount(); i++) {
                            CreatedPcb createdPcb = new CreatedPcb(itemOrderLine.getName() + i, pcb, itemOrder);
                            createdPcb.setDateCreated(itemOrder.getDateReceived());
                            createdPcb.save();
                        }

                        JOptionPane.showMessageDialog(
                                OrderPanel.this,
                                "Created " + itemOrderLine.getAmount() + " pcbs!",
                                "Pcb's created",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }
        }
    }


    //
    // Actions
    //
    @Override
    public void onSetOrderItemAmount(ItemOrderLine line, int amount) {
        ItemOrder itemOrder = getSelectedItemOrder();
        if (itemOrder != null && itemOrder.canBeSaved() && (itemOrder.isPlanned() || !itemOrder.isLocked())) {
            if (line != null) {
                line.setAmount(amount);
                line.save();
            }
        }
    }

    @Override
    void onMoveToOrdered(ItemOrder itemOrder) {
        Application.beginWait(OrderPanel.this);
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
            Application.endWait(OrderPanel.this);
        }
    }

    @Override
    void onMoveToReceived(ItemOrder itemOrder) {
        Application.beginWait(OrderPanel.this);
        try {
            OrderManager.moveToReceived(itemOrder);
        } finally {
            Application.endWait(OrderPanel.this);
        }
    }

    @Override
    void onBackToOrdered(ItemOrder itemOrder) {
        if (itemOrder != null && itemOrder.canBeSaved() && itemOrder.isReceived()) {
            int res = JOptionPane.showConfirmDialog(
                    OrderPanel.this,
                    "Are you sure you want to move the itemOrder back to the \"Ordered\" tab?",
                    "Back to ordered",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                Application.beginWait(OrderPanel.this);
                try {
                    OrderManager.backToOrdered(itemOrder);
                } finally {
                    Application.endWait(OrderPanel.this);
                }
            }
        }
    }

    @Override
    void onBackToPlanned(ItemOrder itemOrder) {
        if (itemOrder != null && itemOrder.canBeSaved() && itemOrder.isOrdered()) {
            int res = JOptionPane.showConfirmDialog(
                    OrderPanel.this,
                    "Are you sure you want to move the itemOrder back to the \"Planned\" tab?",
                    "Back to planned",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (res == JOptionPane.YES_OPTION) {
                Application.beginWait(OrderPanel.this);
                try {
                    OrderManager.backToPlanned(itemOrder);
                } finally {
                    Application.endWait(OrderPanel.this);
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
        if (orderItem != null && selectedItemOrder != null) {

            int res = JOptionPane.showConfirmDialog(
                    OrderPanel.this,
                    "Are you sure you want to delete " + orderItem.getName() + "?",
                    "Confirm delete",
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                selectedItemOrder.removeOrderLine(orderItem);
                selectedItemOrderLine = null;
                selectedItemOrder.save(); // This will fire the onOrderItemsChanged -> order updated
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
    public void onEditReference(ItemOrderLine line) {
        if (line != null && selectedItemOrder != null) {
            DistributorPartLink link = line.getDistributorPartLink();
            if (link == null) {
                long id = 0;
                switch (selectedItemOrder.getDistributorType()) {
                    case Items:
                        id = line.getItemId();
                        break;
                    case Pcbs:
                        id = line.getPcbId();
                        break;
                }
                link = new DistributorPartLink(selectedItemOrder.getDistributor(), id);
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
            ItemOrder itemOrder = (ItemOrder) node.getUserObject();
            JPopupMenu popupMenu = null;
            if (itemOrder != null && itemOrder.canBeSaved()) {
                treeSelectNewOrder(itemOrder);

                popupMenu = new OrderPopup(selectedItemOrder) {
                    @Override
                    public void onEditOrder(ItemOrder itemOrder) {
                        previewPanel.editOrder(itemOrder);
                    }

                    @Override
                    public void onDeleteOrder(ItemOrder itemOrder) {
                        previewPanel.deleteOrder(itemOrder);
                    }

                    @Override
                    public void onOrderDetails(ItemOrder itemOrder) {
                        previewPanel.viewOrderDetails(itemOrder);
                    }

                    @Override
                    public void onMoveToOrdered(ItemOrder itemOrder) {
                        OrderPanel.this.onMoveToOrdered(itemOrder);
                    }

                    @Override
                    public void onMoveToReceived(ItemOrder itemOrder) {
                        OrderPanel.this.onMoveToReceived(itemOrder);
                    }

                    @Override
                    public void onBackToOrdered(ItemOrder itemOrder) {
                        OrderPanel.this.onBackToOrdered(itemOrder);
                    }

                    @Override
                    public void onBackToPlanned(ItemOrder itemOrder) {
                        OrderPanel.this.onBackToPlanned(itemOrder);
                    }

                    @Override
                    public void onLocked(ItemOrder itemOrder, boolean locked) {
                        OrderPanel.this.onSetOrderLocked(itemOrder, locked);
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
                    public void onDeleteOrderItem(ItemOrderLine orderItem) {
                        OrderPanel.this.onDeleteOrderItem(orderItem);
                    }

                    @Override
                    public void onEditReference(ItemOrderLine orderItem) {
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
            if (selectedItemOrder.getDistributorType() == DistributorType.Items) {
                EditItemDialog<Item> dialog = new EditItemDialog<>(application, "Item", selectedItemOrderLine.getItem());
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

            if (node == null || ((ItemOrder) node.getUserObject()).isUnknown() || !((ItemOrder) node.getUserObject()).canBeSaved()) {
                selectedItemOrder = null;
                return; // Nothing selected
            }

            if (selectedItemOrder != null) {
                selectedItemOrder.setLocked(!selectedItemOrder.isPlanned());
            }

            treeSelectNewOrder((ItemOrder) node.getUserObject());
        }
    }

    private void treeSelectNewOrder(ItemOrder newItemOrder) {
        selectedItemOrder = newItemOrder;
        selectedItemOrderLine = null;

        tableInitialize(selectedItemOrder);
        updateToolBar(selectedItemOrder);

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
                selectedItemOrderLine = tableGetSelectedItem();
                if (selectedItemOrderLine != null) {
                    detailPanel.updateComponents(selectedItemOrderLine);
                } else {
                    detailPanel.updateComponents();
                }
                updateToolBar(selectedItemOrder);
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
            tableInitialize(selectedItemOrder);
            final long orderId = treeUpdate();

            SwingUtilities.invokeLater(() -> {
                selectedItemOrder = SearchManager.sm().findItemOrderById(orderId);
                treeSelectOrder(selectedItemOrder);

                updateVisibleComponents();
                updateEnabledComponents();
            });
        } finally {
            Application.endWait(OrderPanel.this);
        }

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        if (selectedItemOrder != null && !selectedItemOrder.isUnknown() && selectedItemOrder.canBeSaved()) {
            OrderSearchItemsDialog dialog = new OrderSearchItemsDialog(application, selectedItemOrder);
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
//                        addItemsToOrder(itemsToOrder, selectedItemOrder);
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
        if (selectedItemOrder != null) {
            switch (selectedItemOrder.getDistributorType()) {
                case Items:
                    onEditItem(selectedItemOrderLine.getItem());
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
    public void onEditPrice(ItemOrderLine orderItem) {
        if (orderItem != null) {
            onEditReference(orderItem);
        }
    }
}

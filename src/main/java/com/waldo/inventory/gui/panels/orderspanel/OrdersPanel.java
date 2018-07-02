package com.waldo.inventory.gui.panels.orderspanel;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.popups.OrderLinePopup;
import com.waldo.inventory.gui.components.popups.OrderPopup;
import com.waldo.inventory.gui.dialogs.editdistributorpartlinkdialog.EditDistributorPartLinkDialog;
import com.waldo.inventory.gui.dialogs.edititemdialog.EditItemDialog;
import com.waldo.inventory.gui.dialogs.ordersearchitemdialog.OrderSearchItemsDialog;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.waldo.inventory.managers.CacheManager.cache;

public class OrdersPanel extends OrdersPanelLayout {


    public OrdersPanel(Application application) {
        super(application);

        initializeComponents();
        initializeLayouts();

        cache().addListener(ItemOrder.class, createItemOrderListener());
        cache().addListener(PcbOrder.class, createPcbOrderListener());
        cache().addListener(ItemOrderLine.class, createItemOrderLineListener());
        cache().addListener(PcbOrderLine.class, createPcbOrderLineListener());
        cache().addListener(DistributorPartLink.class, createDistributorPartLinkListener());

        updateComponents();
    }

    // Listeners
    private CacheChangedListener<ItemOrder> createItemOrderListener() {
        return new CacheChangedListener<ItemOrder>() {
            @Override
            public void onInserted(ItemOrder order) {
                onOrderAdded(order);
            }

            @Override
            public void onUpdated(ItemOrder order) {
                onOrderUpdated(order);
            }

            @Override
            public void onDeleted(ItemOrder order) {
                onOrderDeleted(order);
            }

            @Override
            public void onCacheCleared() {

            }
        };
    }

    private CacheChangedListener<PcbOrder> createPcbOrderListener() {
        return new CacheChangedListener<PcbOrder>() {
            @Override
            public void onInserted(PcbOrder order) {
                onOrderAdded(order);
            }

            @Override
            public void onUpdated(PcbOrder order) {
                onOrderUpdated(order);
            }

            @Override
            public void onDeleted(PcbOrder order) {
                onOrderDeleted(order);
            }

            @Override
            public void onCacheCleared() {

            }
        };
    }

    private CacheChangedListener<ItemOrderLine> createItemOrderLineListener() {
        return new CacheChangedListener<ItemOrderLine>() {
            @Override
            public void onInserted(ItemOrderLine line) {
                onLineAdded(line);
            }

            @Override
            public void onUpdated(ItemOrderLine line) {
                onLineUpdated(line);
            }

            @Override
            public void onDeleted(ItemOrderLine line) {
                onLineDeleted(line);
            }

            @Override
            public void onCacheCleared() {

            }
        };
    }

    private CacheChangedListener<PcbOrderLine> createPcbOrderLineListener() {
        return new CacheChangedListener<PcbOrderLine>() {
            @Override
            public void onInserted(PcbOrderLine line) {
                onLineAdded(line);
            }

            @Override
            public void onUpdated(PcbOrderLine line) {
                onLineUpdated(line);
            }

            @Override
            public void onDeleted(PcbOrderLine line) {
                onLineDeleted(line);
            }

            @Override
            public void onCacheCleared() {

            }
        };
    }

    private CacheChangedListener<DistributorPartLink> createDistributorPartLinkListener() {
        return new CacheChangedListener<DistributorPartLink>() {
            @Override
            public void onInserted(DistributorPartLink object) {

            }

            @Override
            public void onUpdated(DistributorPartLink object) {
                lineTableUpdate();
                lineDetailsPanel.updateComponents(selectedOrderLine);
            }

            @Override
            public void onDeleted(DistributorPartLink object) {

            }

            @Override
            public void onCacheCleared() {

            }
        };
    }


    private void onOrderAdded(AbstractOrder order) {
        treeReload();
        orderTableAdd(order);
        SwingUtilities.invokeLater(() -> {
            selectedOrder = order;
            treeSelectOrder(order);
            orderTableSelect(order);
        });
    }

    private void onOrderUpdated(AbstractOrder order) {
        treeReload();
        orderTableUpdate();
        SwingUtilities.invokeLater(() -> {
            selectedOrder = order;
            treeSelectOrder(order);
        });
    }

    private void onOrderDeleted(AbstractOrder order) {
        if (order != null) {
            order.updateLineStates();
        }
        orderTableRemove(order);
        treeReload();
        onOrderSelected(null);
    }


    private void onLineAdded(AbstractOrderLine line) {
        if (line != null) {
            line.updateOrderState();
            AbstractOrder order = line.getOrder();
            if (order != null) {
                order.updateOrderLines();
            }
            onOrderSelected(order);
            SwingUtilities.invokeLater(() -> lineTableSelect(line));
        }
    }

    private void onLineUpdated(AbstractOrderLine line) {
        lineTableUpdate();
        lineDetailsPanel.updateComponents(line);
        if (line != null) {
            line.updateOrderState();
        }
    }

    private void onLineDeleted(AbstractOrderLine line) {
        if (line != null) {
            line.updateOrderState();
            AbstractOrder order = line.getOrder();
            if (order != null) {
                order.updateOrderLines();
            }
            onOrderSelected(order);
        }
    }


    private void onAddOrder() {
        OrderSearchItemsDialog dialog = new OrderSearchItemsDialog(application);
        dialog.showDialog();
    }

    private void onEditOrder(AbstractOrder order) {

    }

    private void onDeleteOrder(AbstractOrder order) {
        if (order != null) {
            int res = JOptionPane.showConfirmDialog(
                    application,
                    "Do you want to delete order '" + order + "'?",
                    "Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (res == JOptionPane.YES_OPTION) {
                order.delete();
            }
        }
    }

    private void onLockOrder(AbstractOrder order, boolean locked) {

    }


    private void onAddOrderLines() {

    }

    private void onDeleteOrderLines(List<AbstractOrderLine> orderLineList) {
        if (orderLineList != null && orderLineList.size() > 0) {
            String msg;
            if (orderLineList.size() == 1) {
                msg = "Delete " + orderLineList.get(0).getLine() + " from order?";
            } else {
                msg = "Delete " + orderLineList.size() + " lines from order?";
            }

            int res = JOptionPane.showConfirmDialog(
                    application,
                    msg,
                    "Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (res == JOptionPane.YES_OPTION) {
                for (AbstractOrderLine line : orderLineList) {
                    line.delete();
                }
            }
        }
    }

    private void onEditOrderLine(AbstractOrderLine orderLine) {
        if (orderLine != null) {
            if (orderLine.getLine() instanceof Item) {
                EditItemDialog dialog = new EditItemDialog<>(application, "Item", (Item) orderLine.getLine());
                dialog.showDialog();
            } else {
                // TODO pcb dialog
            }
        }
    }

    private void onEditLineReferenceAndPrice(AbstractOrderLine orderLine) {
        if (orderLine != null && !orderLine.isLocked()) {
            DistributorPartLink link = orderLine.getDistributorPartLink();
            if (link == null) {
                long id = orderLine.getLineId();
                link = new DistributorPartLink(orderLine.getOrder().getDistributor(), id);
            }
            EditDistributorPartLinkDialog dialog = new EditDistributorPartLinkDialog(application, link);
            dialog.enableDistributor(false);
            if (dialog.showDialog() == IDialog.OK) {
                link.save();
            }
        }
    }


    // Tool bars
    @Override
    public void onToolBarRefresh(IdBToolBar source) {

    }

    @Override
    public void onToolBarAdd(IdBToolBar source) {
        onAddOrder();
    }

    @Override
    public void onToolBarDelete(IdBToolBar source) {
        onDeleteOrder(selectedOrder);
    }

    @Override
    public void onToolBarEdit(IdBToolBar source) {
        onEditOrder(selectedOrder);
    }


    // Selection
    @Override
    void onOrderSelected(AbstractOrder order) {
        selectedOrder = order;
        selectedOrderLine = null;
        // Select order
        if (order != null) {
            order.updateOrderLines();
            orderTableSelect(order);
            lineTableInitialize(order);

            // Select line
            if (order.getOrderLines().size() > 0) {
                SwingUtilities.invokeLater(() -> {
                    AbstractOrderLine line = (AbstractOrderLine) order.getOrderLines().get(0);
                    lineTableSelect(line);
                });
            }

        } else {
            orderTableClear();
            lineTableClear();
        }
        orderDetailsPanel.updateComponents(order);
        updateEnabledComponents();
    }

    @Override
    void onLinesSelected(List<AbstractOrderLine> lineList) {
        if (lineList != null) {
            if (lineList.size() == 1) {
                selectedOrderLine = lineList.get(0);
                lineDetailsPanel.updateComponents(lineList.get(0));
            } else {
                selectedOrderLine = null;
                lineDetailsPanel.updateComponents((AbstractOrderLine)null);
            }
            updateEnabledComponents();
        }
    }

    @Override
    void onTreeSelected(Statics.OrderStates states, int year) {
        //selectedOrder = null;
        orderDetailsPanel.updateComponents();
        lineDetailsPanel.updateComponents();
        orderTableInitialize(states, year);
        updateEnabledComponents();
    }


    // Click

    @Override
    void onOrderDoubleClick(MouseEvent e) {
        if (selectedOrder != null) {

        }
    }

    @Override
    void onOrderRightClick(MouseEvent e) {
        if (selectedOrder != null) {
            OrderPopup popup = new OrderPopup(selectedOrder) {
                @Override
                public void onEditOrder(AbstractOrder order) {
                    OrdersPanel.this.onEditOrder(order);
                }

                @Override
                public void onDeleteOrder(AbstractOrder order) {
                    OrdersPanel.this.onDeleteOrder(order);
                }

                @Override
                public void onLocked(AbstractOrder order, boolean locked) {
                    OrdersPanel.this.onLockOrder(order, locked);
                }
            };
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    void onLinesRightClick(MouseEvent e) {
        final List<AbstractOrderLine> orderLineList = lineTableGetAllSelected();
        if (orderLineList != null && orderLineList.size() > 0) {
            OrderLinePopup popup = new OrderLinePopup(orderLineList) {
                @Override
                public void onDeleteOrderLines(List<AbstractOrderLine> orderLineList) {
                    OrdersPanel.this.onDeleteOrderLines(orderLineList);
                }

                @Override
                public void onOrderOrderLines(List<AbstractOrderLine> orderLineList) {

                }

                @Override
                public void onEditReference(AbstractOrderLine orderLine) {
                    onEditLineReferenceAndPrice(orderLine);
                }

                @Override
                public void onEditLine(AbstractOrderLine orderLine) {
                    onEditOrderLine(orderLine);
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
                public void onShowHistory(Item item) {
                    application.showHistory(item);
                }
            };
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}

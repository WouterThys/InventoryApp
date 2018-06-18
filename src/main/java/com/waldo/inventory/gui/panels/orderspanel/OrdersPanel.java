package com.waldo.inventory.gui.panels.orderspanel;

import com.waldo.inventory.Utils.Statics;
import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IdBToolBar;
import com.waldo.inventory.gui.components.popups.OrderPopup;

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

        updateComponents((Object) null);
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
        orderTableAdd(order);
        onOrderSelected(order);
    }

    private void onOrderUpdated(AbstractOrder order) {
        onOrderSelected(order);
    }

    private void onOrderDeleted(AbstractOrder order) {
        orderTableRemove(order);
        onOrderSelected(null);
    }


    private void onLineAdded(AbstractOrderLine line) {
        if (line != null) {
            AbstractOrder order = line.getOrder();
            if (order != null) {
                order.updateOrderLines();
            }
            onOrderSelected(order);
        }
    }

    private void onLineUpdated(AbstractOrderLine line) {
        lineTableUpdate();
        lineDetailsPanel.updateComponents(line);
    }

    private void onLineDeleted(AbstractOrderLine line) {
        if (line != null) {
            AbstractOrder order = line.getOrder();
            if (order != null) {
                order.updateOrderLines();
            }
            onOrderSelected(order);
        }
    }


    private void onAddOrder() {

    }

    private void onEditOrder(AbstractOrder order) {

    }

    private void onDeleteOrder(AbstractOrder order) {

    }

    private void onLockOrder(AbstractOrder order, boolean locked) {

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
        if (order != null) {
            orderTableSelect(order);
            lineTableInitialize(order);
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
        selectedOrder = null;
        orderDetailsPanel.updateComponents();
        lineDetailsPanel.updateComponents();
        orderTableInitialize(states, year);
        updateEnabledComponents();
    }


    // Click
    @Override
    void onTreeRightClick(MouseEvent e) {

    }

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
        List<AbstractOrderLine> orderLineList = lineTableGetAllSelected();
        if (orderLineList != null && orderLineList.size() > 0) {

        }
    }
}

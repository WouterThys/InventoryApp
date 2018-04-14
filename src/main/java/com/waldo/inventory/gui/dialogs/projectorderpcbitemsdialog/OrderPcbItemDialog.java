package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.classes.dbclasses.*;
import com.waldo.inventory.database.interfaces.CacheChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.addtoorderdialog.AddToOrderDialog;
import com.waldo.utils.icomponents.IDialog;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class OrderPcbItemDialog extends OrderPcbItemDialogLayout implements CacheChangedListener<Order> {

    private final Application application;

    public OrderPcbItemDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);
        this.application = application;
        selectedPcb = projectPcb;

        initializeComponents();
        initializeLayouts();

        addCacheListener(Order.class, this);
    }


    private void selectOrder() {
        Order order;
        AddToOrderDialog orderItemDialog = new AddToOrderDialog(application, "Order", new ArrayList<>(), false, true);
        if (orderItemDialog.showDialog() == IDialog.OK) {
            order = orderItemDialog.getSelectedOrder();
            updateComponents(selectedPcb, order);
        }
    }

    private void removeUnorderedItems() {
        for (PcbItemProjectLink link : pcbItemPnl.pcbTableGetItemList()) {
            PcbItem pcbItem = link.getPcbItem();
            if (pcbItem.isOrdered()) {
                OrderLine orderItem = pcbItem.getOrderItem();
                if (orderItem.getId() < DbObject.UNKNOWN_ID) {
                    // Remove it from order
                    orderItem.getOrder().removeOrderLine(orderItem);
                    // Remove from pcb item
                    pcbItem.setOrderAmount(0);
                    pcbItem.setOrderLine(null);
                }
            }
        }
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        removeUnorderedItems();
        super.onOK();
    }

    @Override
    protected void onCancel() {
        removeUnorderedItems();
        super.onCancel();
    }

    @Override
    public void windowOpened(WindowEvent e) {
        super.windowOpened(e);
        selectOrder();
    }

    //
    // Left panel listeners
    //
    @Override
    public void onDoOrder() {
        if (selectedOrder != null) {
            application.addOrderItemsToOrder(selectedOrder.getTempOrderItems(), selectedOrder);

            selectedOrder.clearTempOrderList();

            selectedOrder.save();
        }
    }

    //
    // Order changed
    //
    @Override
    public void onInserted(Order order) {
        SwingUtilities.invokeLater(() -> orderPnl.orderTableUpdate());
    }

    @Override
    public void onUpdated(Order order) {
        SwingUtilities.invokeLater(() -> orderPnl.orderTableUpdate());
    }

    @Override
    public void onDeleted(Order order) {
        // Should not happen
    }

    @Override
    public void onCacheCleared() {
        // Ignore
    }

    //
    // Right panel listeners
    //
    @Override
    public void onAdd() {
        if (selectedOrder == null || selectedOrder.isUnknown()) {
            selectOrder();
        } else {
            pcbItemPnl.createOrderItems(selectedOrder);
            orderPnl.updateComponents(selectedOrder);
        }
    }

    @Override
    ActionListener onChangeOrder() {
        return e -> selectOrder();
    }
}
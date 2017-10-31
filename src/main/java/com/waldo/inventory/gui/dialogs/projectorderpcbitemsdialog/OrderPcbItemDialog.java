package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.classes.*;
import com.waldo.inventory.database.DbManager;
import com.waldo.inventory.database.interfaces.DbObjectChangedListener;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class OrderPcbItemDialog extends OrderPcbItemDialogLayout implements DbObjectChangedListener<Order> {

    public OrderPcbItemDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);

        selectedPcb = projectPcb;

        initializeComponents();
        initializeLayouts();

        DbManager.db().addOnOrdersChangedListener(this);
    }


    private void selectOrder() {
        Order order;
        OrderItemDialog orderItemDialog = new OrderItemDialog(application, "Order", new ArrayList<>(), false);
        if (orderItemDialog.showDialog() == IDialog.OK) {
            order = orderItemDialog.getSelectedOrder();
            updateComponents(selectedPcb, order);
        }
    }

    private void removeUnorderedItems() {
        for (PcbItem pcbItem : pcbItemPnl.pcbTableGetItemList()) {
            if (pcbItem.isOrdered()) {
                OrderItem orderItem = pcbItem.getOrderItem();
                if (orderItem.getId() < DbObject.UNKNOWN_ID) {
                    // Remove it from order
                    orderItem.getOrder().removeItemFromList(orderItem);
                    // Remove from pcb item
                    pcbItem.setOrderAmount(0);
                    pcbItem.setOrderItem(null);
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
//        for (OrderItem orderItem : order.getOrderItems()) {
//            orderItem.save();
//        }
        SwingUtilities.invokeLater(() -> orderPnl.orderTableUpdate());
    }

    @Override
    public void onUpdated(Order order) {
//        for (OrderItem orderItem : order.getOrderItems()) {
//            orderItem.save();
//        }
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
    public void onAddToOrder() {
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
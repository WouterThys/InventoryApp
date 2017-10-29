package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.classes.Order;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.components.IDialog;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class OrderPcbItemDialog extends OrderPcbItemDialogLayout {

    public OrderPcbItemDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);

        selectedPcb = projectPcb;

        initializeComponents();
        initializeLayouts();
    }


    private void selectOrder() {
        Order order;
        OrderItemDialog orderItemDialog = new OrderItemDialog(application, "Order", new ArrayList<>(), false);
        if (orderItemDialog.showDialog() == IDialog.OK) {
            order = orderItemDialog.getSelectedOrder();
            updateComponents(selectedPcb, order);
        }
    }

    //
    // Dialog
    //
    @Override
    protected void onOK() {
        super.onOK();
    }

    @Override
    public void windowOpened(WindowEvent e) {
        super.windowOpened(e);
        selectOrder();
    }

    //
    // Button clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
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
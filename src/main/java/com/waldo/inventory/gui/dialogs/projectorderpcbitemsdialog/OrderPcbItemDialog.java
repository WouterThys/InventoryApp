package com.waldo.inventory.gui.dialogs.projectorderpcbitemsdialog;

import com.waldo.inventory.classes.OrderItem;
import com.waldo.inventory.classes.ProjectPcb;
import com.waldo.inventory.gui.Application;
import com.waldo.inventory.gui.dialogs.orderitemdialog.OrderItemDialog;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.List;

public class OrderPcbItemDialog extends OrderPcbItemDialogLayout {

    public OrderPcbItemDialog(Application application, String title, ProjectPcb projectPcb) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(projectPcb);

    }


    //
    // Dialog
    //
    @Override
    protected void onOK() {
        super.onOK();
    }

    @Override
    public void windowActivated(WindowEvent e) {
        super.windowActivated(e);

        OrderItemDialog dialog = new OrderItemDialog(application, "Order " + item.getName(), item, true);
        dialog.showDialog();
    }

    //
    // Button clicked
    //
    @Override
    public void actionPerformed(ActionEvent e) {
        List<OrderItem> orderItems = pcbItemPnl.createOrderItems(order);
    }


}
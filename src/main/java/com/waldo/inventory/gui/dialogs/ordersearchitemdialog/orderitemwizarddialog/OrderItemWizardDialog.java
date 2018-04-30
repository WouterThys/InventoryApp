package com.waldo.inventory.gui.dialogs.ordersearchitemdialog.orderitemwizarddialog;

import com.waldo.inventory.classes.dbclasses.DistributorPartLink;
import com.waldo.inventory.classes.dbclasses.Item;
import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.managers.SearchManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class OrderItemWizardDialog extends OrderItemWizardDialogLayout {

    private List<Item> itemsToOrder = new ArrayList<>();

    public OrderItemWizardDialog(Window window, Order order) {
        super(window, order);

        initializeComponents();
        initializeLayouts();
        updateComponents();

        orderCb.setEnabled(order == null);
    }

    public List<Item> getItemsToOrder() {
        return itemsToOrder;
    }


    @Override
    protected void onOK() {
        if (selectedOrder == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an order..",
                    "No order",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean emptyReference = isAllowEmptyReference();
        boolean lessThan = isAvailableLessThanMinimum();
        boolean pcb = isFromPcb();

        itemsToOrder.clear();
        List<Item> itemList = SearchManager.sm().findItemsToOrder();
        if (lessThan) {
            for (Item item : itemList) {
                DistributorPartLink link = SearchManager.sm().findDistributorPartLink(selectedOrder.getDistributorId(), item);
                if (link != null || emptyReference) {
                    itemsToOrder.add(item);
                }
            }
        }

        if (pcb) {

        }

        super.onOK();
    }
}
package com.waldo.inventory.gui.dialogs.ordersearchitemdialog;

import com.waldo.inventory.classes.dbclasses.Order;
import com.waldo.inventory.gui.Application;

public class OrderSearchItemsDialog extends OrderSearchItemsDialogLayout {


    public OrderSearchItemsDialog(Application application, Order order) {
        super(application, order);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}

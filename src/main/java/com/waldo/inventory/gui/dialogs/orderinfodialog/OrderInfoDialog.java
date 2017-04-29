package com.waldo.inventory.gui.dialogs.orderinfodialog;

import com.waldo.inventory.classes.OrderFile;
import com.waldo.inventory.gui.Application;

public class OrderInfoDialog extends OrderInfoDialogLayout {

    public OrderInfoDialog(Application application, String title, OrderFile orderFile) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(orderFile);
    }
}

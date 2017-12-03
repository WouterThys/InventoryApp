package com.waldo.inventory.gui.dialogs.ordereditreferencedialog;

import com.waldo.inventory.classes.dbclasses.OrderItem;
import com.waldo.inventory.gui.Application;

public class OrderEditReferenceDialog extends OrderEditReferenceDialogLayout {


    public OrderEditReferenceDialog(Application application, String title, OrderItem orderItem) {
        super(application, title);

        initializeComponents();
        initializeLayouts();
        updateComponents(orderItem);

    }

}
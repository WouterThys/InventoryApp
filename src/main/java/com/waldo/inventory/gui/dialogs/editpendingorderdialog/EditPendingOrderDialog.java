package com.waldo.inventory.gui.dialogs.editpendingorderdialog;

import com.waldo.inventory.classes.dbclasses.PendingOrder;

import java.awt.*;

public class EditPendingOrderDialog extends EditPendingOrderDialogLayout {


    public EditPendingOrderDialog(Window window, String title, PendingOrder pendingOrder) {
        super(window, title, pendingOrder);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }

}

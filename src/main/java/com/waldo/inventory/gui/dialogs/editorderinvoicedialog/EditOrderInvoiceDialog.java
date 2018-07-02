package com.waldo.inventory.gui.dialogs.editorderinvoicedialog;

import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.inventory.classes.dbclasses.AbstractOrderLine;

import java.awt.*;

public class EditOrderInvoiceDialog extends EditOrderInvoiceDialogLayout {


    public EditOrderInvoiceDialog(Window window, AbstractOrder order, AbstractOrderLine orderLine) {
        super(window, order, orderLine);

        initializeComponents();
        initializeLayouts();
        updateComponents();

    }


    @Override
    public void onValueChanged(Component component, String s, Object o, Object o1) {
        getButtonNeutral().setEnabled(!originalOrder.equals(selectedOrder));
        updatePriceFields();
    }

    @Override
    public AbstractOrder getGuiObject() {
        if (isShown) {
            return selectedOrder;
        }
        return null;
    }
}

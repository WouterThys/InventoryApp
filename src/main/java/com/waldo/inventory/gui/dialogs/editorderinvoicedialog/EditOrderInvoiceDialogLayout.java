package com.waldo.inventory.gui.dialogs.editorderinvoicedialog;

import com.waldo.inventory.classes.dbclasses.AbstractOrder;
import com.waldo.inventory.classes.dbclasses.AbstractOrderLine;
import com.waldo.inventory.gui.components.iDialog;
import com.waldo.utils.icomponents.ITextField;

import javax.swing.*;
import java.awt.*;

abstract class EditOrderInvoiceDialogLayout extends iDialog {

    /*
     *                  COMPONENTS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Order
    private ITextField vatTf;
    private ITextField priceIncTf;
    private ITextField priceExcTf;

    // Lines

    // Order text

    /*
     *                  VARIABLES
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    final AbstractOrder selectedOrder;
    AbstractOrderLine selectedOrderLine;

    /*
     *                  CONSTRUCTOR
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    EditOrderInvoiceDialogLayout(Window window, AbstractOrder order, AbstractOrderLine orderLine) {
        super(window, "Invoice");

        this.selectedOrder = order;
        this.selectedOrderLine = orderLine;
    }

    /*
     *                   METHODS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    /*
     *                  LISTENERS
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Override
    public void initializeComponents() {

    }

    @Override
    public void initializeLayouts() {

        JTabbedPane tabbedPane = new JTabbedPane();
        //..
        pack();
    }

    @Override
    public void updateComponents(Object... args) {
        if (selectedOrderLine != null) {

        }
    }
}
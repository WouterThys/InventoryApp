package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OrderDetailsAction extends AbstractAction {

    private static final String name = "Order details";

    protected OrderDetailsAction() {
        super(name, imageResource.readImage("Actions.OrderDetails"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onOrderDetails();

    @Override
    public void actionPerformed(ActionEvent e) {
        onOrderDetails();
    }
}

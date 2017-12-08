package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OrderDetailsAction extends AbstractAction {

    private static final String name = "Order details";

    public OrderDetailsAction() {
        super(name, imageResource.readImage("Actions.OrderDetails"));
    }

    public abstract void onOrderDetails();

    @Override
    public void actionPerformed(ActionEvent e) {
        onOrderDetails();
    }
}

package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class OrderItemAction extends AbstractAction {

    private static final String name = "Order item";

    public OrderItemAction() {
        super(name, imageResource.readImage("Actions.ItemOrder"));
    }

    public abstract void onOrderItem();

    @Override
    public void actionPerformed(ActionEvent e) {
        onOrderItem();
    }
}

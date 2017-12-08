package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class DeleteOrderItemAction extends AbstractAction {

    private static final String name = "Delete order item";

    public DeleteOrderItemAction() {
        super(name, imageResource.readImage("Actions.OrderItemDelete"));
    }

    public abstract void onDeleteOrderItem();

    @Override
    public void actionPerformed(ActionEvent e) {
        onDeleteOrderItem();
    }
}
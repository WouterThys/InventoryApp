package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class AddOrderAction extends AbstractAction {

    private static final String name = "Add order";

    public AddOrderAction() {
        super(name, imageResource.readImage("Actions.OrderAdd"));
    }

    public abstract void onAddOrder();

    @Override
    public void actionPerformed(ActionEvent e) {
        onAddOrder();
    }
}

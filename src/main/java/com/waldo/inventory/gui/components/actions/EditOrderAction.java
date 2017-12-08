package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditOrderAction  extends AbstractAction {

    private static final String name = "Edit order";

    protected EditOrderAction() {
        super(name, imageResource.readImage("Actions.OrderEdit"));
    }

    public abstract void onEditOrder();

    @Override
    public void actionPerformed(ActionEvent e) {
        onEditOrder();
    }
}

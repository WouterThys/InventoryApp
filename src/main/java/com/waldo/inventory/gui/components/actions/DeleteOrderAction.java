package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class DeleteOrderAction extends AbstractAction {

    private static final String name = "Delete order";

    protected DeleteOrderAction() {
        super(name, imageResource.readImage("Actions.OrderDelete"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onDeleteOrder();

    @Override
    public void actionPerformed(ActionEvent e) {
        onDeleteOrder();
    }
}

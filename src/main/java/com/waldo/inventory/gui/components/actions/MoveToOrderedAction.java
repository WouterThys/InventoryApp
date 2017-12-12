package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class MoveToOrderedAction extends AbstractAction {

    private static final String name = "To Ordered";

    protected MoveToOrderedAction() {
        super(name, imageResource.readImage("Actions.OrderMoveToOrdered"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onMoveToOrdered();

    @Override
    public void actionPerformed(ActionEvent e) {
        onMoveToOrdered();
    }
}

package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class BackToOrderedAction extends AbstractAction {

    private static final String name = "Back to ordered";

    protected BackToOrderedAction() {
        super(name, imageResource.readImage("Actions.OrderBackToOrdered"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onBackToOrdered();

    @Override
    public void actionPerformed(ActionEvent e) {
        onBackToOrdered();
    }
}

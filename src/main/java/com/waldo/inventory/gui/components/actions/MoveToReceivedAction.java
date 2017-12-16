package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class MoveToReceivedAction extends AbstractAction {

    private static final String name = "To received";

    protected MoveToReceivedAction() {
        super(name, imageResource.readImage("Actions.OrderMoveToReceived"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onMoveToReceived();

    @Override
    public void actionPerformed(ActionEvent e) {
        onMoveToReceived();
    }
}

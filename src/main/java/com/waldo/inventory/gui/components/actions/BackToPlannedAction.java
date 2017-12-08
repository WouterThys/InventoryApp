package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class BackToPlannedAction extends AbstractAction {

    private static final String name = "Back to planned";

    protected BackToPlannedAction() {
        super(name, imageResource.readImage("Actions.OrderBackToPlanned"));
    }

    public abstract void onBackToPlanned();

    @Override
    public void actionPerformed(ActionEvent e) {
        onBackToPlanned();
    }
}

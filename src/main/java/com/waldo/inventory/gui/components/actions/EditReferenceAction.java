package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditReferenceAction extends AbstractAction {

    private static final String name = "Edit reference";

    protected EditReferenceAction() {
        super(name, imageResource.readImage("Actions.OrderReference"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onEditReference();

    @Override
    public void actionPerformed(ActionEvent e) {
        onEditReference();
    }
}

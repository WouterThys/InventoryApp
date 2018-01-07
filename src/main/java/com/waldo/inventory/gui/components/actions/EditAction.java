package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditAction extends AbstractAction {

    private static final String name = "Edit";

    protected EditAction() {
        super(name, imageResource.readImage("Actions.ItemEdit"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onEdit();

    @Override
    public void actionPerformed(ActionEvent e) {
        onEdit();
    }
}


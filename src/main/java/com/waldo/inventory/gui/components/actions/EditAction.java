package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class EditAction extends AbstractAction {

    private static final String name = "Edit";

    protected EditAction() {
        super(name, imageResource.readImage("Actions.Edit"));
    }

    protected EditAction(String name) {
        super(name, imageResource.readImage("Actions.Edit"));
    }

    public void setName(String name) {
        putValue(AbstractAction.NAME, name);
    }

    public abstract void onEdit(ActionEvent e);

    @Override
    public void actionPerformed(ActionEvent e) {
        onEdit(e);
    }
}


package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class DeleteAction extends AbstractAction {

    private static final String name = "Delete";

    public DeleteAction() {
        super(name, imageResource.readImage("Actions.Delete"));
    }

    public DeleteAction(String name) {
        super(name, imageResource.readImage("Actions.Delete"));
    }

    public void setName(String name) {
        putValue(AbstractAction.NAME, name);
    }

    public abstract void onDelete();

    @Override
    public void actionPerformed(ActionEvent e) {
        onDelete();
    }
}

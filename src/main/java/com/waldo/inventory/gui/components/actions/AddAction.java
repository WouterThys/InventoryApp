package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class AddAction extends AbstractAction {

    private static final String name = "Add";

    protected AddAction() {
        super(name, imageResource.readImage("Actions.Add"));
    }

    protected AddAction(String name) {
        super(name, imageResource.readImage("Actions.Add"));
    }

    public void setName(String name) {
        putValue(AbstractAction.NAME, name);
    }

    public abstract void onAdd();

    @Override
    public void actionPerformed(ActionEvent e) {
        onAdd();
    }
}

package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class SaveAction extends AbstractAction {

    private static final String name = "Save";

    protected SaveAction() {
        super(name, imageResource.readImage("Actions.Save"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onSave(ActionEvent e);

    @Override
    public void actionPerformed(ActionEvent e) {
        onSave(e);
    }
}


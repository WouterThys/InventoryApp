package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class RenameAction extends AbstractAction {

    private static final String name = "Rename";

    protected RenameAction() {
        super(name, imageResource.readImage("Actions.M.Rename"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onRename();

    @Override
    public void actionPerformed(ActionEvent e) {
        onRename();
    }
}


package com.waldo.inventory.gui.components.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static com.waldo.inventory.gui.Application.imageResource;

public abstract class ReplaceAction extends AbstractAction {

    private static final String name = "Replace";

    protected ReplaceAction() {
        super(name, imageResource.readImage("Actions.M.Replace"));
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
    }

    public abstract void onReplace();

    @Override
    public void actionPerformed(ActionEvent e) {
        onReplace();
    }
}

